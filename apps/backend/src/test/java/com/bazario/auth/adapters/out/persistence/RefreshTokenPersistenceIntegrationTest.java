package com.bazario.auth.adapters.out.persistence;

import com.bazario.BazarioApplication;
import com.bazario.auth.domain.model.RefreshToken;
import com.bazario.auth.domain.service.JwtService;
import com.bazario.auth.domain.service.RefreshTokenService;
import com.bazario.auth.ports.out.RefreshTokenRepositoryPort;
import com.bazario.customers.adapters.out.persistence.SpringDataCustomerRepository;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.out.CustomerRepositoryPort;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = BazarioApplication.class,
        properties = {
                "security.jwt.secret=AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=",
                "spring.datasource.url=${TEST_DB_URL:jdbc:postgresql://localhost:5432/bazario_dev_db}",
                "spring.datasource.username=${TEST_DB_USERNAME:postgres}",
                "spring.datasource.password=${TEST_DB_PASSWORD:postgres}"
        })
class RefreshTokenPersistenceIntegrationTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private CustomerRepositoryPort customerRepository;

    @Autowired
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private Environment environment;

    @Autowired
    private SpringDataCustomerRepository springDataCustomerRepository;

    private UUID customerId;

    @AfterEach
    void removeCustomer() {
        if (customerId != null) {
            springDataCustomerRepository.deleteById(customerId);
        }
    }

    @Test
    void refreshStateIsStoredInPostgresAndVisibleOnFreshConnection() throws Exception {
        Customer customer = createCustomer();
        String refreshToken = refreshTokenService.issue(customer, false);
        String tokenId = jwtService.tryParse(refreshToken, JwtService.TokenType.REFRESH)
                .orElseThrow().getPayload().getId();
        String tokenIdHash = hashTokenId(tokenId);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT token_id_hash, customer_id, family_id, state FROM auth_refresh_tokens WHERE token_id_hash = ?")) {
            statement.setString(1, tokenIdHash);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertThat(resultSet.next()).isTrue();
                assertThat(resultSet.getString("token_id_hash")).isEqualTo(tokenIdHash);
                assertThat(UUID.fromString(resultSet.getString("customer_id"))).isEqualTo(customerId);
                assertThat(resultSet.getString("family_id")).isNotBlank();
                assertThat(resultSet.getString("state")).isEqualTo(RefreshToken.State.ACTIVE.name());
                assertThat(resultSet.next()).isFalse();
            }
        }
    }

    @Test
    void refreshTokenSurvivesANewApplicationInstance() {
        Customer customer = createCustomer();
        String token = refreshTokenService.issue(customer, false);

        try (var newApplication = new SpringApplicationBuilder(BazarioApplication.class)
                .web(WebApplicationType.SERVLET)
                .run("--server.port=0",
                        "--spring.profiles.active=dev",
                        "--security.jwt.secret=AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=",
                        "--spring.datasource.url=" + environment.getRequiredProperty("spring.datasource.url"),
                        "--spring.datasource.username=" + environment.getRequiredProperty("spring.datasource.username"),
                        "--spring.datasource.password=" + environment.getRequiredProperty("spring.datasource.password"))) {
            var result = newApplication.getBean(RefreshTokenService.class).rotate(token);

            assertThat(result.status()).isEqualTo(RefreshTokenService.RotationStatus.ROTATED);
            assertThat(jwtService.tryParse(result.refreshToken(), JwtService.TokenType.REFRESH)).isPresent();
        }
    }

    @Test
    void concurrentRefreshAndReuseRevokeTheTokenFamily() throws Exception {
        Customer customer = createCustomer();
        String originalToken = refreshTokenService.issue(customer, false);
        String originalTokenId = jwtService.tryParse(originalToken, JwtService.TokenType.REFRESH)
                .orElseThrow().getPayload().getId();
        String originalHash = hashTokenId(originalTokenId);
        var initialRecord = refreshTokenRepository.findByTokenIdHash(originalHash).orElseThrow();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try {
            List<Future<RefreshTokenService.RotationResult>> attempts = new ArrayList<>();
            for (int index = 0; index < 2; index++) {
                attempts.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Concurrent refresh test did not start");
                    }
                    return refreshTokenService.rotate(originalToken);
                }));
            }

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            List<RefreshTokenService.RotationResult> results = attempts.stream()
                    .map(this::getResult)
                    .toList();

            assertThat(results).extracting(RefreshTokenService.RotationResult::status)
                    .containsExactlyInAnyOrder(
                            RefreshTokenService.RotationStatus.ROTATED,
                            RefreshTokenService.RotationStatus.REUSED);
            assertThat(refreshTokenRepository.findByTokenIdHash(originalHash).orElseThrow().state())
                    .isEqualTo(RefreshToken.State.REVOKED);

            String replacementToken = results.stream()
                    .filter(result -> result.status() == RefreshTokenService.RotationStatus.ROTATED)
                    .findFirst().orElseThrow().refreshToken();
            String replacementId = jwtService.tryParse(replacementToken, JwtService.TokenType.REFRESH)
                    .orElseThrow().getPayload().getId();
            RefreshToken replacement = refreshTokenRepository.findByTokenIdHash(
                    hashTokenId(replacementId)).orElseThrow();
            assertThat(replacement.familyId()).isEqualTo(initialRecord.familyId());
            assertThat(replacement.state()).isEqualTo(RefreshToken.State.REVOKED);
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    private Customer createCustomer() {
        Customer customer = customerRepository.save(Customer.builder()
                .email("refresh-" + UUID.randomUUID() + "@example.com")
                .passwordHash("integration-test-password-hash")
                .firstName("Refresh test")
                .enabled(true)
                .build());
        customerId = customer.getId();
        return customer;
    }

    private RefreshTokenService.RotationResult getResult(Future<RefreshTokenService.RotationResult> result) {
        try {
            return result.get(10, TimeUnit.SECONDS);
        } catch (Exception exception) {
            throw new IllegalStateException("Concurrent refresh attempt failed", exception);
        }
    }

    private String hashTokenId(String tokenId) throws NoSuchAlgorithmException {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(tokenId.getBytes(StandardCharsets.UTF_8)));
    }
}