/**
 * Bazario Admin Portal — Dashboard Service
 *
 * Fetches KPI metrics and recent orders for the admin dashboard.
 * Falls back to STATIC_DASHBOARD_DATA / STATIC_RECENT_ORDERS on any failure
 * so the dashboard never renders broken or empty.
 *
 * All endpoints require: Authorization: Bearer <adminJwt>
 * Scope: ROLE_ADMIN or ROLE_SUPER_ADMIN
 */

import { adminFetch } from './apiClient';
import {
  DashboardMetrics,
  RecentOrder,
  PagedResponse,
  OrderFilterParams,
} from '@/types';

// ─── Static Fallback Data ─────────────────────────────────────────────────────

export const STATIC_DASHBOARD_METRICS: DashboardMetrics = {
  totalRevenue: 124592.0,
  revenueChange: 12.5,
  totalOrders: 1284,
  ordersChange: 8.2,
  totalCustomers: 3847,
  customersChange: 5.1,
  activeProducts: 142,
  productsChange: -2.3,
};

export const STATIC_RECENT_ORDERS: RecentOrder[] = [
  {
    id: 'ord-001',
    orderNumber: 'BZ-2024-00891',
    customerName: 'Sophia Chen',
    customerEmail: 'sophia.chen@example.com',
    totalAmount: 598.0,
    status: 'PAID',
    itemCount: 2,
    createdAt: new Date(Date.now() - 1000 * 60 * 15).toISOString(),
  },
  {
    id: 'ord-002',
    orderNumber: 'BZ-2024-00890',
    customerName: 'Marcus Rivera',
    customerEmail: 'marcus.r@example.com',
    totalAmount: 299.0,
    status: 'SHIPPED',
    itemCount: 1,
    createdAt: new Date(Date.now() - 1000 * 60 * 42).toISOString(),
  },
  {
    id: 'ord-003',
    orderNumber: 'BZ-2024-00889',
    customerName: 'Aisha Patel',
    customerEmail: 'aisha.patel@example.com',
    totalAmount: 845.0,
    status: 'PENDING',
    itemCount: 3,
    createdAt: new Date(Date.now() - 1000 * 60 * 90).toISOString(),
  },
  {
    id: 'ord-004',
    orderNumber: 'BZ-2024-00888',
    customerName: 'James Okafor',
    customerEmail: 'j.okafor@example.com',
    totalAmount: 120.0,
    status: 'CANCELLED',
    itemCount: 1,
    createdAt: new Date(Date.now() - 1000 * 60 * 180).toISOString(),
  },
  {
    id: 'ord-005',
    orderNumber: 'BZ-2024-00887',
    customerName: 'Elena Vasquez',
    customerEmail: 'elena.v@example.com',
    totalAmount: 1199.0,
    status: 'PAID',
    itemCount: 4,
    createdAt: new Date(Date.now() - 1000 * 60 * 240).toISOString(),
  },
];

// ─── Service Functions ────────────────────────────────────────────────────────

/**
 * Fetch aggregated KPI metrics for the admin dashboard header cards.
 * GET /api/v1/admin/dashboard/metrics
 *
 * Response shape: DashboardMetrics
 */
export async function fetchDashboardMetrics(token?: string): Promise<DashboardMetrics> {
  const result = await adminFetch<DashboardMetrics>(
    '/api/v1/admin/dashboard/metrics',
    { token },
  );

  if (!result.success) {
    console.warn('[dashboardService] fetchDashboardMetrics failed:', result.error, '— using static fallback.');
    return STATIC_DASHBOARD_METRICS;
  }

  return result.data;
}

/**
 * Fetch a paginated list of recent orders for the dashboard table.
 * GET /api/v1/admin/orders?page=0&size=10&sort=createdAt,desc&status=...
 */
export async function fetchRecentOrders(
  params: OrderFilterParams = {},
  token?: string,
): Promise<{ orders: RecentOrder[]; totalElements: number; totalPages: number }> {
  const {
    page = 0,
    size = 10,
    sort = 'createdAt,desc',
    status,
    search,
    fromDate,
    toDate,
  } = params;

  const query = new URLSearchParams({
    page: String(page),
    size: String(size),
    sort,
  });
  if (status) query.set('status', status);
  if (search) query.set('search', search);
  if (fromDate) query.set('fromDate', fromDate);
  if (toDate) query.set('toDate', toDate);

  const result = await adminFetch<PagedResponse<RecentOrder>>(
    `/api/v1/admin/orders?${query.toString()}`,
    { token },
  );

  if (!result.success) {
    console.warn('[dashboardService] fetchRecentOrders failed:', result.error, '— using static fallback.');
    return {
      orders: STATIC_RECENT_ORDERS,
      totalElements: STATIC_RECENT_ORDERS.length,
      totalPages: 1,
    };
  }

  const paged = result.data;

  if (!paged.content || paged.content.length === 0) {
    console.warn('[dashboardService] fetchRecentOrders returned empty. Using static fallback.');
    return {
      orders: STATIC_RECENT_ORDERS,
      totalElements: STATIC_RECENT_ORDERS.length,
      totalPages: 1,
    };
  }

  return {
    orders: paged.content,
    totalElements: paged.totalElements,
    totalPages: paged.totalPages,
  };
}

/**
 * Update the status of a single order.
 * PATCH /api/v1/admin/orders/{orderId}/status
 * Body: { status: OrderStatus }
 */
export async function updateOrderStatus(
  orderId: string,
  status: RecentOrder['status'],
  token?: string,
): Promise<{ success: boolean; error?: string }> {
  const result = await adminFetch<void>(
    `/api/v1/admin/orders/${orderId}/status`,
    {
      method: 'PATCH',
      body: JSON.stringify({ status }),
      token,
    },
  );

  if (!result.success) {
    console.error('[dashboardService] updateOrderStatus failed:', result.error);
    return { success: false, error: result.error };
  }

  return { success: true };
}
