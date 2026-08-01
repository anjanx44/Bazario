package com.bazario.inventory.adapters.out.persistence;

import com.bazario.inventory.domain.model.Inventory;
import com.bazario.inventory.ports.out.InventoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostgresInventoryAdapter implements InventoryRepositoryPort {

    private final SpringDataInventoryRepository repository;
    private final InventoryPersistenceMapper mapper;

    @Override
    public Inventory save(Inventory inventory) {
        InventoryJpaEntity entity = mapper.toEntity(inventory);
        InventoryJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Inventory> findByProductId(UUID productId) {
        return repository.findByProductId(productId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Inventory> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}
