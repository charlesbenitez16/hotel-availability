package com.mindata.hotelavailability.infrastructure.adapter.out.messaging;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SearchEventMessageTest {

    private final HotelSearchQuery stay = new HotelSearchQuery(
            "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
    private final Instant registeredAt = Instant.parse("2023-12-01T10:15:30Z");
    private final RegisteredSearch search = new RegisteredSearch("search-id", stay, registeredAt);

    @Test
    void shouldBuildFromARegisteredSearch() {
        SearchEventMessage message = SearchEventMessage.from(search);

        assertAll(
                () -> assertThat(message.searchId()).isEqualTo("search-id"),
                () -> assertThat(message.hotelId()).isEqualTo("1234aBc"),
                () -> assertThat(message.checkIn()).isEqualTo(LocalDate.of(2023, 12, 29)),
                () -> assertThat(message.checkOut()).isEqualTo(LocalDate.of(2023, 12, 31)),
                () -> assertThat(message.ages()).containsExactly(30, 29, 1, 3),
                () -> assertThat(message.registeredAt()).isEqualTo(registeredAt));
    }

    @Test
    void shouldRoundTripBackToTheSameDomainRecord() {
        SearchEventMessage message = SearchEventMessage.from(search);

        RegisteredSearch roundTripped = message.toDomain();

        assertThat(roundTripped).isEqualTo(search);
    }
}
