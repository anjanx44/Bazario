package com.bazario.catalog.adapters.out.persistence;

import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    public Optional<Product> findById(java.util.UUID id) {
        return productRepository.findById(id).map(mapper::toDomain);
    }
}
