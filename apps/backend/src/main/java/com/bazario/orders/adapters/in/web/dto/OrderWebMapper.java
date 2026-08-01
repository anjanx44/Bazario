package com.bazario.orders.adapters.in.web.dto;

import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderWebMapper {

    OrderDtos.OrderResponse toResponse(Order order);

    List<OrderDtos.OrderResponse> toResponseList(List<Order> orders);

    OrderDtos.OrderItemResponse toItemResponse(OrderItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "totalAmount", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "shippingAddressSnapshot", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Order toDomain(OrderDtos.CreateOrderRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productName", ignore = true)
    @Mapping(target = "price", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    OrderItem toDomainItem(OrderDtos.OrderItemRequest request);
}
