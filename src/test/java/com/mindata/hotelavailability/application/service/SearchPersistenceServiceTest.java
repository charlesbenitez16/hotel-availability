package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.exception.SearchPersistenceException;
import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SearchPersistenceServiceTest {

    @Mock
    private SearchRepository searchRepository;

    private ExecutorService virtualThreadExecutor;
    private SearchPersistenceService service;
    private RegisteredSearch record;

    @BeforeEach
    void setUp() {
        virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();
        service = new SearchPersistenceService(searchRepository, virtualThreadExecutor);

        HotelSearchQuery stay = new HotelSearchQuery(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
        record = new RegisteredSearch("search-id", stay, Instant.parse("2023-12-01T10:15:30Z"));
    }

    @AfterEach
    void tearDown() {
        virtualThreadExecutor.close();
    }

    @Test
    void shouldPersistTheRecordUsingAVirtualThread() {
        service.persist(record);

        verify(searchRepository).save(record);
    }

    @Test
    void shouldWrapRepositoryFailuresAsSearchPersistenceException() {
        doThrow(new IllegalStateException("db is down")).when(searchRepository).save(record);

        assertThatThrownBy(() -> service.persist(record))
                .isInstanceOf(SearchPersistenceException.class)
                .hasMessageContaining("search-id")
                .cause()
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("db is down");
    }
}
