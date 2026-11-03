package com.bazario.auth.adapters.in.web;

import com.bazario.BazarioApplication;
import com.bazario.auth.domain.service.JwtService;
import com.bazario.auth.domain.service.RefreshTokenService;
import com.bazario.auth.ports.out.PasswordResetNotifier;
import com.bazario.customers.adapters.out.persistence.SpringDataCustomerRepository;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.domain.model.CustomerAddress;
import com.bazario.customers.ports.in.CustomerUseCase;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = BazarioApplication.class,
        properties = {
                "security.jwt.secret=AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=",
                "spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://localhost:5432/bazario_dev_db}",
                "spring.datasource.username=${TEST_DB_USERNAME:postgres}",
                "spring.datasource.password=${TEST_DB_PASSWORD:postgres}"
        })
@AutoConfigureMockMvc
@Import(PasswordResetIntegrationTest.NotifierConfiguration.class)
class PasswordResetIntegrationTest {

    private static final String TEST_SECRET = "AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerUseCase customerUseCase;

    @Autowired
    private SpringDataCustomerRepository customerRepository;

    @Autowired
    private CapturingNotifier notifier;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    private final List<UUID> customerIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        customerIds.forEach(customerRepository::deleteById);
        notifier.clear();
    }

    @Test
    void forgotPasswordHasSameResponseForKnownAndUnknownEmails() throws Exception {
        Customer customer = createCustomer();
        MvcResult known = requestReset(customer.getEmail());
        assertThat(notifier.token()).isNotBlank();

        notifier.clear();
        MvcResult unknown = requestReset("unknown-" + UUID.randomUUID() + "@example.com");

        assertThat(unknown.getResponse().getContentAsString())
                .isEqualTo(known.getResponse().getContentAsString());
        assertThat(notifier.token()).isNull();
    }

    @Test
    void resetIsSingleUseEncodesPasswordAndPreservesAddresses() throws Exception {
        Customer customer = createCustomer();
        CustomerAddress address = customerUseCase.addAddress(customer.getId(), CustomerAddress.builder()
                .addressLine1("123 Test Street")
                .city("Dhaka")
                .postalCode("1200")
                .country("Bangladesh")
                .build());
        String token = requestToken(customer.getEmail());

        reset(token, "new-password-123").andExpect(status().isOk());
        reset(token, "another-password-123").andExpect(status().isBadRequest());

        Customer updated = customerUseCase.getCustomerById(customer.getId()).orElseThrow();
        assertThat(updated.getPasswordHash()).isNotEqualTo("new-password-123");
        assertThat(passwordEncoder.matches("new-password-123", updated.getPasswordHash())).isTrue();
        assertThat(passwordEncoder.matches("original-password", updated.getPasswordHash())).isFalse();
        assertThat(customerUseCase.getAddressesByCustomerId(customer.getId()))
                .extracting(CustomerAddress::getId).containsExactly(address.getId());

        login(customer.getEmail(), "new-password-123").andExpect(status().isOk());
        login(customer.getEmail(), "original-password").andExpect(status().isUnauthorized());
    }

    @Test
    void expiredInvalidAndNonResetTokensCannotChangePassword() throws Exception {
        Customer customer = createCustomer();
        var key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(TEST_SECRET));
        var pastJwt = new JwtService(key, Clock.offset(Clock.systemUTC(), Duration.ofHours(-1)));
        String expiredToken = pastJwt.generateResetToken(customer.getEmail(),
                fingerprint(customer.getPasswordHash()), Duration.ofMinutes(15));
        String accessToken = jwtService.generateAccessToken(customer.getEmail(),
                List.of("ROLE_CUSTOMER"), Duration.ofHours(1));
        String refreshToken = refreshTokenService.issue(customer, false);

        for (String token : List.of(expiredToken, accessToken, refreshToken, "invalid-token")) {
            reset(token, "new-password-123").andExpect(status().isBadRequest());
        }
        assertThat(passwordEncoder.matches("original-password",
                customerUseCase.getCustomerById(customer.getId()).orElseThrow().getPasswordHash())).isTrue();
    }

    @Test
    void concurrentUseOfOneResetTokenChangesPasswordOnlyOnce() throws Exception {
        Customer customer = createCustomer();
        String token = requestToken(customer.getEmail());
        var executor = Executors.newFixedThreadPool(2);
        var ready = new CountDownLatch(2);
        var start = new CountDownLatch(1);
        try {
            List<Future<Integer>> attempts = new ArrayList<>();
            for (String password : List.of("first-new-password", "second-new-password")) {
                attempts.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Concurrent reset test did not start");
                    }
                    return reset(token, password).andReturn().getResponse().getStatus();
                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            assertThat(List.of(attempts.get(0).get(10, TimeUnit.SECONDS), attempts.get(1).get(10, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(200, 400);

            String storedHash = customerUseCase.getCustomerById(customer.getId()).orElseThrow().getPasswordHash();
            assertThat(passwordEncoder.matches("first-new-password", storedHash)
                    ^ passwordEncoder.matches("second-new-password", storedHash)).isTrue();
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    private Customer createCustomer() {
        UUID id = customerUseCase.registerCustomer(Customer.builder()
                .email("reset-" + UUID.randomUUID() + "@example.com")
                .firstName("Reset")
                .lastName("Test")
                .passwordHash("original-password")
                .build());
        customerIds.add(id);
        return customerUseCase.getCustomerById(id).orElseThrow();
    }

    private MvcResult requestReset(String email) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s" }
                                """.formatted(email)))
                .andExpect(status().isOk())
                .andReturn();
    }

    private String requestToken(String email) throws Exception {
        notifier.clear();
        requestReset(email);
        assertThat(notifier.token()).isNotBlank();
        return notifier.token();
    }

    private org.springframework.test.web.servlet.ResultActions reset(String token, String password) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "token": "%s", "newPassword": "%s" }
                        """.formatted(token, password)));
    }

    private org.springframework.test.web.servlet.ResultActions login(String email, String password) throws Exception {
        return mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "email": "%s", "password": "%s", "rememberMe": false }
                        """.formatted(email, password)));
    }

    private String fingerprint(String hash) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(hash.getBytes(StandardCharsets.UTF_8)));
    }

    @TestConfiguration
    static class NotifierConfiguration {

        @Bean
        @Primary
        CapturingNotifier capturingNotifier() {
            return new CapturingNotifier();
        }
    }

    static class CapturingNotifier implements PasswordResetNotifier {

        private final AtomicReference<String> token = new AtomicReference<>();

        @Override
        public void sendResetToken(String email, String resetToken) {
            token.set(resetToken);
        }

        String token() {
            return token.get();
        }

        void clear() {
            token.set(null);
        }
    }
}