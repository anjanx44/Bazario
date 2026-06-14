package com.bazario.catalog.adapters.out.persistence;

import com.bazario.catalog.domain.model.Category;
import com.bazario.catalog.ports.out.CategoryRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostgresCategoryAdapter implements CategoryRepositoryPort {

    private final SpringDataCategoryRepository categoryRepository;
    private final ProductPersistenceMapper mapper;

    @Override
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id).map(mapper::toDomain);
    }
}
