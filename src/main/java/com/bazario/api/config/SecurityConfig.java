package com.bazario.api.config;


import com.bazario.api.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    /**
     * Configures the Spring Security filter chain for the application.
     *
     * This defines the complete security rules and request handling flow:
     *
     * Security Configurations:
     * - Disables CSRF as the application is stateless (JWT-based auth)
     * - Sets session management to STATELESS (no server-side session)
     * - Defines public and secured endpoints
     *
     * Endpoint Rules:
     * - /api/auth/** → public access (login/register)
     * - /api/public/** → public APIs
     * - Swagger endpoints → publicly accessible
     * - All other endpoints → require authentication
     *
     * Security Components:
     * - Uses custom AuthenticationProvider for authentication logic
     * - Adds JWT Authentication Filter before UsernamePasswordAuthenticationFilter
     *   to process and validate JWT tokens on each request
     *
     * This setup enables stateless JWT-based authentication for REST APIs.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )

                .authenticationProvider(authenticationProvider())

                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    /**
     * Configures the authentication provider for Spring Security.
     *
     * DaoAuthenticationProvider is used to retrieve user details from
     * UserDetailsService and validate credentials using a password encoder.
     *
     * Responsibilities:
     * - Loads user from database via UserDetailsService
     * - Compares raw password with encoded password
     * - Uses BCrypt for secure password hashing
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    /**
     * Provides a BCrypt password encoder for secure password hashing.
     *
     * BCrypt is a strong hashing function used to store passwords safely
     * by adding salt and making brute-force attacks difficult.
     */
   @Bean
   public PasswordEncoder passwordEncoder() {
       return new BCryptPasswordEncoder();
   }

    /**
     * Exposes AuthenticationManager bean for authentication processing.
     *
     * AuthenticationManager is responsible for processing authentication
     * requests and delegating to the configured AuthenticationProvider(s).
     */
   @Bean
   public AuthenticationManager authenticationManager
           (AuthenticationConfiguration authConfig) throws  Exception {
        return authConfig.getAuthenticationManager();
   }

}
