package com.bazario.orders.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private UUID id;
    private String orderNumber;
    private UUID customerId;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String shippingAddressSnapshot;
    private List<OrderItem> items;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
