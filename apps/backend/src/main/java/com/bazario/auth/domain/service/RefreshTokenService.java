package com.bazario.auth.domain.service;

import com.bazario.auth.domain.model.RefreshToken;
import com.bazario.auth.ports.out.RefreshTokenRepositoryPort;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.out.CustomerRepositoryPort;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final JwtService jwtService;
    private final RefreshTokenRepositoryPort refreshTokenRepository;
    private final CustomerRepositoryPort customerRepository;
    private final Clock clock;
    private final Duration regularTtl;
    private final Duration rememberedTtl;

    public RefreshTokenService(
            JwtService jwtService,
            RefreshTokenRepositoryPort refreshTokenRepository,
            CustomerRepositoryPort customerRepository,
            Clock clock,
            @Value("${security.jwt.refresh-ttl:PT24H}") Duration regularTtl,
            @Value("${security.jwt.remembered-refresh-ttl:P30D}") Duration rememberedTtl) {
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
        this.customerRepository = customerRepository;
        this.clock = clock;
        this.regularTtl = regularTtl;
        this.rememberedTtl = rememberedTtl;
    }

    @Transactional
    public String issue(Customer customer, boolean rememberMe) {
        Duration ttl = rememberMe ? rememberedTtl : regularTtl;
        UUID familyId = UUID.randomUUID();
        return issue(customer, familyId, ttl, rememberMe);
    }

    @Transactional
    public RotationResult rotate(String rawToken) {
        Optional<Claims> parsedClaims = jwtService.tryParse(rawToken, JwtService.TokenType.REFRESH)
                .map(token -> token.getPayload());
        if (parsedClaims.isEmpty()) {
            return RotationResult.invalid();
        }

        Claims claims = parsedClaims.orElseThrow();
        String tokenId = claims.getId();
        if (tokenId == null || tokenId.isBlank()) {
            return RotationResult.invalid();
        }

        String tokenIdHash = hashTokenId(tokenId);
        Optional<RefreshToken> original = refreshTokenRepository.findByTokenIdHash(tokenIdHash);
        if (original.isEmpty()) {
            return RotationResult.invalid();
        }

        UUID familyId = original.orElseThrow().familyId();
        refreshTokenRepository.lockFamily(familyId);
        Optional<RefreshToken> lockedToken = refreshTokenRepository.findByTokenIdHash(tokenIdHash);
        if (lockedToken.isEmpty()) {
            return RotationResult.invalid();
        }

        RefreshToken tokenState = lockedToken.orElseThrow();
        if (tokenState.state() == RefreshToken.State.CONSUMED) {
            refreshTokenRepository.revokeFamily(familyId);
            return RotationResult.reused();
        }
        if (tokenState.state() != RefreshToken.State.ACTIVE) {
            return RotationResult.invalid();
        }
        if (!tokenState.expiresAt().isAfter(clock.instant())) {
            refreshTokenRepository.save(tokenState.withState(RefreshToken.State.REVOKED));
            return RotationResult.invalid();
        }

        Optional<Customer> customer = customerRepository.findById(tokenState.customerId());
        if (customer.isEmpty() || !customer.orElseThrow().isEnabled()
                || !customer.orElseThrow().getEmail().equals(claims.getSubject())) {
            refreshTokenRepository.revokeFamily(familyId);
            return RotationResult.invalid();
        }

        boolean rememberMe = Boolean.TRUE.equals(claims.get("remember_me", Boolean.class));
        refreshTokenRepository.save(tokenState.withState(RefreshToken.State.CONSUMED));
        String replacement = issue(customer.orElseThrow(), familyId,
                rememberMe ? rememberedTtl : regularTtl, rememberMe);
        return RotationResult.rotated(replacement, customer.orElseThrow(), rememberMe);
    }

    static String hashTokenId(String tokenId) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(tokenId.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    private String issue(Customer customer, UUID familyId, Duration ttl, boolean rememberMe) {
        String tokenId = UUID.randomUUID().toString();
        String token = jwtService.generateToken(customer.getEmail(), List.of("ROLE_CUSTOMER"), ttl,
                JwtService.TokenType.REFRESH, tokenId, rememberMe);
        refreshTokenRepository.save(new RefreshToken(
                hashTokenId(tokenId), customer.getId(), familyId, clock.instant().plus(ttl), RefreshToken.State.ACTIVE));
        return token;
    }

    public enum RotationStatus {
        ROTATED,
        INVALID,
        REUSED
    }

    public record RotationResult(
            RotationStatus status,
            String refreshToken,
            Customer customer,
            boolean rememberMe) {

        private static RotationResult rotated(String token, Customer customer, boolean rememberMe) {
            return new RotationResult(RotationStatus.ROTATED, token, customer, rememberMe);
        }

        private static RotationResult invalid() {
            return new RotationResult(RotationStatus.INVALID, null, null, false);
        }

        private static RotationResult reused() {
            return new RotationResult(RotationStatus.REUSED, null, null, false);
        }
    }
}