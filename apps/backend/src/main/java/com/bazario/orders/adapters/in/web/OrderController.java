package com.bazario.orders.adapters.in.web;

import com.bazario.orders.adapters.in.web.dto.OrderDtos;
import com.bazario.orders.adapters.in.web.dto.OrderWebMapper;
import com.bazario.orders.domain.model.Order;
import com.bazario.orders.ports.in.OrderUseCase;
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
    public ResponseEntity<OrderDtos.OrderResponse> createOrder(@RequestBody OrderDtos.CreateOrderRequest request) {
        Order order = webMapper.toDomain(request);
        Order savedOrder = orderUseCase.createOrder(order);
        return ResponseEntity.ok(webMapper.toResponse(savedOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDtos.OrderResponse> getOrderById(@PathVariable UUID id) {
        return orderUseCase.getOrderById(id)
                .map(webMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDtos.OrderResponse>> getOrdersByCustomerId(@PathVariable UUID customerId) {
        List<Order> orders = orderUseCase.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(webMapper.toResponseList(orders));
    }
}
