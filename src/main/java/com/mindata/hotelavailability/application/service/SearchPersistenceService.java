package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.exception.SearchPersistenceException;
import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.port.in.PersistSearchUseCase;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Service
public class SearchPersistenceService implements PersistSearchUseCase {

    private static final Logger events = LoggerFactory.getLogger("events");

    private final SearchRepository searchRepository;
    private final ExecutorService virtualThreadExecutor;

    public SearchPersistenceService(SearchRepository searchRepository, ExecutorService virtualThreadExecutor) {
        this.searchRepository = searchRepository;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    @Override
    public void persist(RegisteredSearch searchRecord) {
        try {
            virtualThreadExecutor.submit(() -> searchRepository.save(searchRecord)).get();
            HotelSearchQuery stay = searchRecord.stay();
            events.info("db saved -> searchId={} hotelId={} checkIn={} checkOut={} ages={}",
                    searchRecord.searchId(), stay.hotelId(), stay.checkIn(), stay.checkOut(), stay.ages());
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            throw new SearchPersistenceException(searchRecord.searchId(), interruptedException);
        } catch (ExecutionException executionException) {
            throw new SearchPersistenceException(searchRecord.searchId(), executionException.getCause());
        }
    }
}
