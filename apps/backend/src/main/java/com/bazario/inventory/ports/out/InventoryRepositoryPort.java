package com.bazario.inventory.ports.out;

import com.bazario.inventory.domain.model.Inventory;

import java.util.Optional;
import java.util.UUID;

public interface InventoryRepositoryPort {
    Inventory save(Inventory inventory);
    Optional<Inventory> findByProductId(UUID productId);
    Optional<Inventory> findById(UUID id);
}
