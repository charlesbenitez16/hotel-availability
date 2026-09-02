package com.mindata.hotelavailability.infrastructure.adapter.out.persistence.mapper;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.SearchJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchPersistenceMapperTest {

    private final SearchPersistenceMapper mapper = new SearchPersistenceMapper();

    @Test
    void shouldEncodeAgesPreservingOrder() {
        assertThat(mapper.toAgesKey(List.of(30, 29, 1, 3))).isEqualTo("30,29,1,3");
        assertThat(mapper.toAgesKey(List.of(3, 29, 30, 1))).isEqualTo("3,29,30,1");
    }

    @Test
    void shouldDecodeAgesKeyBackToAnOrderedList() {
        assertThat(mapper.toAgesList("30,29,1,3")).containsExactly(30, 29, 1, 3);
    }

    @Test
    void shouldDecodeBlankAgesKeyAsEmptyList() {
        assertThat(mapper.toAgesList("")).isEmpty();
        assertThat(mapper.toAgesList(null)).isEmpty();
    }

    @Test
    void shouldRoundTripDomainRecordThroughEntity() {
        HotelSearchQuery stay = new HotelSearchQuery(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
        Instant registeredAt = Instant.parse("2023-12-01T10:15:30Z");
        RegisteredSearch original = new RegisteredSearch("search-id", stay, registeredAt);

        SearchJpaEntity entity = mapper.toEntity(original);
        RegisteredSearch roundTripped = mapper.toDomain(entity);

        assertThat(roundTripped).isEqualTo(original);
        assertThat(entity.getAgesKey()).isEqualTo("30,29,1,3");
    }
}
