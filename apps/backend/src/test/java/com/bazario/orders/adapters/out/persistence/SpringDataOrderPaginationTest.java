package com.bazario.orders.adapters.out.persistence;

import com.bazario.AbstractPostgresTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Persistence-level proof that the {@code id} tie-breaker keeps paging stable
 * when the primary sort key is identical for every row.
 *
 * <p>Rows are inserted with native SQL: it gives every row the same
 * {@code created_at} (Hibernate's {@code @CreationTimestamp} would make them all
 * distinct) and writes the jsonb column with an explicit cast. Without the
 * tie-breaker, Postgres may return a different order for each OFFSET and a row
 * can be repeated or skipped across pages.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpringDataOrderPaginationTest extends AbstractPostgresTest {

    @Autowired
    private SpringDataOrderRepository repository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("pages are stable and non-overlapping when createdAt is identical")
    void identicalTimestampsDoNotRepeatOrDropRows() {
        UUID customerId = UUID.randomUUID();

        for (int i = 0; i < 5; i++) {
            entityManager.createNativeQuery("""
                            INSERT INTO orders
                                (order_number, customer_id, total_amount, status,
                                 shipping_address_json, created_at, updated_at)
                            VALUES
                                (:orderNumber, :customerId, 10.0000, 'PENDING',
                                 CAST('{}' AS jsonb), TIMESTAMPTZ '2026-01-01 00:00:00+00',
                                 TIMESTAMPTZ '2026-01-01 00:00:00+00')
                            """)
                    .setParameter("orderNumber", "PAGING-" + customerId + "-" + i)
                    .setParameter("customerId", customerId)
                    .executeUpdate();
        }
        entityManager.clear();

        @SuppressWarnings("unchecked")
        List<UUID> expectedOrder = new ArrayList<>(entityManager.createNativeQuery(
                        "SELECT id FROM orders WHERE customer_id = :customerId ORDER BY id")
                .setParameter("customerId", customerId)
                .getResultList());

        Sort sort = Sort.by(Sort.Order.desc("createdAt"), Sort.Order.asc("id"));
        List<UUID> collected = new ArrayList<>();
        for (int page = 0; page < 3; page++) {
            Page<OrderJpaEntity> result = repository.findByCustomerId(
                    customerId, PageRequest.of(page, 2, sort));
            result.getContent().stream()
                    .map(OrderJpaEntity::getId)
                    .forEach(collected::add);
        }

        assertThat(collected)
                .hasSize(5)
                .doesNotHaveDuplicates()
                .containsExactlyInAnyOrderElementsOf(expectedOrder);

        // With equal timestamps the id tie-breaker decides the order, ascending.
        assertThat(collected).containsExactlyElementsOf(expectedOrder);
    }
}
