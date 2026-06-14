package com.bazario.catalog.ports.out;

import com.bazario.catalog.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Optional<Category> findById(UUID id);
    List<Category> findAll();
}
