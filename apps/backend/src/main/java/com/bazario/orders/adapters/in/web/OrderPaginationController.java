package com.bazario.orders.adapters.in.web;

import com.bazario.orders.adapters.in.web.dto.OrderDtos;
import com.bazario.orders.adapters.in.web.dto.OrderWebMapper;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.ports.in.OrderUseCase;
import com.bazario.shared.web.PageRequests;
import com.bazario.shared.web.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

/**
 * Paginated order history, versioned as v2 so the v1 array contract stays intact.
 *
 * <p>{@code GET /api/v2/orders/customer/{customerId}?page=0&size=20&sort=createdAt,desc}
 * returns {@code {content[], page, number, size, totalElements, totalPages}}.
 */
@RestController
@RequestMapping("/api/v2/orders")
@RequiredArgsConstructor
public class OrderPaginationController {

    /** Order fields that may be used as the {@code sort} query parameter. */
    private static final Set<String> ORDER_SORTABLE_PROPERTIES =
            Set.of("id", "createdAt", "updatedAt", "orderNumber", "totalAmount", "status");

    private final OrderUseCase orderUseCase;
    private final OrderWebMapper webMapper;

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<PageResponse<OrderDtos.OrderResponse>> getOrdersByCustomerId(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = PageRequests.DEFAULT_SORT) String sort) {
        Pageable pageable = PageRequests.of(page, size, sort, ORDER_SORTABLE_PROPERTIES);
        Page<Order> orders = orderUseCase.getOrdersByCustomerId(customerId, pageable);
        return ResponseEntity.ok(PageResponse.of(orders.map(webMapper::toResponse)));
    }
}
