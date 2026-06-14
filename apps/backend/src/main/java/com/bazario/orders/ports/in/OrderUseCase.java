package com.bazario.orders.ports.in;

import com.bazario.orders.domain.model.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderUseCase {
    Order createOrder(Order order);
    Optional<Order> getOrderById(UUID id);
    List<Order> getOrdersByCustomerId(UUID customerId);
}
