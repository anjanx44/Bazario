package com.bazario.admin.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Admin-bounded-context DTOs.
 *
 * These records are FLAT and SELF-CONTAINED — they carry every field the Admin
 * API surface needs without importing any DTO or domain type from another
 * bounded context (catalog, orders, inventory).  All cross-context translation
 * is performed inside AdminController using the respective context's mapper.
 *
 * Architectural rule enforced here:
 *   Adapter layer (web DTOs) must NEVER import another module's Adapter DTOs
 *   or Domain models.  Only primitive JDK types, jakarta.validation, and
 *   types defined within this same package are permitted.
 */
public class AdminDtos {

    // ══════════════════════════════════════════════════════════════════════════
    // DASHBOARD
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Aggregated KPI metrics returned by GET /api/v1/admin/dashboard/metrics.
     * Flat record — no dependency on any other module's types.
     */
    public record DashboardMetricsResponse(
            double totalRevenue,
            double revenueChange,
            long   totalOrders,
            double ordersChange,
            long   totalCustomers,
            double customersChange,
            long   activeProducts,
            double productsChange
    ) {}

    // ══════════════════════════════════════════════════════════════════════════
    // ORDERS  (admin view — flat, no import of orders.adapters.in.web.dto)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Flat representation of a single order as seen by the Admin Portal.
     * Fields are duplicated from the Orders bounded context intentionally —
     * this is the Anti-Corruption Layer pattern: each context owns its view.
     */
    public record AdminOrderResponse(
            UUID          id,
            String        orderNumber,
            UUID          customerId,
            BigDecimal    totalAmount,
            String        status,           // String, not OrderStatus enum — no cross-module domain import
            String        shippingAddressSnapshot,
            List<AdminOrderItemResponse> items,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {}

    /** Flat order-item line as seen by the Admin Portal. */
    public record AdminOrderItemResponse(
            UUID       id,
            UUID       productId,
            String     productName,
            BigDecimal price,
            Integer    quantity
    ) {}

    /** Paginated wrapper for admin order list responses. */
    public record PagedOrderResponse(
            List<AdminOrderResponse> content,
            long totalElements,
            int  totalPages,
            int  size,
            int  number
    ) {}

    /**
     * Request body for PATCH /api/v1/admin/orders/{id}/status.
     * Status is accepted as a String and validated/converted in the controller
     * to avoid importing the Orders domain enum here.
     */
    public record UpdateOrderStatusRequest(
            @NotNull @NotBlank String status
    ) {}

    // ══════════════════════════════════════════════════════════════════════════
    // PRODUCTS  (admin view — flat, no import of catalog.adapters.in.web.dto)
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Flat representation of a product as seen by the Admin Portal.
     * Includes stock fields that the public storefront ProductResponse omits.
     */
    public record AdminProductResponse(
            UUID          id,
            String        name,
            String        slug,
            String        description,
            BigDecimal    basePrice,
            BigDecimal    discountPrice,
            String        sku,
            boolean       active,
            String        imageUrl,
            Integer       stockQuantity,
            Integer       lowStockThreshold,
            AdminCategoryResponse category,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {}

    /**
     * Flat category summary embedded inside AdminProductResponse.
     * Duplicates catalog's CategoryResponse intentionally — ACL pattern.
     */
    public record AdminCategoryResponse(
            UUID   id,
            String name,
            String slug
    ) {}

    /** Paginated wrapper for admin product list responses. */
    public record PagedAdminProductResponse(
            List<AdminProductResponse> content,
            long totalElements,
            int  totalPages,
            int  size,
            int  number
    ) {}

    /**
     * Request body for POST /api/v1/admin/products.
     * Flat record — owns all fields needed to create a product + initial stock.
     */
    public record AdminCreateProductRequest(
            @NotBlank String  name,
            @NotBlank String  slug,
            String            description,
            @NotNull  BigDecimal basePrice,
            BigDecimal        discountPrice,
            @NotBlank String  sku,
            @NotNull  UUID    categoryId,
            @NotNull  Integer initialStock,
            @NotNull  Integer lowStockThreshold
    ) {}

    /**
     * Request body for PUT /api/v1/admin/products/{id}.
     * All fields are optional to support partial updates.
     */
    public record AdminUpdateProductRequest(
            String     name,
            String     slug,
            String     description,
            BigDecimal basePrice,
            BigDecimal discountPrice,
            String     sku,
            UUID       categoryId,
            Boolean    active,
            Integer    lowStockThreshold
    ) {}

    // ══════════════════════════════════════════════════════════════════════════
    // CATEGORIES  (admin view — for dropdown lists)
    // ══════════════════════════════════════════════════════════════════════════

    /** Flat category entry used in admin product-form dropdowns. */
    public record AdminCategoryListItem(
            UUID   id,
            String name,
            String slug
    ) {}

    // ══════════════════════════════════════════════════════════════════════════
    // INVENTORY  (admin view — flat, no import of inventory.adapters.in.web.dto)
    // ══════════════════════════════════════════════════════════════════════════

    /** Flat inventory snapshot as seen by the Admin Portal. */
    public record AdminInventoryResponse(
            UUID    productId,
            Integer quantityOnHand,
            Integer lowStockThreshold,
            boolean lowStock
    ) {}

    /** Request body for PATCH /api/v1/admin/inventory/{productId}/stock. */
    public record AdminAdjustStockRequest(
            @NotNull Integer quantityChange
    ) {}
}
