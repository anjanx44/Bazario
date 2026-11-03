package com.bazario.auth.domain.service;

import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.in.CustomerUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthTokenService {

    private final AuthenticationManager authenticationManager;
    private final CustomerUseCase customerUseCase;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final Duration accessTtl;

    public AuthTokenService(
            AuthenticationManager authenticationManager,
            CustomerUseCase customerUseCase,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            @Value("${security.jwt.access-ttl:PT1H}") Duration accessTtl) {
        if (accessTtl.isZero() || accessTtl.isNegative()) {
            throw new IllegalArgumentException("Access token lifetime must be positive");
        }
        this.authenticationManager = authenticationManager;
        this.customerUseCase = customerUseCase;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.accessTtl = accessTtl;
    }

    public TokenPair login(String email, String password, boolean rememberMe) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));
        Customer customer = customerUseCase.getCustomerByEmail(authentication.getName())
                .filter(Customer::isEnabled)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return issue(customer, roles, rememberMe);
    }

    @Transactional
    public TokenPair register(String firstName, String lastName, String email, String password) {
        if (customerUseCase.getCustomerByEmail(email).isPresent()) {
            throw new EmailAlreadyRegisteredException();
        }
        UUID customerId = customerUseCase.registerCustomer(Customer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .passwordHash(password)
                .build());
        Customer customer = customerUseCase.getCustomerById(customerId)
                .orElseThrow(() -> new IllegalStateException("Registered customer not found"));
        return issue(customer, List.of("ROLE_CUSTOMER"), false);
    }

    public Optional<TokenPair> refresh(String rawRefreshToken) {
        RefreshTokenService.RotationResult result = refreshTokenService.rotate(rawRefreshToken);
        if (result.status() != RefreshTokenService.RotationStatus.ROTATED) {
            return Optional.empty();
        }
        return Optional.of(tokenPair(result.customer(), List.of("ROLE_CUSTOMER"), result.refreshToken()));
    }

    private TokenPair issue(Customer customer, List<String> roles, boolean rememberMe) {
        String refreshToken = refreshTokenService.issue(customer, rememberMe);
        return tokenPair(customer, roles, refreshToken);
    }

    private TokenPair tokenPair(Customer customer, List<String> roles, String refreshToken) {
        String accessToken = jwtService.generateAccessToken(customer.getEmail(), roles, accessTtl);
        return new TokenPair(accessToken, refreshToken, accessTtl.toSeconds(), customer);
    }

    public record TokenPair(String accessToken, String refreshToken, long expiresIn, Customer customer) {
    }

    public static class EmailAlreadyRegisteredException extends RuntimeException {
    }
}