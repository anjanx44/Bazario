package com.bazario.orders.adapters.out.persistence;

import com.bazario.orders.domain.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataOrderRepository extends JpaRepository<OrderJpaEntity, UUID> {

    List<OrderJpaEntity> findByCustomerId(UUID customerId);

    long countByStatus(OrderStatus status);

    @Query("""
            SELECT o FROM OrderJpaEntity o
            WHERE (:status IS NULL OR o.status = :status)
              AND (:search IS NULL
                   OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<OrderJpaEntity> findAllOrdersAdmin(
            @Param("status") OrderStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM OrderJpaEntity o WHERE o.status = 'PAID'")
    double sumTotalRevenue();
}
