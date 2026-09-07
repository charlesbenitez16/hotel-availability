package com.mindata.hotelavailability.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class RegisteredSearchTest {

    private static final Instant NOW = Instant.parse("2023-12-01T10:15:30Z");

    private final HotelSearchQuery stay = new HotelSearchQuery(
            "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));

    @Test
    void shouldExposeGivenValues() {
        RegisteredSearch search = new RegisteredSearch("search-id", stay, NOW);

        assertAll(
                () -> assertThat(search.searchId()).isEqualTo("search-id"),
                () -> assertThat(search.stay()).isEqualTo(stay),
                () -> assertThat(search.registeredAt()).isEqualTo(NOW));
    }

    @Test
    void shouldRejectNullSearchId() {
        assertThatThrownBy(() -> new RegisteredSearch(null, stay, NOW))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectBlankSearchId() {
        assertThatThrownBy(() -> new RegisteredSearch(" ", stay, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullStay() {
        assertThatThrownBy(() -> new RegisteredSearch("search-id", null, NOW))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectNullRegisteredAt() {
        assertThatThrownBy(() -> new RegisteredSearch("search-id", stay, null))
                .isInstanceOf(NullPointerException.class);
    }
}
