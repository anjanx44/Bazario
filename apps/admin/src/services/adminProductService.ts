/**
 * Bazario Admin Portal — Product & Inventory Management Service
 *
 * Provides full CRUD for products and stock management via the
 * admin-scoped Spring Boot endpoints (/api/v1/admin/products).
 * Falls back to STATIC_ADMIN_PRODUCTS_DATA on read failures.
 *
 * All endpoints require: Authorization: Bearer <adminJwt>
 * Scope: ROLE_ADMIN or ROLE_SUPER_ADMIN
 */

import { adminFetch } from './apiClient';
import {
  AdminProduct,
  AdminCategory,
  CreateProductRequest,
  UpdateProductRequest,
  ProductFilterParams,
  InventoryItem,
  UpdateStockRequest,
  PagedResponse,
} from '@/types';

// ─── Static Fallback Data ─────────────────────────────────────────────────────

export const STATIC_ADMIN_PRODUCTS_DATA: AdminProduct[] = [
  {
    id: '1',
    name: 'AeroSound Elite 5',
    slug: 'aerosound-elite-5',
    description: 'Premium noise-cancelling headphones with ANC 4.0 chip.',
    basePrice: 350.0,
    discountPrice: 299.0,
    sku: 'AS-ELITE5-BLK',
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDsNhCQBBA6b5RUM1vjvUGW0MCUcepD12fgWo3Mu5DHTtcp-gpkMy77cbW8wSNfB711jz0u2u_KpiyggyifdhmitO6I_3BvN5Kd9mBuNI-BqTYFHw06BAGTOYo4Ug01iAMLtPKyQJxOEyIWUxrwxH80Uct8_mcfVBp2tEci2v8Ol0wwUOqRZ-f8Rg8YySykTSNVij7QkphlJc185N1cHVEIcLIi4zlCBZnMYqSIKXS4UBTKm8hHK61uH9s_BXfxCuPbWDMPGnkhHNM',
    stockQuantity: 45,
    lowStockThreshold: 10,
    category: { id: 'cat-1', name: 'Audio', slug: 'audio' },
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-06-01T08:30:00Z',
  },
  {
    id: '2',
    name: 'Smart Watch Pro',
    slug: 'smart-watch-pro',
    description: 'Advanced smartwatch with health monitoring and GPS.',
    basePrice: 399.0,
    discountPrice: 349.0,
    sku: 'SW-PRO-SIL',
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuCXgyKB3FGb1l_drwImZYe-fWae-tzTftc6WRckjsDVjfQvbCXRu252685h2GSjA_neQem_2ADa3KWsXmNEzSET4gdKVXT-l69cbCIY_3ObyNbXmhLm8lFUWhWWdDjzpu-d7UxtZFq3ERdLDolgHx6WyjSS8-PhFFpZdjJ963RXGbtQQ9Igps4htT0fiZpzsu7XIvcbiaNPcOyWWPQc4bjTfNiKQX25epcCTdksEDeqLY8x6uNOYiSpwIpv2m2u45sYF80vm23hd5o',
    stockQuantity: 8,
    lowStockThreshold: 10,
    category: { id: 'cat-2', name: 'Wearables', slug: 'wearables' },
    createdAt: '2024-02-10T09:00:00Z',
    updatedAt: '2024-06-05T14:00:00Z',
  },
  {
    id: '3',
    name: 'Titan Mesh Router',
    slug: 'titan-mesh-router',
    description: 'Whole-home mesh Wi-Fi 6E router system.',
    basePrice: 250.0,
    discountPrice: 199.0,
    sku: 'TITAN-MESH-001',
    active: true,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuBBS3ZgijprHGamll8xYB8gGBp6knSbfgCstM0hwmdayw89PMpFVGVTRqIw68mhyKJED_rFjwp7LUdwAA7POlTpptIIu_LQBci9r_K3mEM4nTS2d-Vx1-3eCG3NWw-IacMfP60z6H2tiRInLlRoTTC5GY6d35SwRhODuqZBO0iLHcW7OsgjvrUc1EUFVdDVyfnTPPB5I63WWvkXmxU3zHtHX5KW0O11OHH6DXFV0fwfR5M1On1NJog1s_PfuuCtQ5WGnoAGbV5sub4',
    stockQuantity: 32,
    lowStockThreshold: 5,
    category: { id: 'cat-3', name: 'Networking', slug: 'networking' },
    createdAt: '2024-03-01T11:00:00Z',
    updatedAt: '2024-06-10T10:00:00Z',
  },
  {
    id: '4',
    name: 'Pro-Book X1 Case',
    slug: 'pro-book-x1-case',
    description: 'Premium leather laptop case for 14-inch notebooks.',
    basePrice: 45.0,
    discountPrice: null,
    sku: 'PRO-CASE-004',
    active: false,
    imageUrl:
      'https://lh3.googleusercontent.com/aida-public/AB6AXuDr9C7zh1VL59wfqocKE7prp1bZKL_lWw1GBcpTFAG1aAWWLY2hWeE4ABUbdQq2_DNtENuGdU48_rT3NeM-yO9wDj71_8SHJddiX3fC88a3alWzLMISvV3H6Ws7PG1Jy3f1Bp8IysUbO_6aRvWEeILSwL_Hhk5dpfZItjFNrbsiC5u6szNZPIAV3FvLcK5AW8ZkciTj_XO0Xk-jR8JPJAuUOn89JqDXUTBRMfp53ddItJ1M5uBg7GbvnJC1I0YO_7S0FtJimH_B2YI',
    stockQuantity: 0,
    lowStockThreshold: 5,
    category: { id: 'cat-4', name: 'Accessories', slug: 'accessories' },
    createdAt: '2024-04-20T08:00:00Z',
    updatedAt: '2024-05-30T16:00:00Z',
  },
];

export const STATIC_ADMIN_CATEGORIES: AdminCategory[] = [
  { id: 'cat-1', name: 'Audio', slug: 'audio' },
  { id: 'cat-2', name: 'Wearables', slug: 'wearables' },
  { id: 'cat-3', name: 'Networking', slug: 'networking' },
  { id: 'cat-4', name: 'Accessories', slug: 'accessories' },
];

// ─── Service Functions ────────────────────────────────────────────────────────

/**
 * Fetch a paginated, filterable list of all products (active + inactive).
 * GET /api/v1/admin/products?page=0&size=20&sort=createdAt,desc&search=...&categoryId=...&active=...&lowStock=...
 */
export async function fetchAdminProducts(
  params: ProductFilterParams = {},
  token?: string,
): Promise<{ products: AdminProduct[]; totalElements: number; totalPages: number }> {
  const {
    page = 0,
    size = 20,
    sort = 'createdAt,desc',
    search,
    categoryId,
    active,
    lowStock,
  } = params;

  const query = new URLSearchParams({
    page: String(page),
    size: String(size),
    sort,
  });
  if (search) query.set('search', search);
  if (categoryId) query.set('categoryId', categoryId);
  if (active !== undefined) query.set('active', String(active));
  if (lowStock !== undefined) query.set('lowStock', String(lowStock));

  const result = await adminFetch<PagedResponse<AdminProduct>>(
    `/api/v1/admin/products?${query.toString()}`,
    { token },
  );

  if (!result.success) {
    console.warn('[adminProductService] fetchAdminProducts failed:', result.error, '— using static fallback.');
    return {
      products: STATIC_ADMIN_PRODUCTS_DATA,
      totalElements: STATIC_ADMIN_PRODUCTS_DATA.length,
      totalPages: 1,
    };
  }

  const paged = result.data;

  if (!paged.content || paged.content.length === 0) {
    console.warn('[adminProductService] fetchAdminProducts returned empty. Using static fallback.');
    return {
      products: STATIC_ADMIN_PRODUCTS_DATA,
      totalElements: STATIC_ADMIN_PRODUCTS_DATA.length,
      totalPages: 1,
    };
  }

  return {
    products: paged.content,
    totalElements: paged.totalElements,
    totalPages: paged.totalPages,
  };
}

/**
 * Fetch a single product by ID (admin view includes stock + inactive).
 * GET /api/v1/admin/products/{productId}
 */
export async function fetchAdminProductById(
  productId: string,
  token?: string,
): Promise<AdminProduct | null> {
  const result = await adminFetch<AdminProduct>(
    `/api/v1/admin/products/${productId}`,
    { token },
  );

  if (!result.success) {
    console.warn(`[adminProductService] fetchAdminProductById(${productId}) failed:`, result.error);
    return STATIC_ADMIN_PRODUCTS_DATA.find((p) => p.id === productId) ?? null;
  }

  return result.data;
}

/**
 * Create a new product with initial inventory.
 * POST /api/v1/admin/products
 * Body: CreateProductRequest
 */
export async function createAdminProduct(
  payload: CreateProductRequest,
  token?: string,
): Promise<{ success: boolean; data?: AdminProduct; error?: string }> {
  const result = await adminFetch<AdminProduct>(
    '/api/v1/admin/products',
    { method: 'POST', body: JSON.stringify(payload), token },
  );

  if (!result.success) {
    console.error('[adminProductService] createAdminProduct failed:', result.error);
    return { success: false, error: result.error };
  }

  return { success: true, data: result.data };
}

/**
 * Update an existing product's details.
 * PUT /api/v1/admin/products/{productId}
 * Body: UpdateProductRequest (partial)
 */
export async function updateAdminProduct(
  productId: string,
  payload: UpdateProductRequest,
  token?: string,
): Promise<{ success: boolean; data?: AdminProduct; error?: string }> {
  const result = await adminFetch<AdminProduct>(
    `/api/v1/admin/products/${productId}`,
    { method: 'PUT', body: JSON.stringify(payload), token },
  );

  if (!result.success) {
    console.error('[adminProductService] updateAdminProduct failed:', result.error);
    return { success: false, error: result.error };
  }

  return { success: true, data: result.data };
}

/**
 * Soft-delete (deactivate) a product.
 * DELETE /api/v1/admin/products/{productId}
 */
export async function deleteAdminProduct(
  productId: string,
  token?: string,
): Promise<{ success: boolean; error?: string }> {
  const result = await adminFetch<void>(
    `/api/v1/admin/products/${productId}`,
    { method: 'DELETE', token },
  );

  if (!result.success) {
    console.error('[adminProductService] deleteAdminProduct failed:', result.error);
    return { success: false, error: result.error };
  }

  return { success: true };
}

/**
 * Fetch all categories for the product form dropdown.
 * GET /api/v1/admin/categories
 */
export async function fetchAdminCategories(token?: string): Promise<AdminCategory[]> {
  const result = await adminFetch<AdminCategory[]>(
    '/api/v1/admin/categories',
    { token },
  );

  if (!result.success) {
    console.warn('[adminProductService] fetchAdminCategories failed:', result.error, '— using static fallback.');
    return STATIC_ADMIN_CATEGORIES;
  }

  if (!Array.isArray(result.data) || result.data.length === 0) {
    return STATIC_ADMIN_CATEGORIES;
  }

  return result.data;
}

/**
 * Fetch inventory details for a specific product.
 * GET /api/v1/admin/inventory/{productId}
 */
export async function fetchProductInventory(
  productId: string,
  token?: string,
): Promise<InventoryItem | null> {
  const result = await adminFetch<InventoryItem>(
    `/api/v1/admin/inventory/${productId}`,
    { token },
  );

  if (!result.success) {
    console.warn(`[adminProductService] fetchProductInventory(${productId}) failed:`, result.error);
    return null;
  }

  return result.data;
}

/**
 * Adjust stock quantity for a product (restock or deduct).
 * PATCH /api/v1/admin/inventory/{productId}/stock
 * Body: { quantityChange: number }  — positive = add, negative = remove
 */
export async function adjustProductStock(
  productId: string,
  payload: UpdateStockRequest,
  token?: string,
): Promise<{ success: boolean; data?: InventoryItem; error?: string }> {
  const result = await adminFetch<InventoryItem>(
    `/api/v1/admin/inventory/${productId}/stock`,
    { method: 'PATCH', body: JSON.stringify(payload), token },
  );

  if (!result.success) {
    console.error('[adminProductService] adjustProductStock failed:', result.error);
    return { success: false, error: result.error };
  }

  return { success: true, data: result.data };
}
