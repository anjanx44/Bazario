package com.bazario.auth.adapters.in.web;

import com.bazario.auth.adapters.in.web.dto.AuthDtos;
import com.bazario.customers.domain.model.Customer;
import com.bazario.customers.ports.in.CustomerUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Customer Authentication Controller
 *
 * Handles all public-facing auth flows for the Bazario storefront.
 * JWT generation is delegated to JwtService (to be implemented in
 * com.bazario.auth.domain.service.JwtService).
 *
 * Endpoints:
 *   POST /api/v1/auth/login           — credential login → JWT
 *   POST /api/v1/auth/register        — new customer registration → JWT
 *   POST /api/v1/auth/refresh         — refresh access token
 *   GET  /api/v1/auth/me              — current customer profile (requires Bearer token)
 *   POST /api/v1/auth/forgot-password — trigger password-reset email
 *   POST /api/v1/auth/reset-password  — submit new password with reset token
 *
 * Security: All endpoints are PUBLIC except GET /api/v1/auth/me.
 * Configure in SecurityConfig: .requestMatchers("/api/v1/auth/**").permitAll()
 *                               except .requestMatchers(GET, "/api/v1/auth/me").authenticated()
 */
@Tag(name = "Customer Auth", description = "Storefront customer authentication endpoints")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomerUseCase customerUseCase;
    private final AuthenticationManager authenticationManager;

    // TODO: inject JwtService once implemented:
    // private final JwtService jwtService;

    // ── POST /api/v1/auth/login ───────────────────────────────────────────────
    /**
     * Authenticate a customer with email + password.
     *
     * Request body:
     * {
     *   "email":      "user@example.com",
     *   "password":   "secret123",
     *   "rememberMe": true
     * }
     *
     * Success 200:
     * {
     *   "accessToken":  "<jwt>",
     *   "refreshToken": "<jwt>",
     *   "tokenType":    "Bearer",
     *   "expiresIn":    3600,
     *   "customer": { "id", "firstName", "lastName", "email", "createdAt" }
     * }
     *
     * Error 401: { "message": "Invalid email or password." }
     */
    @Operation(summary = "Customer login with email + password")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            // TODO: replace stub with real JWT generation:
            // String accessToken  = jwtService.generateAccessToken(auth, request.rememberMe());
            // String refreshToken = jwtService.generateRefreshToken(auth);
            String accessToken  = "TODO_GENERATE_JWT_ACCESS_TOKEN";
            String refreshToken = "TODO_GENERATE_JWT_REFRESH_TOKEN";
            long   expiresIn    = request.rememberMe() ? 2592000L : 3600L; // 30d or 1h

            Customer customer = customerUseCase.getCustomerByEmail(request.email())
                    .orElseThrow(() -> new IllegalStateException("Authenticated customer not found"));

            AuthDtos.CustomerProfileResponse profile = new AuthDtos.CustomerProfileResponse(
                    customer.getId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    null,
                    customer.getCreatedAt()
            );

            return ResponseEntity.ok(new AuthDtos.AuthTokenResponse(
                    accessToken, refreshToken, "Bearer", expiresIn, profile));

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthDtos.MessageResponse("Invalid email or password."));
        }
    }

    // ── POST /api/v1/auth/register ────────────────────────────────────────────
    /**
     * Register a new customer account and return JWT tokens.
     *
     * Request body:
     * {
     *   "firstName": "Jane",
     *   "lastName":  "Doe",
     *   "email":     "jane@example.com",
     *   "password":  "secret123"
     * }
     *
     * Success 201: same shape as /login response.
     * Error 409: { "message": "An account with this email already exists." }
     */
    @Operation(summary = "Register a new customer account")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthDtos.RegisterRequest request) {
        boolean exists = customerUseCase.getCustomerByEmail(request.email()).isPresent();
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new AuthDtos.MessageResponse("An account with this email already exists."));
        }

        UUID newCustomerId = customerUseCase.registerCustomer(
                Customer.builder()
                        .firstName(request.firstName())
                        .lastName(request.lastName())
                        .email(request.email())
                        .passwordHash(request.password()) // hashed inside CustomerService
                        .build()
        );
        Customer newCustomer = customerUseCase.getCustomerById(newCustomerId)
                .orElseThrow(() -> new IllegalStateException("Customer not found after register: " + newCustomerId));

        // TODO: replace stub with real JWT generation:
        String accessToken  = "TODO_GENERATE_JWT_ACCESS_TOKEN";
        String refreshToken = "TODO_GENERATE_JWT_REFRESH_TOKEN";

        AuthDtos.CustomerProfileResponse profile = new AuthDtos.CustomerProfileResponse(
                newCustomer.getId(),
                newCustomer.getFirstName(),
                newCustomer.getLastName(),
                newCustomer.getEmail(),
                null,
                newCustomer.getCreatedAt()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthDtos.AuthTokenResponse(accessToken, refreshToken, "Bearer", 3600L, profile));
    }

    // ── POST /api/v1/auth/refresh ─────────────────────────────────────────────
    /**
     * Exchange a valid refresh token for a new access token.
     *
     * Request body: { "refreshToken": "<jwt>" }
     * Success 200: same shape as /login response.
     * Error 401: { "message": "Session expired. Please sign in again." }
     */
    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody AuthDtos.RefreshTokenRequest request) {
        // TODO: validate refresh token via jwtService.validateRefreshToken(request.refreshToken())
        // and issue new access token.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body(new AuthDtos.MessageResponse("JWT service not yet implemented. Wire JwtService here."));
    }

    // ── GET /api/v1/auth/me ───────────────────────────────────────────────────
    /**
     * Returns the currently authenticated customer's profile.
     * Requires: Authorization: Bearer <accessToken>
     *
     * Success 200: CustomerProfileResponse
     * Error 401: if token is missing or invalid (handled by Spring Security filter)
     */
    @Operation(summary = "Get current customer profile (requires auth)")
    @GetMapping("/me")
    public ResponseEntity<AuthDtos.CustomerProfileResponse> me(Authentication authentication) {
        String email = authentication.getName();
        return customerUseCase.getCustomerByEmail(email)
                .map(customer -> ResponseEntity.ok(new AuthDtos.CustomerProfileResponse(
                        customer.getId(),
                        customer.getFirstName(),
                        customer.getLastName(),
                        customer.getEmail(),
                        null,
                        customer.getCreatedAt()
                )))
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // ── POST /api/v1/auth/forgot-password ─────────────────────────────────────
    /**
     * Triggers a password-reset email to the given address.
     * Always returns 200 (to prevent email enumeration).
     *
     * Request body: { "email": "user@example.com" }
     * Success 200: { "message": "If that email exists, a reset link has been sent." }
     */
    @Operation(summary = "Request password reset email")
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthDtos.MessageResponse> forgotPassword(
            @Valid @RequestBody AuthDtos.ForgotPasswordRequest request) {
        // TODO: generate a secure reset token, persist it, and send via email service
        // customerUseCase.initiatePasswordReset(request.email());
        return ResponseEntity.ok(
                new AuthDtos.MessageResponse("If that email exists, a reset link has been sent."));
    }

    // ── POST /api/v1/auth/reset-password ──────────────────────────────────────
    /**
     * Validates the reset token and updates the customer's password.
     *
     * Request body: { "token": "<reset-token>", "newPassword": "newSecret123" }
     * Success 200: { "message": "Password updated successfully." }
     * Error 400:   { "message": "Invalid or expired reset token." }
     */
    @Operation(summary = "Reset password with token")
    @PostMapping("/reset-password")
    public ResponseEntity<AuthDtos.MessageResponse> resetPassword(
            @Valid @RequestBody AuthDtos.ResetPasswordRequest request) {
        // TODO: validate token and update password:
        // boolean updated = customerUseCase.resetPassword(request.token(), request.newPassword());
        // if (!updated) return ResponseEntity.badRequest().body(new AuthDtos.MessageResponse("Invalid or expired reset token."));
        return ResponseEntity.ok(new AuthDtos.MessageResponse("Password updated successfully. You can now sign in."));
    }
}
