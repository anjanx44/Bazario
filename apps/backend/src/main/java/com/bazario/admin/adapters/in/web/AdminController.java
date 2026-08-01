package com.bazario.admin.adapters.in.web;

import com.bazario.admin.adapters.in.web.dto.AdminDtos;
import com.bazario.catalog.application.model.AdminUpdateProductCommand;
import com.bazario.catalog.application.model.CategoryView;
import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.ports.in.ProductUseCase;
import com.bazario.inventory.domain.model.Inventory;
import com.bazario.inventory.ports.in.InventoryUseCase;
import com.bazario.orders.application.model.DashboardMetrics;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderItem;
import com.bazario.orders.domain.model.OrderStatus;
import com.bazario.orders.ports.in.OrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Bazario Admin REST Controller
 *
 * All endpoints under /api/v1/admin/** require ROLE_ADMIN (enforced by
 * SecurityConfig + @PreAuthorize). The JWT Bearer token must be sent in
 * the Authorization header on every request.
 *
 * Architectural rule enforced here:
 *   This controller is the ONLY place where cross-context translation occurs.
 *   It receives domain objects / application-layer models from Use Cases and
 *   maps them to flat AdminDtos records before returning them to the client.
 *   No other module's Adapter DTOs (CatalogDtos, OrderDtos, InventoryDtos)
 *   are imported here — all mapping is done inline via private helper methods.
 *
 * ── Dashboard ──────────────────────────────────────────────────────────────
 *   GET  /api/v1/admin/dashboard/metrics        — KPI summary cards
 *
 * ── Orders ─────────────────────────────────────────────────────────────────
 *   GET   /api/v1/admin/orders                  — paginated order list
 *   PATCH /api/v1/admin/orders/{id}/status      — update order status
 *
 * ── Products ───────────────────────────────────────────────────────────────
 *   GET    /api/v1/admin/products               — paginated product list (all)
 *   GET    /api/v1/admin/products/{id}          — single product detail
 *   POST   /api/v1/admin/products               — create product + inventory
 *   PUT    /api/v1/admin/products/{id}          — update product
 *   DELETE /api/v1/admin/products/{id}          — soft-delete (deactivate)
 *
 * ── Categories ─────────────────────────────────────────────────────────────
 *   GET  /api/v1/admin/categories               — all categories (for dropdowns)
 *
 * ── Inventory ──────────────────────────────────────────────────────────────
 *   GET   /api/v1/admin/inventory/{productId}        — inventory for product
 *   PATCH /api/v1/admin/inventory/{productId}/stock  — adjust stock quantity
 */
@Tag(name = "Admin Portal", description = "Admin-scoped endpoints — requires ROLE_ADMIN Bearer JWT")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ProductUseCase   productUseCase;
    private final OrderUseCase     orderUseCase;
    private final InventoryUseCase inventoryUseCase;

    // ═══════════════════════════════════════════════════════════════════════
    // DASHBOARD
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/admin/dashboard/metrics
     * Returns aggregated KPI metrics for the dashboard header cards.
     */
    @Operation(summary = "Get dashboard KPI metrics")
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<AdminDtos.DashboardMetricsResponse> getDashboardMetrics() {
        DashboardMetrics metrics = orderUseCase.getDashboardMetrics();
        return ResponseEntity.ok(toDto(metrics));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // ORDERS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/admin/orders
     * Returns a paginated list of all orders with optional filters.
     *
     * Query params:
     *   page     (int,    default 0)
     *   size     (int,    default 10)
     *   sort     (string, default "createdAt,desc")
     *   status   (string, optional — PENDING|PAID|SHIPPED|CANCELLED)
     *   search   (string, optional — matches orderNumber or customer email)
     *   fromDate (string, optional — ISO-8601 date)
     *   toDate   (string, optional — ISO-8601 date)
     */
    @Operation(summary = "List all orders (paginated)")
    @GetMapping("/orders")
    public ResponseEntity<AdminDtos.PagedOrderResponse> listOrders(
            @RequestParam(defaultValue = "0")              int page,
            @RequestParam(defaultValue = "10")             int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false)                String status,
            @RequestParam(required = false)                String search,
            @RequestParam(required = false)                String fromDate,
            @RequestParam(required = false)                String toDate
    ) {
        String[]       sortParts  = sort.split(",");
        Sort.Direction direction  = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable       pageable   = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
        OrderStatus    orderStatus = parseOrderStatus(status);

        Page<Order> orderPage = orderUseCase.listOrders(pageable, orderStatus, search, fromDate, toDate);

        AdminDtos.PagedOrderResponse response = new AdminDtos.PagedOrderResponse(
                orderPage.getContent().stream().map(this::toDto).toList(),
                orderPage.getTotalElements(),
                orderPage.getTotalPages(),
                orderPage.getSize(),
                orderPage.getNumber()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/v1/admin/orders/{orderId}/status
     * Updates the status of a single order.
     *
     * Request body: { "status": "SHIPPED" }
     * Response 200: AdminOrderResponse
     * Response 404: order not found
     */
    @Operation(summary = "Update order status")
    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<AdminDtos.AdminOrderResponse> updateOrderStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody AdminDtos.UpdateOrderStatusRequest request
    ) {
        OrderStatus newStatus = parseOrderStatus(request.status());
        if (!orderUseCase.updateOrderStatus(orderId, newStatus)) {
            return ResponseEntity.notFound().build();
        }
        return orderUseCase.getOrderById(orderId)
                .map(order -> ResponseEntity.ok(toDto(order)))
                .orElse(ResponseEntity.notFound().build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PRODUCTS
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/admin/products
     * Returns a paginated list of ALL products (active + inactive).
     *
     * Query params:
     *   page       (int,     default 0)
     *   size       (int,     default 20)
     *   sort       (string,  default "createdAt,desc")
     *   search     (string,  optional — matches name or SKU)
     *   categoryId (UUID,    optional)
     *   active     (boolean, optional)
     *   lowStock   (boolean, optional — stock <= threshold)
     */
    @Operation(summary = "List all products for admin (paginated)")
    @GetMapping("/products")
    public ResponseEntity<AdminDtos.PagedAdminProductResponse> listAdminProducts(
            @RequestParam(defaultValue = "0")              int page,
            @RequestParam(defaultValue = "20")             int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false)                String search,
            @RequestParam(required = false)                UUID categoryId,
            @RequestParam(required = false)                Boolean active,
            @RequestParam(required = false)                Boolean lowStock
    ) {
        String[]       sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        Page<Product> productPage = productUseCase.findAllProducts(
                pageable, search, categoryId, active, lowStock);

        AdminDtos.PagedAdminProductResponse response = new AdminDtos.PagedAdminProductResponse(
                productPage.getContent().stream().map(this::toDto).toList(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getSize(),
                productPage.getNumber()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/admin/products/{productId}
     * Returns a single product by ID including stock info.
     * Response 200: AdminProductResponse | Response 404: not found
     */
    @Operation(summary = "Get single product by ID (admin view)")
    @GetMapping("/products/{productId}")
    public ResponseEntity<AdminDtos.AdminProductResponse> getAdminProduct(
            @PathVariable UUID productId) {
        return productUseCase.getProductById(productId)
                .map(product -> ResponseEntity.ok(toDto(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/admin/products
     * Creates a new product and initializes its inventory record.
     * Response 201: AdminProductResponse | Response 409: SKU already exists
     */
    @Operation(summary = "Create a new product with initial inventory")
    @PostMapping("/products")
    public ResponseEntity<AdminDtos.AdminProductResponse> createAdminProduct(
            @Valid @RequestBody AdminDtos.AdminCreateProductRequest request) {

        Product product = toDomain(request);
        UUID productId = productUseCase.createProduct(
                product, request.initialStock(), request.lowStockThreshold());
        Product created = productUseCase.getProductById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found after create: " + productId));

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(created));
    }

    /**
     * PUT /api/v1/admin/products/{productId}
     * Updates an existing product. All fields are optional (partial update).
     * Response 200: AdminProductResponse | Response 404: not found
     */
    @Operation(summary = "Update an existing product")
    @PutMapping("/products/{productId}")
    public ResponseEntity<AdminDtos.AdminProductResponse> updateAdminProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody AdminDtos.AdminUpdateProductRequest request) {

        AdminUpdateProductCommand command = new AdminUpdateProductCommand(
                request.name(),
                request.slug(),
                request.description(),
                request.basePrice(),
                request.discountPrice(),
                request.sku(),
                request.categoryId(),
                request.active(),
                request.lowStockThreshold()
        );
        return productUseCase.updateProduct(productId, command)
                .map(updated -> ResponseEntity.ok(toDto(updated)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/v1/admin/products/{productId}
     * Soft-deletes a product by setting active = false.
     * Response 204: no content | Response 404: not found
     */
    @Operation(summary = "Soft-delete (deactivate) a product")
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<Void> deleteAdminProduct(@PathVariable UUID productId) {
        if (productUseCase.getProductById(productId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        productUseCase.deactivateProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CATEGORIES
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/admin/categories
     * Returns all categories for product form dropdowns.
     * Response 200: List<AdminCategoryListItem>
     */
    @Operation(summary = "List all categories")
    @GetMapping("/categories")
    public ResponseEntity<List<AdminDtos.AdminCategoryListItem>> listCategories() {
        List<CategoryView> views = productUseCase.listAllCategories();
        List<AdminDtos.AdminCategoryListItem> response = views.stream()
                .map(v -> new AdminDtos.AdminCategoryListItem(v.id(), v.name(), v.slug()))
                .toList();
        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // INVENTORY
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * GET /api/v1/admin/inventory/{productId}
     * Returns inventory details for a specific product.
     * Response 200: AdminInventoryResponse | Response 404: not found
     */
    @Operation(summary = "Get inventory for a product")
    @GetMapping("/inventory/{productId}")
    public ResponseEntity<AdminDtos.AdminInventoryResponse> getInventory(
            @PathVariable UUID productId) {
        return inventoryUseCase.getInventoryByProductId(productId)
                .map(inv -> ResponseEntity.ok(toDto(inv)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PATCH /api/v1/admin/inventory/{productId}/stock
     * Adjusts stock quantity for a product.
     * Positive quantityChange = restock; negative = deduct.
     * Response 200: AdminInventoryResponse | Response 404: not found
     */
    @Operation(summary = "Adjust stock quantity for a product")
    @PatchMapping("/inventory/{productId}/stock")
    public ResponseEntity<AdminDtos.AdminInventoryResponse> adjustStock(
            @PathVariable UUID productId,
            @Valid @RequestBody AdminDtos.AdminAdjustStockRequest request) {
        return inventoryUseCase.getInventoryByProductId(productId)
                .map(existing -> {
                    inventoryUseCase.updateStock(productId, request.quantityChange());
                    Inventory updated = inventoryUseCase.getInventoryByProductId(productId)
                            .orElseThrow(() -> new IllegalStateException("Inventory not found after update: " + productId));
                    return ResponseEntity.ok(toDto(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PRIVATE MAPPING HELPERS  (Anti-Corruption Layer — inline translators)
    //
    // These methods are the ONLY place where cross-context field mapping
    // occurs.  They translate domain/application objects into flat AdminDtos
    // records.  No other module's Adapter DTOs are used anywhere in this file.
    // ═══════════════════════════════════════════════════════════════════════

    private AdminDtos.DashboardMetricsResponse toDto(DashboardMetrics m) {
        return new AdminDtos.DashboardMetricsResponse(
                m.totalRevenue(),   m.revenueChange(),
                m.totalOrders(),    m.ordersChange(),
                m.totalCustomers(), m.customersChange(),
                m.activeProducts(), m.productsChange()
        );
    }

    private AdminDtos.AdminOrderResponse toDto(Order order) {
        List<AdminDtos.AdminOrderItemResponse> items = order.getItems() == null
                ? List.of()
                : order.getItems().stream().map(this::toDto).toList();
        return new AdminDtos.AdminOrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomerId(),
                order.getTotalAmount(),
                order.getStatus() != null ? order.getStatus().name() : null,
                order.getShippingAddressSnapshot(),
                items,
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private AdminDtos.AdminOrderItemResponse toDto(OrderItem item) {
        return new AdminDtos.AdminOrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getPrice(),
                item.getQuantity()
        );
    }

    private AdminDtos.AdminProductResponse toDto(Product product) {
        AdminDtos.AdminCategoryResponse categoryDto = product.getCategory() == null ? null
                : new AdminDtos.AdminCategoryResponse(
                        product.getCategory().getId(),
                        product.getCategory().getName(),
                        product.getCategory().getSlug());
        return new AdminDtos.AdminProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getBasePrice(),
                product.getDiscountPrice(),
                product.getSku(),
                product.isActive(),
                null,   // imageUrl — not present on Product domain model; enrich from media service if needed
                null,   // stockQuantity — enriched separately via InventoryUseCase if needed
                null,   // lowStockThreshold — enriched separately via InventoryUseCase if needed
                categoryDto,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private AdminDtos.AdminInventoryResponse toDto(Inventory inv) {
        return new AdminDtos.AdminInventoryResponse(
                inv.getProductId(),
                inv.getStockQuantity(),
                inv.getLowStockThreshold(),
                inv.getStockQuantity() != null
                        && inv.getLowStockThreshold() != null
                        && inv.getStockQuantity() <= inv.getLowStockThreshold()
        );
    }

    /**
     * Case-insensitive {@link OrderStatus} parsing.
     * Blank/null values mean "no filter"; unknown values throw a
     * {@link IllegalArgumentException} that the global handler turns
     * into a 400 Bad Request.
     */
    private OrderStatus parseOrderStatus(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return OrderStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid order status: '" + raw + "'. Allowed: " + Arrays.toString(OrderStatus.values()));
        }
    }

    private Product toDomain(AdminDtos.AdminCreateProductRequest request) {
        com.bazario.catalog.domain.model.Category category =
                new com.bazario.catalog.domain.model.Category();
        category.setId(request.categoryId());

        Product product = new Product();
        product.setName(request.name());
        product.setSlug(request.slug());
        product.setDescription(request.description());
        product.setBasePrice(request.basePrice());
        product.setDiscountPrice(request.discountPrice());
        product.setSku(request.sku());
        product.setCategory(category);
        return product;
    }
}
