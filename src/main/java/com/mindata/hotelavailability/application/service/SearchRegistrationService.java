package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.port.in.RegisterSearchUseCase;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import com.mindata.hotelavailability.domain.port.out.SearchIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class SearchRegistrationService implements RegisterSearchUseCase {

    private static final Logger log = LoggerFactory.getLogger(SearchRegistrationService.class);

    private final SearchEventPublisher searchEventPublisher;
    private final SearchIdGenerator searchIdGenerator;
    private final Clock clock;

    public SearchRegistrationService(
            SearchEventPublisher searchEventPublisher,
            SearchIdGenerator searchIdGenerator,
            Clock clock) {
        this.searchEventPublisher = searchEventPublisher;
        this.searchIdGenerator = searchIdGenerator;
        this.clock = clock;
    }

    @Override
    public RegisteredSearch registerSearch(HotelSearchQuery stay) {
        String searchId = searchIdGenerator.generate();
        RegisteredSearch searchRecord = new RegisteredSearch(searchId, stay, Instant.now(clock));

        searchEventPublisher.publish(searchRecord);
        log.info("Search '{}' for hotel '{}' published to the event stream", searchId, stay.hotelId());

        return searchRecord;
    }
}
