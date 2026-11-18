package com.bazario.orders.adapters.in.web.dto;

import com.bazario.orders.domain.model.OrderStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class OrderDtos {

    public record CreateOrderRequest(
            @NotNull UUID customerId,
            @NotEmpty @Valid List<OrderItemRequest> items
    ) {}

    public record OrderItemRequest(
            @NotNull UUID productId,
            @NotNull @Positive Integer quantity
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
