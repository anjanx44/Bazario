package com.bazario.catalog.adapters.out.persistence;

import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostgresProductAdapter implements ProductRepositoryPort {

    private final SpringDataProductRepository productRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public Product save(Product product) {
        ProductJpaEntity entity = mapper.toEntity(product);
        ProductJpaEntity saved = productRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Product> findBySlug(String slug) {
        return productRepository.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku).map(mapper::toDomain);
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deactivate(UUID id) {
        productRepository.deactivateById(id);
    }

    @Override
    public Page<Product> findActiveProducts(
            Pageable pageable,
            String categorySlug,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return productRepository
                .findActiveProducts(categorySlug, search, minPrice, maxPrice, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<Product> findAllProducts(
            Pageable pageable,
            String search,
            UUID categoryId,
            Boolean active,
            Boolean lowStock
    ) {
        return productRepository
                .findAllProductsAdmin(search, categoryId, active, lowStock, pageable)
                .map(mapper::toDomain);
    }
}
