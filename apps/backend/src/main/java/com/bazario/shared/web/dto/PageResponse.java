package com.bazario.shared.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope returned by every paginated list endpoint of the public API, so
 * clients always read the same shape:
 * {@code {content, page, number, size, totalElements, totalPages}}.
 *
 * <p>The catalog and admin endpoints predate this record and expose the
 * Spring Data field name {@code number}; this envelope carries both {@code page}
 * and {@code number} (same value) so a client built against either naming keeps
 * working without a second migration.
 *
 * @param <T> type of the elements in {@code content}
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int number,
        int size,
        long totalElements,
        int totalPages
) {

    /** Builds the envelope from an already-mapped Spring Data page. */
    public static <T> PageResponse<T> of(Page<T> pagedResult) {
        return new PageResponse<>(
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getNumber(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages()
        );
    }
}
