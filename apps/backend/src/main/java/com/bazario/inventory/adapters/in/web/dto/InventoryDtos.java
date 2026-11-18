package com.bazario.inventory.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.ZonedDateTime;
import java.util.UUID;

public class InventoryDtos {

    public record UpdateStockRequest(
            @NotNull Integer quantityChange
    ) {}

    public record InitializeInventoryRequest(
            @NotNull UUID productId,
            @NotNull @PositiveOrZero Integer initialStock,
            @NotNull @PositiveOrZero Integer lowStockThreshold
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
