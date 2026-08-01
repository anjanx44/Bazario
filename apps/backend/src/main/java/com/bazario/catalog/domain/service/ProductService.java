package com.bazario.catalog.domain.service;

import com.bazario.catalog.application.model.AdminUpdateProductCommand;
import com.bazario.catalog.application.model.CategoryView;
import com.bazario.catalog.domain.model.Category;
import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.domain.model.ProductCreatedEvent;
import com.bazario.catalog.ports.in.ProductUseCase;
import com.bazario.catalog.ports.out.CategoryRepositoryPort;
import com.bazario.catalog.ports.out.ProductRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final CategoryRepositoryPort categoryRepositoryPort;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        return createProduct(product, null, null);
    }

    @Override
    @Transactional
    public Product createProduct(Product product, Integer initialStock, Integer lowStockThreshold) {
        if (productRepositoryPort.findBySku(product.getSku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU " + product.getSku() + " already exists");
        }

        product.setActive(true);
        product.setCreatedAt(ZonedDateTime.now());
        product.setUpdatedAt(ZonedDateTime.now());

        Product savedProduct = productRepositoryPort.save(product);

        eventPublisher.publishEvent(new ProductCreatedEvent(
                savedProduct.getId(), savedProduct.getSku(), initialStock, lowStockThreshold));

        return savedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductBySlug(String slug) {
        return productRepositoryPort.findBySlug(slug).filter(Product::isActive);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(UUID id) {
        return productRepositoryPort.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> listActiveProducts(
            Pageable pageable,
            String categorySlug,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return productRepositoryPort.findActiveProducts(pageable, categorySlug, search, minPrice, maxPrice);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> findAllProducts(
            Pageable pageable,
            String search,
            UUID categoryId,
            Boolean active,
            Boolean lowStock
    ) {
        return productRepositoryPort.findAllProducts(pageable, search, categoryId, active, lowStock);
    }

    @Override
    @Transactional
    public Optional<Product> updateProduct(UUID productId, AdminUpdateProductCommand command) {
        return productRepositoryPort.findById(productId).map(existing -> {
            if (command.name()          != null) existing.setName(command.name());
            if (command.slug()          != null) existing.setSlug(command.slug());
            if (command.description()   != null) existing.setDescription(command.description());
            if (command.basePrice()     != null) existing.setBasePrice(command.basePrice());
            if (command.discountPrice() != null) existing.setDiscountPrice(command.discountPrice());
            if (command.sku()           != null) existing.setSku(command.sku());
            if (command.active()        != null) existing.setActive(command.active());
            if (command.categoryId()    != null) {
                Category cat = new Category();
                cat.setId(command.categoryId());
                existing.setCategory(cat);
            }
            existing.setUpdatedAt(ZonedDateTime.now());
            return productRepositoryPort.save(existing);
        });
    }

    @Override
    @Transactional
    public void deactivateProduct(UUID productId) {
        productRepositoryPort.deactivate(productId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryView> listAllCategories() {
        return categoryRepositoryPort.findAll().stream()
                .map(cat -> new CategoryView(cat.getId(), cat.getName(), cat.getSlug()))
                .toList();
    }
}
