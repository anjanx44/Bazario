package com.bazario.catalog.ports.in;

import com.bazario.catalog.application.model.AdminUpdateProductCommand;
import com.bazario.catalog.application.model.CategoryView;
import com.bazario.catalog.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Inbound Port (Use Case interface) for the Catalog bounded context.
 *
 * Architectural rule: this interface must NEVER import types from
 * *.adapters.* packages.  All parameter and return types must be either
 * domain models (com.bazario.catalog.domain.*) or application-layer value
 * objects (com.bazario.catalog.application.*).
 */
public interface ProductUseCase {

    Product createProduct(Product product);

    Optional<Product> getProductBySlug(String slug);

    Optional<Product> getProductById(UUID id);

    /** Storefront: paginated active products with optional filters. */
    Page<Product> listActiveProducts(
            Pageable pageable,
            String categorySlug,
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice
    );

    /** Admin: paginated ALL products (active + inactive) with optional filters. */
    Page<Product> findAllProducts(
            Pageable pageable,
            String search,
            UUID categoryId,
            Boolean active,
            Boolean lowStock
    );

    /**
     * Admin: partial update of a product.
     * Accepts an application-layer command — not an Adapter DTO.
     */
    Optional<Product> updateProduct(UUID productId, AdminUpdateProductCommand command);

    /** Admin: soft-delete (set active = false). */
    void deactivateProduct(UUID productId);

    /**
     * Admin: all categories for dropdown.
     * Returns application-layer read models — not Adapter DTOs.
     */
    List<CategoryView> listAllCategories();
}
