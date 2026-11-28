package com.bazario.inventory.adapters.out.persistence;

import com.bazario.AbstractPostgresTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpringDataInventoryRepositoryTest extends AbstractPostgresTest {

    @Autowired
    private SpringDataInventoryRepository repository;

    @Test
    @DisplayName("save then findByProductId round-trips the stock fields")
    void savesAndFindsByProductId() {
        UUID productId = UUID.randomUUID();

        InventoryJpaEntity saved = repository.save(InventoryJpaEntity.builder().productId(productId).stockQuantity(50).lowStockThreshold(10).build());

        Optional<InventoryJpaEntity> found = repository.findByProductId(productId);

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getStockQuantity()).isEqualTo(50);
    }


    @Test
    @DisplayName("findByProductId returns empty for unknown product")
    void returnsEmptyForUnknownProduct() {
        assertThat(repository.findByProductId(UUID.randomUUID())).isEmpty();
    }

    @Test
    @DisplayName("lowStockThreshold is persisted and readable")
    void persistsLowStockThreshold() {
        UUID productId = UUID.randomUUID();

        repository.save(InventoryJpaEntity.builder().productId(productId).stockQuantity(3).lowStockThreshold(5).build());

        assertThat(repository.findByProductId(productId)).get().extracting(InventoryJpaEntity::getLowStockThreshold).isEqualTo(5);
    }


    @Test
    @DisplayName("duplicate productId violates the unique constraint")
    void rejectsDuplicateProductId() {
        UUID productId = UUID.randomUUID();

        repository.saveAndFlush(InventoryJpaEntity.builder()
                .productId(productId)
                .stockQuantity(10)
                .build());

        assertThatThrownBy(() -> repository.saveAndFlush(InventoryJpaEntity.builder()
                .productId(productId)
                .stockQuantity(20)
                .build()))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}
