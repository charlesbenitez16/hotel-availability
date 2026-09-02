package com.mindata.hotelavailability.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisteredSearchTest {

    private final HotelSearchQuery stay = new HotelSearchQuery(
            "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));

    @Test
    void shouldExposeGivenValues() {
        Instant now = Instant.parse("2023-12-01T10:15:30Z");
        RegisteredSearch record = new RegisteredSearch("search-id", stay, now);

        assertThat(record.searchId()).isEqualTo("search-id");
        assertThat(record.stay()).isEqualTo(stay);
        assertThat(record.registeredAt()).isEqualTo(now);
    }

    @Test
    void shouldRejectNullSearchId() {
        assertThatThrownBy(() -> new RegisteredSearch(null, stay, Instant.now()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectBlankSearchId() {
        assertThatThrownBy(() -> new RegisteredSearch(" ", stay, Instant.now()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullStay() {
        assertThatThrownBy(() -> new RegisteredSearch("search-id", null, Instant.now()))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectNullRegisteredAt() {
        assertThatThrownBy(() -> new RegisteredSearch("search-id", stay, null))
                .isInstanceOf(NullPointerException.class);
    }
}
