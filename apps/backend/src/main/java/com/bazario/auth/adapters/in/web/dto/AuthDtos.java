package com.bazario.auth.adapters.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Auth DTOs for the Bazario customer authentication API.
 *
 * Endpoints:
 *   POST /api/v1/auth/login           — credential login
 *   POST /api/v1/auth/register        — new customer registration
 *   POST /api/v1/auth/refresh         — refresh access token
 *   GET  /api/v1/auth/me              — fetch current customer profile
 *   POST /api/v1/auth/forgot-password — trigger reset email
 *   POST /api/v1/auth/reset-password  — submit new password with token
 */
public class AuthDtos {

    // ── Requests ──────────────────────────────────────────────────────────────

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password,
            boolean rememberMe
    ) {}

    public record RegisterRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password
    ) {}

    public record RefreshTokenRequest(
            @NotBlank String refreshToken
    ) {}

    public record ForgotPasswordRequest(
            @NotBlank @Email String email
    ) {}

    public record ResetPasswordRequest(
            @NotBlank String token,
            @NotBlank @Size(min = 8) String newPassword
    ) {}

    // ── Responses ─────────────────────────────────────────────────────────────

    public record AuthTokenResponse(
            String accessToken,
            String refreshToken,
            String tokenType,
            long expiresIn,
            CustomerProfileResponse customer
    ) {}

    public record CustomerProfileResponse(
            UUID id,
            String firstName,
            String lastName,
            String email,
            String avatarUrl,
            ZonedDateTime createdAt
    ) {}

    public record MessageResponse(
            String message
    ) {}
}
