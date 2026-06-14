package com.bazario.catalog.adapters.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class CatalogDtos {

    public record CreateProductRequest(
            String name,
            String slug,
            String description,
            BigDecimal basePrice,
            BigDecimal discountPrice,
            String sku,
            UUID categoryId
    ) {}

    public record ProductResponse(
            UUID id,
            String name,
            String slug,
            String description,
            BigDecimal basePrice,
            BigDecimal discountPrice,
            String sku,
            boolean active,
            CategoryResponse category
    ) {}

    public record CategoryResponse(
            UUID id,
            String name,
            String slug
    ) {}
}
