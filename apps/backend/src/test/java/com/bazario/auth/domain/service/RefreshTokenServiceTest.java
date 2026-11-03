package com.bazario.auth.domain.service;

import com.bazario.auth.domain.model.RefreshToken;
import com.bazario.auth.ports.out.RefreshTokenRepositoryPort;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.out.CustomerRepositoryPort;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Duration REGULAR_TTL = Duration.ofDays(1);
    private static final Duration REMEMBERED_TTL = Duration.ofDays(30);

    @Mock
    private CustomerRepositoryPort customerRepository;

    private InMemoryRefreshTokenRepository tokenRepository;
    private Customer customer;
    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        tokenRepository = new InMemoryRefreshTokenRepository();
        customer = Customer.builder()
                .id(UUID.randomUUID())
                .email("user@example.com")
                .passwordHash("encoded-password")
                .firstName("Test")
                .enabled(true)
                .build();
        refreshTokenService = serviceAt(Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void issuePersistsOnlyHashedTokenIdAndUsesRememberMeLifetime() {
        String rawToken = refreshTokenService.issue(customer, true);
        var claims = new JwtService(KEY, Clock.fixed(NOW, ZoneOffset.UTC))
                .tryParse(rawToken, JwtService.TokenType.REFRESH).orElseThrow().getPayload();
        RefreshToken stored = tokenRepository.findByTokenIdHash(
                RefreshTokenService.hashTokenId(claims.getId())).orElseThrow();

        assertThat(stored.tokenIdHash()).isEqualTo(RefreshTokenService.hashTokenId(claims.getId()));
        assertThat(rawToken).doesNotContain(stored.tokenIdHash());
        assertThat(stored.customerId()).isEqualTo(customer.getId());
        assertThat(stored.state()).isEqualTo(RefreshToken.State.ACTIVE);
        assertThat(stored.expiresAt()).isEqualTo(NOW.plus(REMEMBERED_TTL));
        assertThat(claims.get("remember_me", Boolean.class)).isTrue();
    }

    @Test
    void rotateConsumesOldTokenAndIssuesNewTokenInSameFamily() {
        String originalToken = refreshTokenService.issue(customer, false);
        String oldTokenId = new JwtService(KEY, Clock.fixed(NOW, ZoneOffset.UTC))
                .tryParse(originalToken, JwtService.TokenType.REFRESH).orElseThrow().getPayload().getId();
        RefreshToken original = tokenRepository.findByTokenIdHash(
                RefreshTokenService.hashTokenId(oldTokenId)).orElseThrow();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        var result = refreshTokenService.rotate(originalToken);

        assertThat(result.status()).isEqualTo(RefreshTokenService.RotationStatus.ROTATED);
        assertThat(result.refreshToken()).isNotEqualTo(originalToken);
        assertThat(result.customer().getId()).isEqualTo(customer.getId());
        assertThat(tokenRepository.findByTokenIdHash(original.tokenIdHash()).orElseThrow().state())
                .isEqualTo(RefreshToken.State.CONSUMED);
        var rotatedClaims = new JwtService(KEY, Clock.fixed(NOW, ZoneOffset.UTC))
                .tryParse(result.refreshToken(), JwtService.TokenType.REFRESH).orElseThrow().getPayload();
        RefreshToken rotated = tokenRepository.findByTokenIdHash(
                RefreshTokenService.hashTokenId(rotatedClaims.getId())).orElseThrow();
        assertThat(rotated.familyId()).isEqualTo(original.familyId());
        assertThat(rotated.state()).isEqualTo(RefreshToken.State.ACTIVE);
        assertThat(result.rememberMe()).isFalse();
    }

    @Test
    void expiredRefreshTokenIsRejected() {
        String token = refreshTokenService.issue(customer, false);
        var expiredService = serviceAt(Clock.offset(Clock.fixed(NOW, ZoneOffset.UTC), Duration.ofDays(2)));

        var result = expiredService.rotate(token);

        assertThat(result.status()).isEqualTo(RefreshTokenService.RotationStatus.INVALID);
    }

    @Test
    void replayedRefreshTokenRevokesItsWholeFamily() {
        String token = refreshTokenService.issue(customer, false);
        var originalClaims = new JwtService(KEY, Clock.fixed(NOW, ZoneOffset.UTC))
                .tryParse(token, JwtService.TokenType.REFRESH).orElseThrow().getPayload();
        UUID familyId = tokenRepository.findByTokenIdHash(
                RefreshTokenService.hashTokenId(originalClaims.getId())).orElseThrow().familyId();
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        assertThat(refreshTokenService.rotate(token).status())
                .isEqualTo(RefreshTokenService.RotationStatus.ROTATED);
        var replayResult = refreshTokenService.rotate(token);

        assertThat(replayResult.status()).isEqualTo(RefreshTokenService.RotationStatus.REUSED);
        assertThat(tokenRepository.lockFamily(familyId))
                .allMatch(refreshToken -> refreshToken.state() == RefreshToken.State.REVOKED);
    }

    @Test
    void disabledCustomerCannotRefresh() {
        String token = refreshTokenService.issue(customer, false);
        customer.setEnabled(false);
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));

        var result = refreshTokenService.rotate(token);

        assertThat(result.status()).isEqualTo(RefreshTokenService.RotationStatus.INVALID);
    }

    @Test
    void deletedCustomerCannotRefresh() {
        String token = refreshTokenService.issue(customer, false);

        var result = refreshTokenService.rotate(token);

        assertThat(result.status()).isEqualTo(RefreshTokenService.RotationStatus.INVALID);
    }

    private RefreshTokenService serviceAt(Clock clock) {
        return new RefreshTokenService(
                new JwtService(KEY, clock), tokenRepository, customerRepository, clock,
                REGULAR_TTL, REMEMBERED_TTL);
    }

    private static class InMemoryRefreshTokenRepository implements RefreshTokenRepositoryPort {

        private final Map<String, RefreshToken> tokens = new HashMap<>();

        @Override
        public Optional<RefreshToken> findByTokenIdHash(String tokenIdHash) {
            return Optional.ofNullable(tokens.get(tokenIdHash));
        }

        @Override
        public List<RefreshToken> lockFamily(UUID familyId) {
            return tokens.values().stream().filter(token -> token.familyId().equals(familyId)).toList();
        }

        @Override
        public RefreshToken save(RefreshToken refreshToken) {
            tokens.put(refreshToken.tokenIdHash(), refreshToken);
            return refreshToken;
        }

        @Override
        public void revokeFamily(UUID familyId) {
            tokens.replaceAll((hash, token) -> token.familyId().equals(familyId)
                    ? token.withState(RefreshToken.State.REVOKED) : token);
        }
    }
}