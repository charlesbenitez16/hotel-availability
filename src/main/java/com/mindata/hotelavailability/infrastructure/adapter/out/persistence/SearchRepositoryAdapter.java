package com.mindata.hotelavailability.infrastructure.adapter.out.persistence;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import com.mindata.hotelavailability.infrastructure.adapter.out.persistence.mapper.SearchPersistenceMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SearchRepositoryAdapter implements SearchRepository {

    private final SearchJpaRepository jpaRepository;
    private final SearchPersistenceMapper mapper;

    public SearchRepositoryAdapter(SearchJpaRepository jpaRepository, SearchPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(RegisteredSearch searchRecord) {
        jpaRepository.save(mapper.toEntity(searchRecord));
    }

    @Override
    public Optional<RegisteredSearch> findBySearchId(String searchId) {
        return jpaRepository.findById(searchId).map(mapper::toDomain);
    }

    @Override
    public long countByStay(HotelSearchQuery stay) {
        String agesKey = mapper.toAgesKey(stay.ages());
        return jpaRepository.countByStay(
                stay.hotelId(), stay.checkIn(), stay.checkOut(), agesKey);
    }
}
