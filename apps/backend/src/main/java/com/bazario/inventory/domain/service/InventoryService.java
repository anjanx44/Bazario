package com.bazario.inventory.domain.service;

import com.bazario.inventory.domain.model.Inventory;
import com.bazario.inventory.ports.in.InventoryUseCase;
import com.bazario.inventory.ports.out.InventoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    @Override
    @Transactional
    public Inventory initializeInventory(UUID productId, Integer initialStock, Integer lowStockThreshold) {
        log.info("Initializing inventory for product {} with stock {} and threshold {}", productId, initialStock, lowStockThreshold);
        
        inventoryRepositoryPort.findByProductId(productId).ifPresent(i -> {
            throw new IllegalStateException("Inventory already exists for product: " + productId);
        });

        Inventory inventory = Inventory.builder()
                .productId(productId)
                .stockQuantity(initialStock != null ? initialStock : 0)
                .lowStockThreshold(lowStockThreshold != null ? lowStockThreshold : 10)
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();

        return inventoryRepositoryPort.save(inventory);
    }

    @Override
    @Transactional
    public int updateStock(UUID productId, Integer quantityChange) {
        log.info("Updating stock for product {} by {}", productId, quantityChange);
        
        Inventory inventory = inventoryRepositoryPort.findByProductId(productId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found for product: " + productId));

        int newQuantity = inventory.getStockQuantity() + quantityChange;
        
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Insufficient stock for product " + productId + 
                    ". Current: " + inventory.getStockQuantity() + ", Requested change: " + quantityChange);
        }

        inventory.setStockQuantity(newQuantity);
        inventory.setUpdatedAt(ZonedDateTime.now());
        
        inventoryRepositoryPort.save(inventory);
        return newQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Inventory> getInventoryByProductId(UUID productId) {
        return inventoryRepositoryPort.findByProductId(productId);
    }

    @Override
    public boolean isStockAvailable(UUID productId, Integer requestedQuantity) {
        return inventoryRepositoryPort.findByProductId(productId)
                .map(inventory -> inventory.getStockQuantity() >= requestedQuantity)
                .orElse(false);
    }
}
