package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import com.mindata.hotelavailability.domain.port.out.SearchIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchRegistrationServiceTest {

    private static final Instant FIXED_INSTANT = Instant.parse("2023-12-01T10:15:30Z");

    @Mock
    private SearchEventPublisher searchEventPublisher;

    @Mock
    private SearchIdGenerator searchIdGenerator;

    private SearchRegistrationService service;
    private HotelSearchQuery stay;

    @BeforeEach
    void setUp() {
        Clock fixedClock = Clock.fixed(FIXED_INSTANT, ZoneOffset.UTC);
        service = new SearchRegistrationService(searchEventPublisher, searchIdGenerator, fixedClock);
        stay = new HotelSearchQuery(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
    }

    @Test
    void shouldAssignAnIdAndPublishTheSearch() {
        when(searchIdGenerator.generate()).thenReturn("generated-id");

        RegisteredSearch result = service.registerSearch(stay);

        assertThat(result.searchId()).isEqualTo("generated-id");
        assertThat(result.stay()).isEqualTo(stay);
        assertThat(result.registeredAt()).isEqualTo(FIXED_INSTANT);

        ArgumentCaptor<RegisteredSearch> captor = ArgumentCaptor.forClass(RegisteredSearch.class);
        verify(searchEventPublisher).publish(captor.capture());
        assertThat(captor.getValue()).isEqualTo(result);
    }

    @Test
    void shouldNeverCallTheRepositoryOrAnyDatabaseWhenGeneratingTheId() {
        when(searchIdGenerator.generate()).thenReturn("generated-id");

        service.registerSearch(stay);

        verify(searchIdGenerator).generate();
        verify(searchEventPublisher).publish(any());
        // No repository collaborator exists on this service at all: the id
        // is generated purely in-memory, satisfying "no debe acceder a la
        // base de datos" by construction.
    }
}
