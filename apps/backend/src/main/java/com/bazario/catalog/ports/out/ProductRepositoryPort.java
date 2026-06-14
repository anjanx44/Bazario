package com.bazario.catalog.ports.out;

import com.bazario.catalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findBySlug(String slug);
    Optional<Product> findBySku(String sku);
    Optional<Product> findById(UUID id);
    void deactivate(UUID id);

    /**
     * Paginated search of active products with optional filters.
     * Null parameters are ignored (treated as "no filter").
     */
    Page<Product> findActiveProducts(
            Pageable pageable,
            String categorySlug,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    /**
     * Paginated search of ALL products (active + inactive) for admin use.
     * Null parameters are ignored.
     */
    Page<Product> findAllProducts(
            Pageable pageable,
            String search,
            UUID categoryId,
            Boolean active,
            Boolean lowStock
    );
}
