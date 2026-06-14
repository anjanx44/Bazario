package com.bazario.inventory.adapters.in.web.dto;

import com.bazario.inventory.domain.model.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InventoryWebMapper {
    InventoryDtos.InventoryResponse toResponse(Inventory inventory);
}
