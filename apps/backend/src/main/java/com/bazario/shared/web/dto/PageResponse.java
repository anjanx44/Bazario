package com.bazario.shared.web.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Envelope returned by every paginated list endpoint of the public API, so
 * clients always read the same shape:
 * {@code {content, page, size, totalElements, totalPages}}.
 *
 * <p>The catalog and admin endpoints predate this record and still expose the
 * Spring Data field name {@code number} instead of {@code page}; they are
 * intentionally left untouched (changing them would break existing clients).
 *
 * @param <T> type of the elements in {@code content}
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    /** Builds the envelope from an already-mapped Spring Data page. */
    public static <T> PageResponse<T> of(Page<T> pagedResult) {
        return new PageResponse<>(
                pagedResult.getContent(),
                pagedResult.getNumber(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages()
        );
    }
}
