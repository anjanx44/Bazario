package com.bazario.catalog.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataProductRepository extends JpaRepository<ProductJpaEntity, UUID> {

    Optional<ProductJpaEntity> findBySlug(String slug);
    Optional<ProductJpaEntity> findBySku(String sku);

    @Modifying
    @Query("UPDATE ProductJpaEntity p SET p.active = false WHERE p.id = :id")
    void deactivateById(@Param("id") UUID id);

    /**
     * Paginated storefront query — only active products.
     * All filter params are optional (pass null to skip).
     */
    @Query("""
            SELECT p FROM ProductJpaEntity p
            LEFT JOIN p.category c
            WHERE p.active = true
              AND (:categorySlug IS NULL OR c.slug = :categorySlug)
              AND (:search      IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:minPrice    IS NULL OR p.basePrice >= :minPrice)
              AND (:maxPrice    IS NULL OR p.basePrice <= :maxPrice)
            """)
    Page<ProductJpaEntity> findActiveProducts(
            @Param("categorySlug") String categorySlug,
            @Param("search")       String search,
            @Param("minPrice")     BigDecimal minPrice,
            @Param("maxPrice")     BigDecimal maxPrice,
            Pageable pageable
    );

    /**
     * Paginated admin query — all products (active + inactive).
     * lowStock filter: stockQuantity <= lowStockThreshold (joined via inventory).
     */
    @Query("""
            SELECT p FROM ProductJpaEntity p
            LEFT JOIN p.category c
            LEFT JOIN InventoryJpaEntity i ON i.productId = p.id
            WHERE (:search     IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                                      OR LOWER(p.sku)   LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:categoryId IS NULL OR c.id = :categoryId)
              AND (:active     IS NULL OR p.active = :active)
              AND (:lowStock   IS NULL OR :lowStock = false
                                      OR (i.stockQuantity IS NOT NULL AND i.stockQuantity <= i.lowStockThreshold))
            """)
    Page<ProductJpaEntity> findAllProductsAdmin(
            @Param("search")     String search,
            @Param("categoryId") UUID categoryId,
            @Param("active")     Boolean active,
            @Param("lowStock")   Boolean lowStock,
            Pageable pageable
    );
}
