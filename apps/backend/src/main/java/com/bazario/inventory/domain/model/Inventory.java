package com.bazario.inventory.domain.model;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    private UUID id;
    private UUID productId;
    private Integer stockQuantity;
    private Integer lowStockThreshold;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
