package com.bazario.shared.web;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Rules enforced by {@link PageRequests}: size ceiling, page ceiling, strict
 * sort syntax, strict sort direction, and the {@code id} tie-breaker.
 */
class PageRequestsTest {

    private static final Set<String> PROPERTIES = Set.of("id", "createdAt", "totalAmount");

    @Test
    void blankSortFallsBackToNewestFirstWithIdTieBreaker() {
        Pageable pageable = PageRequests.of(0, 20, null, PROPERTIES);

        assertThat(pageable.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void explicitDirectionIsHonouredAndIdIsAppended() {
        Pageable pageable = PageRequests.of(1, 10, "totalAmount,asc", PROPERTIES);

        assertThat(pageable.getPageNumber()).isEqualTo(1);
        assertThat(pageable.getPageSize()).isEqualTo(10);
        assertThat(pageable.getSort().getOrderFor("totalAmount").getDirection()).isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void sortingByIdDoesNotAppendADuplicateTieBreaker() {
        Pageable pageable = PageRequests.of(0, 20, "id,desc", PROPERTIES);

        assertThat(pageable.getSort().stream().count()).isEqualTo(1);
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void rejectsUnknownDirection() {
        assertThatThrownBy(() -> PageRequests.of(0, 20, "createdAt,garbage", PROPERTIES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported sort direction");
    }

    @Test
    void rejectsMoreThanTwoSortParts() {
        assertThatThrownBy(() -> PageRequests.of(0, 20, "createdAt,desc,id", PROPERTIES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be 'property'");
    }

    @Test
    void rejectsUnknownProperty() {
        assertThatThrownBy(() -> PageRequests.of(0, 20, "shippingAddressSnapshot", PROPERTIES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported sort property");
    }

    @Test
    void rejectsAPageWhoseOffsetPassesTheCeiling() {
        assertThatThrownBy(() -> PageRequests.of(Integer.MAX_VALUE, 100, "createdAt,desc", PROPERTIES))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too large");
    }

    @Test
    void clampsSizeAtTheCeiling() {
        assertThat(PageRequests.of(0, 500, "createdAt,desc", PROPERTIES).getPageSize())
                .isEqualTo(PageRequests.MAX_SIZE);
    }
}
