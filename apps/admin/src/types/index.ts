/**
 * Bazario Admin Portal — Shared TypeScript Types
 * These interfaces mirror the Java DTOs from the Spring Boot backend.
 */

// ─── Pagination ───────────────────────────────────────────────────────────────

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // current page (0-indexed)
}

// ─── Auth ─────────────────────────────────────────────────────────────────────

export interface AdminAuthTokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: 'Bearer';
  expiresIn: number;
  admin: AdminProfile;
}

export interface AdminProfile {
  id: string;
  email: string;
  role: 'SUPER_ADMIN' | 'ADMIN' | 'MANAGER';
  createdAt: string;
}

// ─── Dashboard ────────────────────────────────────────────────────────────────

export interface DashboardMetrics {
  totalRevenue: number;
  revenueChange: number; // percentage vs previous period
  totalOrders: number;
  ordersChange: number;
  totalCustomers: number;
  customersChange: number;
  activeProducts: number;
  productsChange: number;
}

export type OrderStatus = 'PENDING' | 'PAID' | 'SHIPPED' | 'CANCELLED';

export interface RecentOrder {
  id: string;
  orderNumber: string;
  customerName: string;
  customerEmail: string;
  totalAmount: number;
  status: OrderStatus;
  itemCount: number;
  createdAt: string;
}

// ─── Products ─────────────────────────────────────────────────────────────────

export interface AdminProduct {
  id: string;
  name: string;
  slug: string;
  description: string;
  basePrice: number;
  discountPrice: number | null;
  sku: string;
  active: boolean;
  imageUrl?: string;
  stockQuantity: number;
  lowStockThreshold: number;
  category: AdminCategory;
  createdAt: string;
  updatedAt: string;
}

export interface AdminCategory {
  id: string;
  name: string;
  slug: string;
}

export interface CreateProductRequest {
  name: string;
  slug: string;
  description: string;
  basePrice: number;
  discountPrice?: number;
  sku: string;
  categoryId: string;
  initialStock: number;
  lowStockThreshold: number;
}

export interface UpdateProductRequest extends Partial<CreateProductRequest> {
  active?: boolean;
}

export interface ProductFilterParams {
  page?: number;
  size?: number;
  sort?: string;
  search?: string;
  categoryId?: string;
  active?: boolean;
  lowStock?: boolean;
}

// ─── Inventory ────────────────────────────────────────────────────────────────

export interface InventoryItem {
  id: string;
  productId: string;
  productName: string;
  sku: string;
  stockQuantity: number;
  lowStockThreshold: number;
  status: 'IN_STOCK' | 'LOW_STOCK' | 'OUT_OF_STOCK';
  updatedAt: string;
}

export interface UpdateStockRequest {
  quantityChange: number; // positive = restock, negative = deduct
}

// ─── Orders ───────────────────────────────────────────────────────────────────

export interface AdminOrder {
  id: string;
  orderNumber: string;
  customerId: string;
  customerName: string;
  customerEmail: string;
  totalAmount: number;
  status: OrderStatus;
  shippingAddressSnapshot: string;
  items: AdminOrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface AdminOrderItem {
  id: string;
  productId: string;
  productName: string;
  price: number;
  quantity: number;
}

export interface OrderFilterParams {
  page?: number;
  size?: number;
  sort?: string;
  status?: OrderStatus;
  search?: string;
  fromDate?: string;
  toDate?: string;
}
