package com.bazario.inventory.adapters.in.events;

import com.bazario.catalog.domain.model.ProductCreatedEvent;
import com.bazario.inventory.ports.in.InventoryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogEventListener {

    private final InventoryUseCase inventoryUseCase;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("Received ProductCreatedEvent for product: {} (SKU: {})", event.productId(), event.sku());
        try {
            inventoryUseCase.initializeInventory(
                    event.productId(), event.initialStock(), event.lowStockThreshold());
            log.info("Successfully initialized inventory for product: {}", event.productId());
        } catch (Exception e) {
            log.error("Failed to initialize inventory for product: {}. Error: {}", event.productId(), e.getMessage());
            // Graceful handling as requested
        }
    }
}
