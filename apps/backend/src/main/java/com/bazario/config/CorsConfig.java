package com.bazario.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Global CORS configuration for the Bazario Spring Boot backend.
 *
 * Allows the Next.js Storefront (port 3000) and Admin Portal (port 3001)
 * to call the API during local development. In production, set the
 * CORS_ALLOWED_ORIGINS environment variable to the real domain(s).
 *
 * Example .env:
 *   CORS_ALLOWED_ORIGINS=https://bazario.com,https://admin.bazario.com
 */
@Configuration
public class CorsConfig {

    /**
     * Comma-separated list of allowed origins.
     * Defaults to local Next.js dev servers if not set.
     */
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:3001}")
    private List<String> allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins — storefront + admin portal
        config.setAllowedOrigins(allowedOrigins);

        // Standard REST methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Headers the frontend sends (including Authorization for JWT)
        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));

        // Headers the frontend is allowed to read from responses
        config.setExposedHeaders(List.of(
                "Authorization",
                "X-Total-Count",
                "X-Total-Pages"
        ));

        // Allow cookies / credentials (needed for refresh-token cookie strategy)
        config.setAllowCredentials(true);

        // Cache preflight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply to all API routes
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
