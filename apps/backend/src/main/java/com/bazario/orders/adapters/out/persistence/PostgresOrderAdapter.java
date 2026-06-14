package com.bazario.orders.adapters.out.persistence;

import com.bazario.orders.domain.model.Order;
import com.bazario.orders.domain.model.OrderStatus;
import com.bazario.orders.ports.out.OrderRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostgresOrderAdapter implements OrderRepositoryPort {

    private final SpringDataOrderRepository repository;
    private final OrderPersistenceMapper mapper;

    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = mapper.toEntity(order);
        OrderJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Order> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<Order> findAllOrders(
            Pageable pageable,
            OrderStatus status,
            String search,
            String fromDate,
            String toDate
    ) {
        // fromDate/toDate filtering can be added via Specification if needed
        return repository.findAllOrdersAdmin(status, search, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public long countAll() {
        return repository.count();
    }

    @Override
    public long countByStatus(OrderStatus status) {
        return repository.countByStatus(status);
    }

    @Override
    public double sumTotalRevenue() {
        return repository.sumTotalRevenue();
    }
}
