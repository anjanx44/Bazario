package com.bazario.catalog.domain.model;

import java.util.UUID;

public record ProductCreatedEvent(UUID productId, String sku) {
}
