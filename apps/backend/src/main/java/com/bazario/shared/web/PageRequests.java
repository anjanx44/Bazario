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
 *   <li>{@code page} is clamped to {@code 0} and rejected when the resulting
 *       offset passes {@link #MAX_OFFSET}, so a client cannot ask the database
 *       for a multi-million row skip.</li>
 *   <li>{@code sort} is {@code property} or {@code property,asc|desc}; anything
 *       else is rejected with {@link IllegalArgumentException} so the caller
 *       gets a 400 instead of a silently flipped sort.</li>
 *   <li>every sort gets {@code id} appended as a tie-breaker: rows that share
 *       the requested property (typically {@code createdAt}) keep a stable
 *       order, so paging cannot repeat or drop a row.</li>
 * </ul>
 */
public final class PageRequests {

    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    /** Largest offset ({@code page * size}) a client may ask for. */
    public static final long MAX_OFFSET = 10_000L;

    /** Immutable unique property appended to every sort. */
    private static final String TIE_BREAKER = "id";

    /** Sort used when the client sends no {@code sort} parameter. */
    public static final String DEFAULT_SORT = "createdAt,desc";

    private PageRequests() {
    }

    public static Pageable of(int page, int size, String sort, Set<String> sortableProperties) {
        if (size < 1) {
            throw new IllegalArgumentException("Parameter 'size' must be greater than 0");
        }
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(size, MAX_SIZE);
        long offset = (long) safePage * safeSize;
        if (offset > MAX_OFFSET) {
            throw new IllegalArgumentException(
                    "Parameter 'page' is too large: offset " + offset + " exceeds " + MAX_OFFSET);
        }
        return PageRequest.of(safePage, safeSize, parseSort(sort, sortableProperties));
    }

    private static Sort parseSort(String sort, Set<String> sortableProperties) {
        if (sort == null || sort.isBlank()) {
            sort = DEFAULT_SORT;
        }
        String[] parts = sort.split(",", -1);
        if (parts.length > 2) {
            throw new IllegalArgumentException(
                    "Parameter 'sort' must be 'property' or 'property,asc|desc', got '" + sort + "'");
        }
        String property = parts[0].trim();
        if (property.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'sort' must name a property");
        }
        if (!sortableProperties.contains(property)) {
            throw new IllegalArgumentException(
                    "Unsupported sort property '" + property + "', allowed: " + sortableProperties);
        }
        Sort.Direction direction = Sort.Direction.DESC;
        if (parts.length == 2) {
            String raw = parts[1].trim();
            if ("asc".equalsIgnoreCase(raw)) {
                direction = Sort.Direction.ASC;
            } else if (!"desc".equalsIgnoreCase(raw)) {
                throw new IllegalArgumentException(
                        "Unsupported sort direction '" + raw + "', allowed: asc, desc");
            }
        }
        if (TIE_BREAKER.equals(property)) {
            return Sort.by(direction, property);
        }
        return Sort.by(new Sort.Order(direction, property), Sort.Order.asc(TIE_BREAKER));
    }
}
