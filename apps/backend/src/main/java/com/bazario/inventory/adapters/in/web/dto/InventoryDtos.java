package com.bazario.inventory.adapters.in.web.dto;

import java.time.ZonedDateTime;
import java.util.UUID;

public class InventoryDtos {

    public record UpdateStockRequest(
            Integer quantityChange
    ) {}

    public record InitializeInventoryRequest(
            UUID productId,
            Integer initialStock,
            Integer lowStockThreshold
    ) {}

    public record InventoryResponse(
            UUID id,
            UUID productId,
            Integer stockQuantity,
            Integer lowStockThreshold,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {}
}
