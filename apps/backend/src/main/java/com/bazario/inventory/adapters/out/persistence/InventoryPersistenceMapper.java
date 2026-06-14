package com.bazario.inventory.adapters.out.persistence;

import com.bazario.inventory.domain.model.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryPersistenceMapper {
    InventoryJpaEntity toEntity(Inventory inventory);
    Inventory toDomain(InventoryJpaEntity entity);
}
