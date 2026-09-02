package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.model.SearchCount;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchCountServiceTest {

    @Mock
    private SearchRepository searchRepository;

    private SearchCountService service;
    private HotelSearchQuery stay;
    private RegisteredSearch record;

    @BeforeEach
    void setUp() {
        service = new SearchCountService(searchRepository);
        stay = new HotelSearchQuery(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
        record = new RegisteredSearch("search-id", stay, Instant.parse("2023-12-01T10:15:30Z"));
    }

    @Test
    void shouldReturnTheCountForAKnownSearchId() {
        when(searchRepository.findBySearchId("search-id")).thenReturn(Optional.of(record));
        when(searchRepository.countByStay(stay)).thenReturn(100L);

        SearchCount result = service.countBySearchId("search-id");

        assertThat(result.searchId()).isEqualTo("search-id");
        assertThat(result.stay()).isEqualTo(stay);
        assertThat(result.count()).isEqualTo(100L);
        verify(searchRepository).countByStay(stay);
    }

    @Test
    void shouldThrowWhenSearchIdIsUnknown() {
        when(searchRepository.findBySearchId("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.countBySearchId("unknown"))
                .isInstanceOf(SearchNotFoundException.class)
                .hasMessageContaining("unknown");
    }
}
