package com.bazario.auth.domain.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class JwtService {

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ROLES_CLAIM = "roles";
    private static final String REMEMBER_ME_CLAIM = "remember_me";
    private static final String FINGERPRINT_CLAIM = "fp";

    private final SecretKey key;
    private final Clock clock;

    public JwtService(SecretKey key, Clock clock) {
        this.key = Objects.requireNonNull(key);
        this.clock = Objects.requireNonNull(clock);
    }

    public String generateAccessToken(String subject, List<String> roles, Duration ttl) {
        return generateToken(subject, roles, ttl, TokenType.ACCESS);
    }

    public String generateResetToken(String subject, String fingerprint, Duration ttl) {
        if (fingerprint == null || fingerprint.isBlank()) {
            throw new IllegalArgumentException("Password fingerprint must not be blank");
        }
        return generateToken(subject, List.of(), ttl, TokenType.PASSWORD_RESET, null, false, fingerprint);
    }

    public String generateToken(String subject, List<String> roles, Duration ttl, TokenType type) {
        return generateToken(subject, roles, ttl, type, null, false);
    }

    public String generateToken(
            String subject,
            List<String> roles,
            Duration ttl,
            TokenType type,
            String tokenId,
            boolean rememberMe) {
        return generateToken(subject, roles, ttl, type, tokenId, rememberMe, null);
    }

    private String generateToken(
            String subject,
            List<String> roles,
            Duration ttl,
            TokenType type,
            String tokenId,
            boolean rememberMe,
            String fingerprint) {
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("Token subject must not be blank");
        }
        if (ttl == null || ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("Token lifetime must be positive");
        }
        Objects.requireNonNull(type, "Token type must not be null");

        var issuedAt = clock.instant();
        var expiresAt = issuedAt.plus(ttl);

        var builder = Jwts.builder()
                .subject(subject)
                .claim(ROLES_CLAIM, roles == null ? List.of() : List.copyOf(roles))
                .claim(TOKEN_TYPE_CLAIM, type.claimValue())
                .claim(REMEMBER_ME_CLAIM, type == TokenType.REFRESH && rememberMe)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt));
        if (tokenId != null) {
            builder.id(tokenId);
        }
        if (fingerprint != null) {
            builder.claim(FINGERPRINT_CLAIM, fingerprint);
        }
        return builder.signWith(key, Jwts.SIG.HS256).compact();
    }

    public Optional<Jws<Claims>> tryParse(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(key)
                    .clock(() -> Date.from(clock.instant()))
                    .build()
                    .parseSignedClaims(token);
            Claims claims = parsed.getPayload();
            boolean knownType = Arrays.stream(TokenType.values())
                    .anyMatch(type -> type.claimValue().equals(claims.get(TOKEN_TYPE_CLAIM)));
            if (!Jwts.SIG.HS256.getId().equals(parsed.getHeader().getAlgorithm())
                    || claims.getExpiration() == null
                    || claims.getSubject() == null || claims.getSubject().isBlank()
                    || !knownType) {
                return Optional.empty();
            }
            return Optional.of(parsed);
        } catch (JwtException | IllegalArgumentException exception) {
            return Optional.empty();
        }
    }

    public Optional<Jws<Claims>> tryParse(String token, TokenType expectedType) {
        if (expectedType == null) {
            return Optional.empty();
        }
        return tryParse(token)
                .filter(parsed -> expectedType.claimValue().equals(parsed.getPayload().get(TOKEN_TYPE_CLAIM)));
    }

    public enum TokenType {
        ACCESS("access"),
        REFRESH("refresh"),
        PASSWORD_RESET("reset");

        private final String claimValue;

        TokenType(String claimValue) {
            this.claimValue = claimValue;
        }

        private String claimValue() {
            return claimValue;
        }
    }
}