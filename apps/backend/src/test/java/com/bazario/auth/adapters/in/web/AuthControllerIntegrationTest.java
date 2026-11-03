package com.bazario.auth.adapters.in.web;

import com.bazario.BazarioApplication;
import com.bazario.auth.adapters.in.web.dto.AuthDtos;
import com.bazario.auth.domain.service.JwtService;
import com.bazario.auth.domain.service.RefreshTokenService;
import com.bazario.customers.adapters.out.persistence.SpringDataCustomerRepository;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.in.CustomerUseCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerUseCase customerUseCase;

    @Autowired
    private SpringDataCustomerRepository customerRepository;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    private final List<UUID> customerIds = new ArrayList<>();

    @AfterEach
    void removeCustomers() {
        customerIds.forEach(customerRepository::deleteById);
    }

    @Test
    void registerReturnsUsableTokenPairAndDuplicateRegistrationConflicts() throws Exception {
        String email = email();
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Register",
                                  "lastName": "Test",
                                  "email": "%s",
                                  "password": "correct-password"
                                }
                                """.formatted(email)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andReturn();

        AuthDtos.AuthTokenResponse response = tokenResponse(result);
        customerIds.add(response.customer().id());
        assertTokenPair(response, email);
        assertThat(response.expiresIn()).isEqualTo(Duration.ofHours(1).toSeconds());
        assertThat(Duration.between(
                jwtService.tryParse(response.accessToken(), JwtService.TokenType.ACCESS)
                        .orElseThrow().getPayload().getIssuedAt().toInstant(),
                jwtService.tryParse(response.accessToken(), JwtService.TokenType.ACCESS)
                        .orElseThrow().getPayload().getExpiration().toInstant()).toSeconds())
                .isEqualTo(response.expiresIn());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "firstName": "Duplicate",
                                  "lastName": "Test",
                                  "email": "%s",
                                  "password": "correct-password"
                                }
                                """.formatted(email)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginReturnsRealTokensAndBadCredentialsAreUnauthorized() throws Exception {
        Customer customer = createCustomer("correct-password");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "correct-password", "rememberMe": false }
                                """.formatted(customer.getEmail())))
                .andExpect(status().isOk())
                .andReturn();

        AuthDtos.AuthTokenResponse response = tokenResponse(result);
        assertTokenPair(response, customer.getEmail());
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + response.accessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(customer.getEmail()));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "incorrect-password", "rememberMe": false }
                                """.formatted(customer.getEmail())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password."));
    }

    @Test
    void rememberMeExtendsOnlyTheRefreshTokenLifetime() throws Exception {
        Customer customer = createCustomer("correct-password");
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "email": "%s", "password": "correct-password", "rememberMe": true }
                                """.formatted(customer.getEmail())))
                .andExpect(status().isOk())
                .andReturn();

        AuthDtos.AuthTokenResponse response = tokenResponse(result);
        assertThat(response.expiresIn()).isEqualTo(Duration.ofHours(1).toSeconds());
        var refreshClaims = jwtService.tryParse(response.refreshToken(), JwtService.TokenType.REFRESH)
                .orElseThrow().getPayload();
        assertThat(Duration.between(refreshClaims.getIssuedAt().toInstant(), refreshClaims.getExpiration().toInstant()))
                .isEqualTo(Duration.ofDays(30));
    }

    @Test
    void refreshRotatesTokenAndReuseReturnsUnauthorized() throws Exception {
        Customer customer = createCustomer("correct-password");
        String originalRefreshToken = refreshTokenService.issue(customer, false);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "refreshToken": "%s" }
                                """.formatted(originalRefreshToken)))
                .andExpect(status().isOk())
                .andReturn();

        AuthDtos.AuthTokenResponse response = tokenResponse(result);
        assertTokenPair(response, customer.getEmail());
        assertThat(response.refreshToken()).isNotEqualTo(originalRefreshToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "refreshToken": "%s" }
                                """.formatted(originalRefreshToken)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "refreshToken": "%s" }
                                """.formatted(response.refreshToken())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshForDeletedCustomerReturnsUnauthorized() throws Exception {
        Customer customer = createCustomer("correct-password");
        String refreshToken = refreshTokenService.issue(customer, false);
        customerRepository.deleteById(customer.getId());
        customerIds.remove(customer.getId());

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "refreshToken": "%s" }
                                """.formatted(refreshToken)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidAndDisabledCustomerRefreshTokensAreUnauthorized() throws Exception {
        Customer customer = createCustomer("correct-password");
        String refreshToken = refreshTokenService.issue(customer, false);
        var entity = customerRepository.findById(customer.getId()).orElseThrow();
        entity.setEnabled(false);
        customerRepository.save(entity);

        for (String token : List.of("invalid-token", refreshToken)) {
            mockMvc.perform(post("/api/v1/auth/refresh")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    { "refreshToken": "%s" }
                                    """.formatted(token)))
                    .andExpect(status().isUnauthorized());
        }
    }

    private Customer createCustomer(String password) {
        UUID id = customerUseCase.registerCustomer(Customer.builder()
                .email(email())
                .firstName("Auth")
                .lastName("Test")
                .passwordHash(password)
                .build());
        customerIds.add(id);
        return customerUseCase.getCustomerById(id).orElseThrow();
    }

    private String email() {
        return "auth-" + UUID.randomUUID() + "@example.com";
    }

    private AuthDtos.AuthTokenResponse tokenResponse(MvcResult result) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), AuthDtos.AuthTokenResponse.class);
    }

    private void assertTokenPair(AuthDtos.AuthTokenResponse response, String subject) {
        assertThat(response.accessToken()).isNotBlank().doesNotContain("TODO");
        assertThat(response.refreshToken()).isNotBlank().doesNotContain("TODO");
        assertThat(jwtService.tryParse(response.accessToken(), JwtService.TokenType.ACCESS)
                .orElseThrow().getPayload().getSubject()).isEqualTo(subject);
        assertThat(jwtService.tryParse(response.refreshToken(), JwtService.TokenType.REFRESH)
                .orElseThrow().getPayload().getSubject()).isEqualTo(subject);
    }
}