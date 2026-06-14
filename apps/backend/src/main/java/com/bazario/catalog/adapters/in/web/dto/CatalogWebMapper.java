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

    CatalogDtos.ProductResponse toResponse(Product product);

    CatalogDtos.CategoryResponse toResponse(Category category);
}
