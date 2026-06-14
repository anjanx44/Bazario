package com.bazario.catalog.adapters.in.web.dto;

import com.bazario.catalog.domain.model.Product;
import com.bazario.catalog.domain.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CatalogWebMapper {

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toDomain(CatalogDtos.CreateProductRequest request);

    @Mapping(target = "category.id", source = "categoryId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Product toDomainFromAdminCreate(CatalogDtos.AdminCreateProductRequest request);

    CatalogDtos.ProductResponse toResponse(Product product);

    CatalogDtos.CategoryResponse toResponse(Category category);

    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "stockQuantity", ignore = true)
    @Mapping(target = "lowStockThreshold", ignore = true)
    CatalogDtos.AdminProductResponse toAdminResponse(Product product);
}
