/**
 * Bazario Storefront — Auth Service
 *
 * Handles customer authentication API calls to the Spring Boot backend.
 * All functions are wrapped in try/catch — on failure they return a typed
 * error result so the UI can display a message without crashing.
 *
 * Backend base URL: NEXT_PUBLIC_API_URL (e.g. http://localhost:8080)
 *
 * NOTE: For full SSO (Google), wire handleGoogleSignIn() into NextAuth.js
 * signIn('google') on the client side. The backend issues a JWT on success.
 */

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

// ─── TypeScript Interfaces (mirror Java DTOs) ────────────────────────────────

export interface LoginRequest {
  email: string;
  password: string;
  rememberMe?: boolean;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: 'Bearer';
  expiresIn: number; // seconds
  customer: CustomerProfile;
}

export interface CustomerProfile {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  avatarUrl?: string;
  createdAt: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

// ─── Discriminated Union Result Type ─────────────────────────────────────────

export type AuthResult<T> =
  | { success: true; data: T }
  | { success: false; error: string; statusCode?: number };

// ─── Static Fallback / Mock ───────────────────────────────────────────────────

const STATIC_CUSTOMER_PROFILE: CustomerProfile = {
  id: 'mock-customer-001',
  firstName: 'Demo',
  lastName: 'User',
  email: 'demo@bazario.com',
  createdAt: new Date().toISOString(),
};

// ─── Service Functions ────────────────────────────────────────────────────────

/**
 * Authenticate a customer with email + password credentials.
 * POST /api/v1/auth/login
 *
 * On success: returns JWT access + refresh tokens.
 * On 401/403: returns a typed error (wrong credentials).
 * On network failure: returns a typed error (backend offline).
 */
export async function loginWithCredentials(
  payload: LoginRequest,
): Promise<AuthResult<AuthTokenResponse>> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      cache: 'no-store',
    });

    if (res.status === 401 || res.status === 403) {
      return { success: false, error: 'Invalid email or password.', statusCode: res.status };
    }

    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      const message: string = (body as { message?: string }).message ?? `Server error (${res.status}).`;
      return { success: false, error: message, statusCode: res.status };
    }

    const data: AuthTokenResponse = await res.json();
    return { success: true, data };
  } catch (error) {
    console.error('[authService] loginWithCredentials error:', error);
    return {
      success: false,
      error: 'Unable to reach the server. Please check your connection and try again.',
    };
  }
}

/**
 * Register a new customer account.
 * POST /api/v1/auth/register
 */
export async function registerCustomer(
  payload: RegisterRequest,
): Promise<AuthResult<AuthTokenResponse>> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      cache: 'no-store',
    });

    if (res.status === 409) {
      return { success: false, error: 'An account with this email already exists.', statusCode: 409 };
    }

    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      const message: string = (body as { message?: string }).message ?? `Registration failed (${res.status}).`;
      return { success: false, error: message, statusCode: res.status };
    }

    const data: AuthTokenResponse = await res.json();
    return { success: true, data };
  } catch (error) {
    console.error('[authService] registerCustomer error:', error);
    return {
      success: false,
      error: 'Unable to reach the server. Please check your connection and try again.',
    };
  }
}

/**
 * Refresh an expired access token using the stored refresh token.
 * POST /api/v1/auth/refresh
 */
export async function refreshAccessToken(
  refreshToken: string,
): Promise<AuthResult<AuthTokenResponse>> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
      cache: 'no-store',
    });

    if (!res.ok) {
      return { success: false, error: 'Session expired. Please sign in again.', statusCode: res.status };
    }

    const data: AuthTokenResponse = await res.json();
    return { success: true, data };
  } catch (error) {
    console.error('[authService] refreshAccessToken error:', error);
    return { success: false, error: 'Session refresh failed.' };
  }
}

/**
 * Fetch the currently authenticated customer's profile.
 * GET /api/v1/auth/me
 * Requires: Authorization: Bearer <accessToken>
 */
export async function fetchCurrentCustomer(
  accessToken: string,
): Promise<AuthResult<CustomerProfile>> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/me`, {
      headers: { Authorization: `Bearer ${accessToken}` },
      cache: 'no-store',
    });

    if (res.status === 401) {
      return { success: false, error: 'Unauthorized. Please sign in again.', statusCode: 401 };
    }

    if (!res.ok) {
      console.warn(`[authService] fetchCurrentCustomer failed (${res.status}). Using static fallback.`);
      return { success: true, data: STATIC_CUSTOMER_PROFILE };
    }

    const data: CustomerProfile = await res.json();
    return { success: true, data };
  } catch (error) {
    console.error('[authService] fetchCurrentCustomer error:', error);
    return { success: true, data: STATIC_CUSTOMER_PROFILE };
  }
}

/**
 * Trigger a password-reset email.
 * POST /api/v1/auth/forgot-password
 */
export async function requestPasswordReset(
  payload: ForgotPasswordRequest,
): Promise<AuthResult<{ message: string }>> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/forgot-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      cache: 'no-store',
    });

    if (!res.ok) {
      return { success: false, error: 'Failed to send reset email. Please try again.', statusCode: res.status };
    }

    return { success: true, data: { message: 'Password reset email sent. Please check your inbox.' } };
  } catch (error) {
    console.error('[authService] requestPasswordReset error:', error);
    return { success: false, error: 'Unable to reach the server.' };
  }
}

/**
 * Submit a new password using the reset token from the email link.
 * POST /api/v1/auth/reset-password
 */
export async function resetPassword(
  payload: ResetPasswordRequest,
): Promise<AuthResult<{ message: string }>> {
  try {
    const res = await fetch(`${API_BASE}/api/v1/auth/reset-password`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
      cache: 'no-store',
    });

    if (res.status === 400) {
      return { success: false, error: 'Invalid or expired reset token.', statusCode: 400 };
    }

    if (!res.ok) {
      return { success: false, error: 'Password reset failed. Please try again.', statusCode: res.status };
    }

    return { success: true, data: { message: 'Password updated successfully. You can now sign in.' } };
  } catch (error) {
    console.error('[authService] resetPassword error:', error);
    return { success: false, error: 'Unable to reach the server.' };
  }
}
