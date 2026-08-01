package com.bazario.catalog.adapters.in.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class CatalogDtos {

    // ── Storefront: Create Product ────────────────────────────────────────────

    public record CreateProductRequest(
            @NotBlank String name,
            @NotBlank String slug,
            String description,
            @NotNull @DecimalMin("0.01") BigDecimal basePrice,
            BigDecimal discountPrice,
            @NotBlank String sku,
            @NotNull UUID categoryId
    ) {}

    // ── Storefront: Product Response (public-facing) ──────────────────────────

    public record ProductResponse(
            UUID id,
            String name,
            String slug,
            String description,
            BigDecimal basePrice,
            BigDecimal discountPrice,
            String sku,
            boolean active,
            CategoryResponse category,
            ZonedDateTime createdAt
    ) {}

    // ── Storefront: Paginated Product List Response ───────────────────────────

    public record PagedProductResponse(
            List<ProductResponse> content,
            long totalElements,
            int totalPages,
            int size,
            int number
    ) {}

    // ── Storefront: Category Response ─────────────────────────────────────────

    public record CategoryResponse(
            UUID id,
            String name,
            String slug
    ) {}

    // ── Admin: Admin Product Response (includes stock) ────────────────────────

    public record AdminProductResponse(
            UUID id,
            String name,
            String slug,
            String description,
            BigDecimal basePrice,
            BigDecimal discountPrice,
            String sku,
            boolean active,
            String imageUrl,
            Integer stockQuantity,
            Integer lowStockThreshold,
            CategoryResponse category,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {}

    // ── Admin: Create Product Request (includes initial stock) ────────────────

    public record AdminCreateProductRequest(
            @NotBlank String name,
            @NotBlank String slug,
            String description,
            @NotNull @DecimalMin("0.01") BigDecimal basePrice,
            BigDecimal discountPrice,
            @NotBlank String sku,
            @NotNull UUID categoryId,
            @NotNull Integer initialStock,
            @NotNull Integer lowStockThreshold
    ) {}

    // ── Admin: Update Product Request (all fields optional) ───────────────────

    public record AdminUpdateProductRequest(
            String name,
            String slug,
            String description,
            BigDecimal basePrice,
            BigDecimal discountPrice,
            String sku,
            UUID categoryId,
            Boolean active,
            Integer lowStockThreshold
    ) {}
}
