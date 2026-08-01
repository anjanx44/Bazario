package com.bazario.catalog.adapters.out.persistence;

import com.bazario.catalog.domain.model.Category;
import com.bazario.catalog.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductPersistenceMapper {

    @Mapping(target = "category", source = "category")
    Product toDomain(ProductJpaEntity entity);

    @Mapping(target = "category", source = "category")
    ProductJpaEntity toEntity(Product domain);

    Category toDomain(CategoryJpaEntity entity);

    CategoryJpaEntity toEntity(Category domain);
}
