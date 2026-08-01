package com.bazario.orders.adapters.in.web.dto;

import com.bazario.orders.domain.model.OrderStatus;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDtos {

    public record CreateOrderRequest(
            UUID customerId,
            List<OrderItemRequest> items
    ) {}

    public record OrderItemRequest(
            UUID productId,
            Integer quantity
    ) {}

    public record OrderResponse(
            UUID id,
            String orderNumber,
            UUID customerId,
            BigDecimal totalAmount,
            OrderStatus status,
            String shippingAddressSnapshot,
            List<OrderItemResponse> items,
            ZonedDateTime createdAt,
            ZonedDateTime updatedAt
    ) {}

    public record OrderItemResponse(
            UUID id,
            UUID productId,
            String productName,
            BigDecimal price,
            Integer quantity
    ) {}
}
