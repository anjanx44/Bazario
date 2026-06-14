package com.bazario.catalog.ports.out;

import com.bazario.catalog.domain.model.Product;

import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findBySlug(String slug);
    Optional<Product> findBySku(String sku);
    Optional<Product> findById(java.util.UUID id);
}
