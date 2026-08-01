package com.bazario.catalog.application.model;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application-layer command object carrying the fields for a partial product
 * update initiated by the Admin Portal.
 *
 * Lives in the application layer so both the inbound Port (ProductUseCase)
 * and the domain Service (ProductService) can reference it without depending
 * on any Adapter DTO (e.g. CatalogDtos.AdminUpdateProductRequest).
 *
 * Architectural rule: this type must NEVER import anything from *.adapters.*
 */
public record AdminUpdateProductCommand(
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
