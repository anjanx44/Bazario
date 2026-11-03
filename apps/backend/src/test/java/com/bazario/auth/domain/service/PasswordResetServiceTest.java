package com.bazario.auth.domain.service;

import com.bazario.auth.ports.out.PasswordResetNotifier;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.in.CustomerUseCase;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    private static final String EMAIL = "user@example.com";
    private static final String PASSWORD_HASH = "stored-password-hash";
    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");
    private static final Duration RESET_TTL = Duration.ofMinutes(15);
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            "01234567890123456789012345678901".getBytes(StandardCharsets.UTF_8));

    @Mock
    private CustomerUseCase customers;

    @Mock
    private PasswordResetNotifier notifier;

    private Customer customer;
    private JwtService jwtService;
    private PasswordResetService service;

    @BeforeEach
    void setUp() {
        customer = Customer.builder().id(UUID.randomUUID()).email(EMAIL)
                .passwordHash(PASSWORD_HASH).enabled(true).build();
        jwtService = new JwtService(KEY, Clock.fixed(NOW, ZoneOffset.UTC));
        service = new PasswordResetService(jwtService, customers, notifier, RESET_TTL);
    }

    @Test
    void knownEmailReceivesAnExpiringResetTokenWithPasswordFingerprint() throws Exception {
        when(customers.getCustomerByEmail(EMAIL)).thenReturn(Optional.of(customer));

        service.requestReset(EMAIL);

        ArgumentCaptor<String> token = ArgumentCaptor.forClass(String.class);
        verify(notifier).sendResetToken(eq(EMAIL), token.capture());
        var claims = jwtService.tryParse(token.getValue(), JwtService.TokenType.PASSWORD_RESET)
                .orElseThrow().getPayload();
        assertThat(claims.get("token_type")).isEqualTo("reset");
        assertThat(claims.get("fp")).isEqualTo(fingerprint(PASSWORD_HASH));
        assertThat(claims.getExpiration().toInstant()).isEqualTo(NOW.plus(RESET_TTL));
        assertThat(jwtService.tryParse(token.getValue(), JwtService.TokenType.ACCESS)).isEmpty();
    }

    @Test
    void unknownOrDisabledEmailDoesNotSendAToken() {
        customer.setEnabled(false);
        when(customers.getCustomerByEmail(EMAIL)).thenReturn(Optional.of(customer));
        when(customers.getCustomerByEmail("unknown@example.com")).thenReturn(Optional.empty());

        service.requestReset("unknown@example.com");
        service.requestReset(EMAIL);

        verifyNoInteractions(notifier);
    }

    @Test
    void validResetUsesConditionalPasswordUpdate() throws Exception {
        when(customers.getCustomerByEmail(EMAIL)).thenReturn(Optional.of(customer));
        when(customers.changePasswordIfUnchanged(customer.getId(), PASSWORD_HASH, "new-password"))
                .thenReturn(true);
        String token = jwtService.generateResetToken(EMAIL, fingerprint(PASSWORD_HASH), RESET_TTL);

        assertThat(service.resetPassword(token, "new-password")).isTrue();
        verify(customers).changePasswordIfUnchanged(customer.getId(), PASSWORD_HASH, "new-password");
    }

    @Test
    void expiredAndWrongTypeTokensAreRejected() throws Exception {
        String token = jwtService.generateResetToken(EMAIL, fingerprint(PASSWORD_HASH), RESET_TTL);
        var laterJwtService = new JwtService(KEY,
                Clock.offset(Clock.fixed(NOW, ZoneOffset.UTC), Duration.ofMinutes(16)));
        var laterService = new PasswordResetService(laterJwtService, customers, notifier, RESET_TTL);
        String accessToken = jwtService.generateAccessToken(EMAIL, List.of("ROLE_CUSTOMER"), Duration.ofHours(1));

        assertThat(laterService.resetPassword(token, "new-password")).isFalse();
        assertThat(service.resetPassword(accessToken, "new-password")).isFalse();
        assertThat(service.resetPassword("garbage", "new-password")).isFalse();
        verify(customers, never()).changePasswordIfUnchanged(any(), anyString(), anyString());
    }

    @Test
    void stalePasswordFingerprintIsRejected() throws Exception {
        when(customers.getCustomerByEmail(EMAIL)).thenReturn(Optional.of(customer));
        String token = jwtService.generateResetToken(EMAIL, fingerprint("older-password-hash"), RESET_TTL);

        assertThat(service.resetPassword(token, "new-password")).isFalse();
        verify(customers, never()).changePasswordIfUnchanged(any(), anyString(), anyString());
    }

    private String fingerprint(String hash) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                .digest(hash.getBytes(StandardCharsets.UTF_8)));
    }
}