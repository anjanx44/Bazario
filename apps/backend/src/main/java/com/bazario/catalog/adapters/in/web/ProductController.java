package com.bazario.catalog.adapters.in.web;

import com.bazario.catalog.adapters.in.web.dto.CatalogDtos;
import com.bazario.catalog.adapters.in.web.dto.CatalogWebMapper;
import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.ports.in.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;
    private final CatalogWebMapper webMapper;

    @PostMapping
    public ResponseEntity<CatalogDtos.ProductResponse> createProduct(@RequestBody CatalogDtos.CreateProductRequest request) {
        Product product = webMapper.toDomain(request);
        Product created = productUseCase.createProduct(product);
        return ResponseEntity.ok(webMapper.toResponse(created));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CatalogDtos.ProductResponse> getProduct(@PathVariable String slug) {
        return productUseCase.getProductBySlug(slug)
                .map(product -> ResponseEntity.ok(webMapper.toResponse(product)))
                .orElse(ResponseEntity.notFound().build());
    }
}
