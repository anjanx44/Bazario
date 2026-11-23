package com.bazario.shared.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

/**
 * Normalises the {@code page} / {@code size} / {@code sort} query parameters
 * shared by the paginated list endpoints.
 *
 * <ul>
 *   <li>{@code size} defaults to {@link #DEFAULT_SIZE} and is clamped to
 *       {@link #MAX_SIZE} - asking for more rows never widens the page.</li>
 *   <li>{@code page} is clamped to {@code 0}.</li>
 *   <li>{@code sort} is {@code property,asc|desc} and the property must be one of
 *       the sortable properties accepted by the endpoint, so a typo cannot reach
 *       Spring Data as an unresolvable property reference.</li>
 * </ul>
 */
public final class PageRequests {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    private PageRequests() {
    }

    public static Pageable of(int page, int size, String sort, Set<String> sortableProperties) {
        if (size < 1) {
            throw new IllegalArgumentException("Parameter 'size' must be greater than 0");
        }
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(size, MAX_SIZE);
        return PageRequest.of(safePage, safeSize, parseSort(sort, sortableProperties));
    }

    private static Sort parseSort(String sort, Set<String> sortableProperties) {
        if (sort == null || sort.isBlank()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        if (!sortableProperties.contains(property)) {
            throw new IllegalArgumentException(
                    "Unsupported sort property '" + property + "', allowed: " + sortableProperties);
        }
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }
}
