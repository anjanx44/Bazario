package com.bazario.catalog.domain.service;

import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.domain.model.ProductCreatedEvent;
import com.bazario.catalog.ports.in.ProductUseCase;
import com.bazario.catalog.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (productRepositoryPort.findBySku(product.getSku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU " + product.getSku() + " already exists");
        }

        product.setActive(true);
        product.setCreatedAt(ZonedDateTime.now());
        product.setUpdatedAt(ZonedDateTime.now());

        Product savedProduct = productRepositoryPort.save(product);

        eventPublisher.publishEvent(new ProductCreatedEvent(savedProduct.getId(), savedProduct.getSku()));

        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductBySlug(String slug) {
        return productRepositoryPort.findBySlug(slug);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(java.util.UUID id) {
        return productRepositoryPort.findById(id);
    }
}
