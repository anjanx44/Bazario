package com.bazario.auth.domain.service;

import com.bazario.config.CorsConfig;
import com.bazario.config.JwtConfiguration;
import com.bazario.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = JwtAuthenticationFilterTest.ProtectedController.class,
        properties = "security.jwt.secret=AAECAwQFBgcICQoLDA0ODxAREhMUFRYXGBkaGxwdHh8=")
@Import({SecurityConfig.class, CorsConfig.class, JwtConfiguration.class,
        JwtAuthenticationFilterTest.ProtectedController.class, JwtAuthenticationFilterTest.TestBeans.class})
class JwtAuthenticationFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void missingBearerTokenGetsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidBearerTokenGetsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer invalid.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenWithBlankAuthorityGetsUnauthorized() throws Exception {
        String token = tokenFor("", JwtService.TokenType.ACCESS);

        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void customerTokenCanAccessMe() throws Exception {
        String token = tokenFor("ROLE_CUSTOMER", JwtService.TokenType.ACCESS);

        mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }

    @Test
    void customerTokenGetsForbiddenOnAdminRoute() throws Exception {
        String token = tokenFor("ROLE_CUSTOMER", JwtService.TokenType.ACCESS);

        mockMvc.perform(get("/api/v1/admin/test").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminTokenCanAccessAdminRoute() throws Exception {
        String token = tokenFor("ROLE_ADMIN", JwtService.TokenType.ACCESS);

        mockMvc.perform(get("/api/v1/admin/test").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("admin"));
    }

    @Test
    void refreshAndPasswordResetTokensCannotAccessProtectedRoutes() throws Exception {
        for (var type : new JwtService.TokenType[]{
                JwtService.TokenType.REFRESH, JwtService.TokenType.PASSWORD_RESET}) {
            String token = tokenFor("ROLE_CUSTOMER", type);

            mockMvc.perform(get("/api/v1/auth/me").header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }
    }

    private String tokenFor(String role, JwtService.TokenType type) {
        return jwtService.generateToken("user@example.com", java.util.List.of(role), Duration.ofHours(1), type);
    }

    @RestController
    static class ProtectedController {

        @GetMapping("/api/v1/auth/me")
        Map<String, String> me(Authentication authentication) {
            return Map.of("email", authentication.getName());
        }

        @GetMapping("/api/v1/admin/test")
        String admin() {
            return "admin";
        }
    }

    @TestConfiguration
    static class TestBeans {

        @Bean
        UserDetailsService userDetailsService() {
            return username -> User.withUsername(username).password("not-used").roles("CUSTOMER").build();
        }
    }
}