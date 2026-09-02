package com.mindata.hotelavailability.domain.port.out;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;

import java.util.Optional;

public interface SearchRepository {

    void save(RegisteredSearch searchRecord);

    Optional<RegisteredSearch> findBySearchId(String searchId);

    long countByStay(HotelSearchQuery stay);
}
