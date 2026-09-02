package com.mindata.hotelavailability.infrastructure.adapter.in.messaging;

import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.port.in.PersistSearchUseCase;
import com.mindata.hotelavailability.infrastructure.adapter.out.messaging.SearchEventMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchEventConsumerTest {

    @Mock
    private PersistSearchUseCase persistSearchUseCase;

    @Test
    void shouldDelegatePersistenceOfTheConsumedMessage() {
        SearchEventConsumer consumer = new SearchEventConsumer(persistSearchUseCase);
        SearchEventMessage message = new SearchEventMessage(
                "search-id", "1234aBc",
                LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31),
                List.of(30, 29, 1, 3), Instant.parse("2023-12-01T10:15:30Z"));

        consumer.onSearchEvent(message);

        ArgumentCaptor<RegisteredSearch> captor = ArgumentCaptor.forClass(RegisteredSearch.class);
        verify(persistSearchUseCase).persist(captor.capture());
        assertThat(captor.getValue().searchId()).isEqualTo("search-id");
        assertThat(captor.getValue().stay().hotelId()).isEqualTo("1234aBc");
        assertThat(captor.getValue().stay().ages()).containsExactly(30, 29, 1, 3);
    }
}
