package com.bazario.auth.domain.service;

import com.bazario.auth.ports.out.PasswordResetNotifier;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.in.CustomerUseCase;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Optional;

@Slf4j
@Service
public class PasswordResetService {

    private final JwtService jwtService;
    private final CustomerUseCase customers;
    private final PasswordResetNotifier notifier;
    private final Duration resetTtl;

    public PasswordResetService(
            JwtService jwtService,
            CustomerUseCase customers,
            PasswordResetNotifier notifier,
            @Value("${security.jwt.reset-ttl:PT15M}") Duration resetTtl) {
        this.jwtService = jwtService;
        this.customers = customers;
        this.notifier = notifier;
        this.resetTtl = resetTtl;
    }

    public void requestReset(String email) {
        customers.getCustomerByEmail(email).filter(Customer::isEnabled).ifPresent(customer -> {
            String token = jwtService.generateResetToken(
                    customer.getEmail(), fingerprint(customer.getPasswordHash()), resetTtl);
            try {
                notifier.sendResetToken(customer.getEmail(), token);
            } catch (RuntimeException exception) {
                log.warn("Password reset notification could not be delivered");
            }
        });
    }

    public boolean resetPassword(String token, String newPassword) {
        Optional<Claims> claims = jwtService.tryParse(token, JwtService.TokenType.PASSWORD_RESET)
                .map(parsed -> parsed.getPayload());
        if (claims.isEmpty() || !(claims.orElseThrow().get("fp") instanceof String tokenFingerprint)) {
            return false;
        }

        Optional<Customer> customer = customers.getCustomerByEmail(claims.orElseThrow().getSubject())
                .filter(Customer::isEnabled);
        if (customer.isEmpty() || customer.orElseThrow().getPasswordHash() == null) {
            return false;
        }

        Customer current = customer.orElseThrow();
        String expectedFingerprint = fingerprint(current.getPasswordHash());
        if (!MessageDigest.isEqual(expectedFingerprint.getBytes(StandardCharsets.UTF_8),
                tokenFingerprint.getBytes(StandardCharsets.UTF_8))) {
            return false;
        }
        return customers.changePasswordIfUnchanged(current.getId(), current.getPasswordHash(), newPassword);
    }

    private static String fingerprint(String passwordHash) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(passwordHash.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}