package com.mindata.hotelavailability.infrastructure.adapter.out.persistence.mapper;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.SearchJpaEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchPersistenceMapper {

    private static final String SEPARATOR = ",";

    public SearchJpaEntity toEntity(RegisteredSearch searchRecord) {
        HotelSearchQuery stay = searchRecord.stay();
        return new SearchJpaEntity(
                searchRecord.searchId(),
                stay.hotelId(),
                stay.checkIn(),
                stay.checkOut(),
                toAgesKey(stay.ages()),
                searchRecord.registeredAt());
    }

    public RegisteredSearch toDomain(SearchJpaEntity entity) {
        HotelSearchQuery stay = new HotelSearchQuery(
                entity.getHotelId(),
                entity.getCheckIn(),
                entity.getCheckOut(),
                toAgesList(entity.getAgesKey()));
        return new RegisteredSearch(entity.getSearchId(), stay, entity.getRegisteredAt());
    }

    // Order matters for equality, so don't sort here.
    public String toAgesKey(List<Integer> ages) {
        return ages.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(SEPARATOR));
    }

    public List<Integer> toAgesList(String agesKey) {
        if (agesKey == null || agesKey.isBlank()) {
            return List.of();
        }
        return Arrays.stream(agesKey.split(SEPARATOR))
                .map(Integer::parseInt)
                .toList();
    }
}
