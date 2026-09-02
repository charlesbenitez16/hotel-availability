package com.mindata.hotelavailability.infrastructure.adapter.in.web.mapper;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.model.SearchCount;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchCountResponseDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchIdResponseDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchWebMapperTest {

    private final SearchWebMapper mapper = new SearchWebMapper();

    private SearchRequestDto requestDto;
    private RegisteredSearch record;

    @BeforeEach
    void setUp() {
        requestDto = new SearchRequestDto("1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3));
        HotelSearchQuery stay = new HotelSearchQuery(
                requestDto.hotelId(), requestDto.checkIn(), requestDto.checkOut(), requestDto.ages());
        record = new RegisteredSearch("search-id", stay, Instant.parse("2023-12-01T10:15:30Z"));
    }

    @Test
    void shouldMapRequestDtoToDomainStay() {
        HotelSearchQuery stay = mapper.toDomain(requestDto);

        assertThat(stay.hotelId()).isEqualTo("1234aBc");
        assertThat(stay.checkIn()).isEqualTo(LocalDate.of(2023, 12, 29));
        assertThat(stay.checkOut()).isEqualTo(LocalDate.of(2023, 12, 31));
        assertThat(stay.ages()).containsExactly(30, 29, 1, 3);
    }

    @Test
    void shouldMapRegisteredSearchToIdResponse() {
        SearchIdResponseDto response = mapper.toResponse(record);

        assertThat(response.searchId()).isEqualTo("search-id");
    }

    @Test
    void shouldMapSearchCountToCountResponse() {
        SearchCount count = new SearchCount(record.searchId(), record.stay(), 100L);

        SearchCountResponseDto response = mapper.toResponse(count);

        assertThat(response.searchId()).isEqualTo("search-id");
        assertThat(response.count()).isEqualTo(100L);
        assertThat(response.search().hotelId()).isEqualTo("1234aBc");
        assertThat(response.search().ages()).containsExactly(30, 29, 1, 3);
    }
}
