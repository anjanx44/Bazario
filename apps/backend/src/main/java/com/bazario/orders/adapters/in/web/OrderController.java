package com.bazario.orders.adapters.in.web;

import com.bazario.orders.adapters.in.web.dto.OrderDtos;
import com.bazario.orders.adapters.in.web.dto.OrderWebMapper;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.ports.in.OrderUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderUseCase orderUseCase;
    private final OrderWebMapper webMapper;

    @PostMapping
    public ResponseEntity<OrderDtos.OrderResponse> createOrder(@Valid @RequestBody OrderDtos.CreateOrderRequest request) {
        Order order = webMapper.toDomain(request);
        UUID orderId = orderUseCase.createOrder(order);
        Order savedOrder = orderUseCase.getOrderById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order not found after create: " + orderId));
        return ResponseEntity.ok(webMapper.toResponse(savedOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDtos.OrderResponse> getOrderById(@PathVariable UUID id) {
        return orderUseCase.getOrderById(id)
                .map(webMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Legacy v1 order list for a single customer: a plain JSON array, no paging.
     *
     * <p>Kept unchanged on purpose - existing storefront and admin clients read
     * this shape. The paginated envelope lives on {@code GET /api/v2/orders/customer/{customerId}}.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDtos.OrderResponse>> getOrdersByCustomerId(@PathVariable UUID customerId) {
        List<Order> orders = orderUseCase.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(webMapper.toResponseList(orders));
    }
}
