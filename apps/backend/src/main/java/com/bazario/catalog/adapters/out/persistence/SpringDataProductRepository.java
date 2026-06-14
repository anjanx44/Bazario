package com.bazario.catalog.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {
    Optional<ProductJpaEntity> findBySlug(String slug);
    Optional<ProductJpaEntity> findBySku(String sku);
}
