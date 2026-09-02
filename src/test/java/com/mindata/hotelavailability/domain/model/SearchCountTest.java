package com.mindata.hotelavailability.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SearchCountTest {

    private final HotelSearchQuery stay = new HotelSearchQuery(
            "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));

    @Test
    void shouldExposeGivenValues() {
        SearchCount count = new SearchCount("search-id", stay, 100L);

        assertThat(count.searchId()).isEqualTo("search-id");
        assertThat(count.stay()).isEqualTo(stay);
        assertThat(count.count()).isEqualTo(100L);
    }

    @Test
    void shouldAllowZeroCount() {
        SearchCount count = new SearchCount("search-id", stay, 0L);
        assertThat(count.count()).isZero();
    }

    @Test
    void shouldRejectNegativeCount() {
        assertThatThrownBy(() -> new SearchCount("search-id", stay, -1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullSearchId() {
        assertThatThrownBy(() -> new SearchCount(null, stay, 1L))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectNullStay() {
        assertThatThrownBy(() -> new SearchCount("search-id", null, 1L))
                .isInstanceOf(NullPointerException.class);
    }
}
