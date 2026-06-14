package com.bazario.orders.ports.in;

import com.bazario.orders.application.model.DashboardMetrics;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Inbound Port (Use Case interface) for the Orders bounded context.
 *
 * Architectural rule: this interface must NEVER import types from
 * *.adapters.* packages.  All parameter and return types must be either
 * domain models (com.bazario.orders.domain.*) or application-layer value
 * objects (com.bazario.orders.application.*).
 */
public interface OrderUseCase {

    Order createOrder(Order order);

    Optional<Order> getOrderById(UUID id);

    List<Order> getOrdersByCustomerId(UUID customerId);

    /**
     * Admin: paginated order list with optional filters.
     * Returns domain Order objects — the AdminController maps them to DTOs.
     */
    Page<Order> listOrders(
            Pageable pageable,
            OrderStatus status,
            String search,
            String fromDate,
            String toDate
    );

    /** Admin: update a single order's status. */
    Optional<Order> updateOrderStatus(UUID orderId, OrderStatus newStatus);

    /**
     * Admin: aggregated KPI metrics for the dashboard.
     * Returns an application-layer read model — not an Adapter DTO.
     */
    DashboardMetrics getDashboardMetrics();
}
