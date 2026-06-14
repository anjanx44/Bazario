package com.bazario.catalog.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private UUID id;
    private Category category;
    private String name;
    private String slug;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private String sku;
    private boolean active;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
