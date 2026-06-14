package com.bazario.catalog.domain.model;

import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private UUID id;
    private String name;
    private String slug;
    private String description;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
