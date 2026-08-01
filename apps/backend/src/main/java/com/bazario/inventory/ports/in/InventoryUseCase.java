package com.bazario.inventory.ports.in;

import com.bazario.inventory.domain.model.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryUseCase {
    Inventory initializeInventory(UUID productId, Integer initialStock, Integer lowStockThreshold);
    Inventory updateStock(UUID productId, Integer quantityChange);
    Optional<Inventory> getInventoryByProductId(UUID productId);
    boolean isStockAvailable(UUID productId, Integer requestedQuantity);
}
