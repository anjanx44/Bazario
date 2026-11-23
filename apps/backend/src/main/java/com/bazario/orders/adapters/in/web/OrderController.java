package com.bazario.orders.adapters.in.web;

import com.bazario.orders.adapters.in.web.dto.OrderDtos;
import com.bazario.orders.adapters.in.web.dto.OrderWebMapper;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.ports.in.OrderUseCase;
import com.bazario.shared.web.PageRequests;
import com.bazario.shared.web.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    /** Order fields that may be used as the {@code sort} query parameter. */
    private static final Set<String> ORDER_SORTABLE_PROPERTIES =
            Set.of("createdAt", "updatedAt", "orderNumber", "totalAmount", "status");

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
     * Paginated order list for a single customer.
     *
     * Query parameters: {@code page} (default 0), {@code size} (default 20, max 100),
     * {@code sort} (default {@code createdAt,desc}).
     * Response: {@code {content[], page, size, totalElements, totalPages}}.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PageResponse<OrderDtos.OrderResponse>> getOrdersByCustomerId(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        Pageable pageable = PageRequests.of(page, size, sort, ORDER_SORTABLE_PROPERTIES);
        Page<Order> orders = orderUseCase.getOrdersByCustomerId(customerId, pageable);
        return ResponseEntity.ok(PageResponse.of(orders.map(webMapper::toResponse)));
    }
}
