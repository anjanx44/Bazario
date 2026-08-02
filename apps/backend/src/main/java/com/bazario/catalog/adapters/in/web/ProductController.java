package com.bazario.catalog.adapters.in.web;

import com.bazario.catalog.adapters.in.web.dto.CatalogDtos;
import com.bazario.catalog.adapters.in.web.dto.CatalogWebMapper;
import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.ports.in.ProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Storefront Catalog", description = "Public product catalog endpoints for the Bazario storefront")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;
    private final CatalogWebMapper webMapper;

    /**
     * GET /api/v1/products
     * Returns a paginated list of active products.
     * Supports filtering by categorySlug, search (name/description), minPrice, maxPrice.
     *
     * Query params:
     *   page          (int,    default 0)
     *   size          (int,    default 12)
     *   sort          (string, default "createdAt,desc")
     *   categorySlug  (string, optional)
     *   search        (string, optional — matches name or description)
     *   minPrice      (decimal, optional)
     *   maxPrice      (decimal, optional)
     *
     * Response: PagedProductResponse { content[], totalElements, totalPages, size, number }
     */
    @Operation(summary = "List active products (paginated)", description = "Returns paginated active products with optional filtering")
    @GetMapping
    public ResponseEntity<CatalogDtos.PagedProductResponse> listProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) String categorySlug,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        String[] sortParts = sort.split(",");
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));

        Page<Product> productPage = productUseCase.listActiveProducts(
                pageable, categorySlug, search, minPrice, maxPrice);

        List<CatalogDtos.ProductResponse> content = productPage.getContent()
                .stream()
                .map(webMapper::toResponse)
                .toList();

        CatalogDtos.PagedProductResponse response = new CatalogDtos.PagedProductResponse(
                content,
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.getSize(),
                productPage.getNumber()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/products/{slug}
     * Returns a single active product by its URL slug.
     * Returns 404 if not found or inactive.
     */
    @Operation(summary = "Get product by slug")
    @GetMapping("/{slug}")
    public ResponseEntity<CatalogDtos.ProductResponse> getProduct(@PathVariable String slug) {
        return productUseCase.getProductBySlug(slug)
                .map(product -> ResponseEntity.ok(webMapper.toResponse(product)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/products
     * Creates a new product. Restricted to ROLE_ADMIN in SecurityConfig.
     */
    @Operation(summary = "Create a new product (admin only)")
    @PostMapping
    public ResponseEntity<CatalogDtos.ProductResponse> createProduct(
            @Valid @RequestBody CatalogDtos.CreateProductRequest request) {
        Product product = webMapper.toDomain(request);
        UUID productId = productUseCase.createProduct(product);
        Product created = productUseCase.getProductById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found after create: " + productId));
        return ResponseEntity.ok(webMapper.toResponse(created));
    }
}
