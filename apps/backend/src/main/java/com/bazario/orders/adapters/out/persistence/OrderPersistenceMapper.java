package com.bazario.orders.adapters.out.persistence;

import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OrderPersistenceMapper {

    Order toDomain(OrderJpaEntity entity);

    OrderItem toDomainItem(OrderItemJpaEntity entity);

    OrderJpaEntity toEntity(Order domain);

    @Mapping(target = "order", ignore = true)
    OrderItemJpaEntity toEntityItem(OrderItem domain);

    @AfterMapping
    default void linkItems(@MappingTarget OrderJpaEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setOrder(entity));
        }
    }
}
