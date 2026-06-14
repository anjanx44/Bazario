package com.bazario.catalog.ports.in;

import com.bazario.catalog.domain.model.Product;

import java.util.Optional;

public interface ProductUseCase {
    Product createProduct(Product product);
    Optional<Product> getProductBySlug(String slug);
    Optional<Product> getProductById(java.util.UUID id);
}
