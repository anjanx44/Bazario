package com.bazario.auth.domain.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void accessTokenContainsSubjectAndCanBeVerified() {
        var key = Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        var jwtService = new JwtService(key, clock);

        String token = jwtService.generateAccessToken(
                "user@example.com", List.of("ROLE_CUSTOMER"), Duration.ofHours(1));

        assertThat(jwtService.tryParse(token)).isPresent();
        assertThat(jwtService.tryParse(token).orElseThrow().getPayload().getSubject())
                .isEqualTo("user@example.com");
    }

    @Test
    void expiredTokenIsRejected() {
        var key = Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        var issuer = new JwtService(key, clock);
        var parser = new JwtService(key, Clock.offset(clock, Duration.ofSeconds(2)));

        String token = issuer.generateAccessToken(
                "user@example.com", List.of("ROLE_CUSTOMER"), Duration.ofSeconds(1));

        assertThat(parser.tryParse(token)).isEmpty();
    }

    @Test
    void tokenWithTamperedSignatureIsRejected() {
        var key = Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        var jwtService = new JwtService(key, clock);
        String token = jwtService.generateAccessToken(
                "user@example.com", List.of("ROLE_CUSTOMER"), Duration.ofHours(1));
        String[] parts = token.split("\\.");
        parts[2] = (parts[2].charAt(0) == 'A' ? "B" : "A") + parts[2].substring(1);

        assertThat(jwtService.tryParse(String.join(".", parts))).isEmpty();
    }

    @Test
    void tokenSignedWithDifferentKeyIsRejected() {
        var signingKey = Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        var verificationKey = Keys.hmacShaKeyFor(
                "abcdef0123456789abcdef0123456789".getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        var issuer = new JwtService(signingKey, clock);
        var verifier = new JwtService(verificationKey, clock);
        String token = issuer.generateAccessToken(
                "user@example.com", List.of("ROLE_CUSTOMER"), Duration.ofHours(1));

        assertThat(verifier.tryParse(token)).isEmpty();
    }

    @Test
    void refreshTokenIsRejectedWhenAccessTokenIsRequired() {
        var key = Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        var jwtService = new JwtService(key, clock);
        String token = jwtService.generateToken(
                "user@example.com", List.of(), Duration.ofDays(1), JwtService.TokenType.REFRESH);

        assertThat(jwtService.tryParse(token)).isPresent();
        assertThat(jwtService.tryParse(token, JwtService.TokenType.ACCESS)).isEmpty();
    }

    @Test
    void tokenSignedWithAnotherAlgorithmIsRejected() {
        var key = Keys.hmacShaKeyFor(
                "0123456789012345678901234567890101234567890123456789012345678901"
                        .getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        String token = Jwts.builder()
                .subject("user@example.com")
                .claim("token_type", "access")
                .claim("roles", List.of("ROLE_CUSTOMER"))
                .issuedAt(Date.from(clock.instant()))
                .expiration(Date.from(clock.instant().plus(Duration.ofHours(1))))
                .signWith(key, Jwts.SIG.HS512)
                .compact();

        assertThat(new JwtService(key, clock).tryParse(token, JwtService.TokenType.ACCESS)).isEmpty();
    }

    @Test
    void tokenWithoutExpiryIsRejected() {
        var key = Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        String token = Jwts.builder()
                .subject("user@example.com")
                .claim("token_type", "access")
                .issuedAt(Date.from(clock.instant()))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        assertThat(new JwtService(key, clock).tryParse(token)).isEmpty();
    }

    @Test
    void tokenWithUnknownTypeIsRejected() {
        var key = Keys.hmacShaKeyFor(
                "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
        var clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        String token = Jwts.builder()
                .subject("user@example.com")
                .claim("token_type", "unknown")
                .issuedAt(Date.from(clock.instant()))
                .expiration(Date.from(clock.instant().plus(Duration.ofHours(1))))
                .signWith(key, Jwts.SIG.HS256)
                .compact();

        assertThat(new JwtService(key, clock).tryParse(token)).isEmpty();
    }
}