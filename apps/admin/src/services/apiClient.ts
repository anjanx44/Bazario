/**
 * Bazario Admin Portal — Secure API Client
 *
 * Central fetch wrapper that automatically injects the admin JWT
 * Authorization header on every request. The token is read from
 * localStorage (key: "bazario_admin_token") on the client side,
 * or passed explicitly for server-side calls.
 *
 * Usage:
 *   import { adminFetch } from '@/services/apiClient';
 *   const res = await adminFetch('/api/v1/admin/dashboard/metrics');
 */

const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? 'http://localhost:8080';

export const ADMIN_TOKEN_KEY = 'bazario_admin_token';

// ─── Types ────────────────────────────────────────────────────────────────────

export interface ApiClientOptions extends Omit<RequestInit, 'headers'> {
  /** Explicit Bearer token (for server components / SSR). */
  token?: string;
  /** Extra headers merged on top of defaults. */
  headers?: Record<string, string>;
}

export type AdminFetchResult<T> =
  | { success: true; data: T; status: number }
  | { success: false; error: string; status: number };

// ─── Core Client ─────────────────────────────────────────────────────────────

/**
 * Authenticated fetch wrapper for all admin API calls.
 * Automatically attaches Authorization: Bearer <token>.
 * Returns a typed discriminated union — never throws.
 */
export async function adminFetch<T>(
  path: string,
  options: ApiClientOptions = {},
): Promise<AdminFetchResult<T>> {
  const { token: explicitToken, headers: extraHeaders, ...fetchOptions } = options;

  // Resolve token: explicit > localStorage (client only)
  let token = explicitToken;
  if (!token && typeof window !== 'undefined') {
    token = localStorage.getItem(ADMIN_TOKEN_KEY) ?? undefined;
  }

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...extraHeaders,
  };

  try {
    const res = await fetch(`${API_BASE}${path}`, {
      ...fetchOptions,
      headers,
      cache: fetchOptions.cache ?? 'no-store',
    });

    if (res.status === 401) {
      // Token expired — clear storage and redirect to admin login
      if (typeof window !== 'undefined') {
        localStorage.removeItem(ADMIN_TOKEN_KEY);
        window.location.href = '/admin/login';
      }
      return { success: false, error: 'Session expired. Please sign in again.', status: 401 };
    }

    if (res.status === 403) {
      return { success: false, error: 'Access denied. Insufficient permissions.', status: 403 };
    }

    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      const message: string = (body as { message?: string }).message ?? `Request failed (${res.status}).`;
      return { success: false, error: message, status: res.status };
    }

    // Handle 204 No Content
    if (res.status === 204) {
      return { success: true, data: null as unknown as T, status: 204 };
    }

    const data: T = await res.json();
    return { success: true, data, status: res.status };
  } catch (error) {
    console.error(`[adminFetch] ${path} error:`, error);
    return {
      success: false,
      error: 'Unable to reach the server. Please check your connection.',
      status: 0,
    };
  }
}
