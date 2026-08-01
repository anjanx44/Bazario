package com.bazario.orders.ports.out;

import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepositoryPort {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findByCustomerId(UUID customerId);

    /** Admin: paginated order list with optional filters. */
    Page<Order> findAllOrders(
            Pageable pageable,
            OrderStatus status,
            String search,
            String fromDate,
            String toDate
    );

    /** Admin: count metrics. */
    long countAll();
    long countByStatus(OrderStatus status);
    double sumTotalRevenue();
}
