package com.mindata.hotelavailability.infrastructure.adapter.out.persistence;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.mapper.SearchPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Exercises the repository adapter against a real (in-memory H2) database,
 * through Spring Data derived queries only - proving there is no
 * hand-written / concatenated SQL and therefore no SQL-injection surface.
 */
@DataJpaTest
@ActiveProfiles("test")
class SearchRepositoryAdapterTest {

    @Autowired
    private SearchJpaRepository jpaRepository;

    private SearchRepositoryAdapter adapter;
    private HotelSearchQuery stay;

    @BeforeEach
    void setUp() {
        adapter = new SearchRepositoryAdapter(jpaRepository, new SearchPersistenceMapper());
        stay = new HotelSearchQuery(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
    }

    @Test
    void shouldSaveAndRetrieveASearchById() {
        RegisteredSearch record = new RegisteredSearch(UUID.randomUUID().toString(), stay, Instant.now());

        adapter.save(record);
        Optional<RegisteredSearch> found = adapter.findBySearchId(record.searchId());

        assertThat(found).contains(record);
    }

    @Test
    void shouldReturnEmptyForAnUnknownSearchId() {
        assertThat(adapter.findBySearchId("unknown")).isEmpty();
    }

    @Test
    void shouldCountOnlyExactStayMatchesIncludingAgeOrder() {
        RegisteredSearch sameStayFirst = new RegisteredSearch(UUID.randomUUID().toString(), stay, Instant.now());
        RegisteredSearch sameStaySecond = new RegisteredSearch(UUID.randomUUID().toString(), stay, Instant.now());

        HotelSearchQuery reorderedAges = new HotelSearchQuery(
                stay.hotelId(), stay.checkIn(), stay.checkOut(), List.of(3, 29, 30, 1));
        RegisteredSearch differentOrder = new RegisteredSearch(UUID.randomUUID().toString(), reorderedAges, Instant.now());

        HotelSearchQuery differentHotel = new HotelSearchQuery(
                "otherHotel", stay.checkIn(), stay.checkOut(), stay.ages());
        RegisteredSearch otherHotel = new RegisteredSearch(UUID.randomUUID().toString(), differentHotel, Instant.now());

        adapter.save(sameStayFirst);
        adapter.save(sameStaySecond);
        adapter.save(differentOrder);
        adapter.save(otherHotel);

        assertThat(adapter.countByStay(stay)).isEqualTo(2L);
        assertThat(adapter.countByStay(reorderedAges)).isEqualTo(1L);
    }

    @Test
    void shouldReturnZeroWhenNoSearchMatchesTheStay() {
        assertThat(adapter.countByStay(stay)).isZero();
    }
}
