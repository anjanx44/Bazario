package com.bazario.catalog.application.model;

import java.util.UUID;

/**
 * Application-layer read model for a Category.
 *
 * Lives in the application layer (ports/use-cases) so it can be referenced
 * by both the inbound Port (ProductUseCase) and the domain Service
 * (ProductService) without either layer depending on an Adapter DTO.
 *
 * Architectural rule: this type must NEVER import anything from
 * *.adapters.* packages.
 */
public record CategoryView(
        UUID   id,
        String name,
        String slug
) {}
