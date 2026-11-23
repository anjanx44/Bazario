package com.bazario.inventory.domain.service;

import com.bazario.AbstractPostgresTest;
import com.bazario.inventory.adapters.out.persistence.InventoryJpaEntity;
import com.bazario.inventory.adapters.out.persistence.InventoryPersistenceMapperImpl;
import com.bazario.inventory.adapters.out.persistence.PostgresInventoryAdapter;
import com.bazario.inventory.adapters.out.persistence.SpringDataInventoryRepository;
import com.bazario.inventory.domain.model.Inventory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Service-level tests for InventoryService.
 * <p>
 * Why @DataJpaTest instead of a plain unit test with a mocked port:
 * the interesting part is that stock changes actually reach PostgreSQL
 * through the real adapter + MapStruct mapper, so we import those two beans.
 * <p>
 * Beans imported explicitly because @DataJpaTest only scans repositories,
 * not @Service / @Component classes.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({InventoryService.class, PostgresInventoryAdapter.class, InventoryPersistenceMapperImpl.class})
class InventoryServiceTest extends AbstractPostgresTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private SpringDataInventoryRepository repository;

    @Test
    @DisplayName("initializeInventory stores the initial stock and explicit threshold")
    void initializeInventoryStoresStockAndThreshold() {
        UUID productId = UUID.randomUUID();

        Inventory created = inventoryService.initializeInventory(productId, 40, 15);

        assertThat(created.getStockQuantity()).isEqualTo(40);

        // Also verify it really hit the database, not just the returned object.
        assertThat(repository.findByProductId(productId)).isPresent();
        assertThat(repository.findByProductId(productId)).get()
                .extracting(InventoryJpaEntity::getLowStockThreshold)
                .isEqualTo(15);
    }

    @Test
    @DisplayName("initializeInventory falls back to threshold 10 when null is passed")
    void initializeInventoryDefaultsThresholdToTen() {
        UUID productId = UUID.randomUUID();

        inventoryService.initializeInventory(productId, 5, null);

        assertThat(repository.findByProductId(productId)).get()
                .extracting(InventoryJpaEntity::getLowStockThreshold)
                .isEqualTo(10);
    }

    @Test
    @DisplayName("initializeInventory rejects a second initialization for the same product")
    void initializeInventoryRejectsDuplicateProduct() {
        UUID productId = UUID.randomUUID();
        inventoryService.initializeInventory(productId, 10, 5);

        assertThatThrownBy(() -> inventoryService.initializeInventory(productId, 20, 5))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    @DisplayName("updateStock adds and subtracts, and the new value is persisted")
    void updateStockAppliesPositiveAndNegativeChanges() {
        UUID productId = UUID.randomUUID();
        inventoryService.initializeInventory(productId, 10, 5);

        int afterAdd = inventoryService.updateStock(productId, 5);
        assertThat(afterAdd).isEqualTo(15);

        int afterSubtract = inventoryService.updateStock(productId, -7);
        assertThat(afterSubtract).isEqualTo(8);

        // The value must be visible on a fresh read, not only in the return value.
        assertThat(repository.findByProductId(productId)).get()
                .extracting(InventoryJpaEntity::getStockQuantity)
                .isEqualTo(8);
    }

    @Test
    @DisplayName("updateStock rejects a change that would drive stock below zero")
    void updateStockRejectsGoingBelowZero() {
        UUID productId = UUID.randomUUID();
        inventoryService.initializeInventory(productId, 4, 5);

        assertThatThrownBy(() -> inventoryService.updateStock(productId, -10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");

        // Nothing changed: the failed update must not corrupt the stored value.
        assertThat(repository.findByProductId(productId)).get()
                .extracting(InventoryJpaEntity::getStockQuantity)
                .isEqualTo(4);
    }

    @Test
    @DisplayName("updateStock rejects an unknown product")
    void updateStockRejectsUnknownProduct() {
        UUID productId = UUID.randomUUID();

        assertThatThrownBy(() -> inventoryService.updateStock(productId, 5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Inventory not found");
    }
}
