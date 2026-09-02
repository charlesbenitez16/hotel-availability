package com.mindata.hotelavailability.application.service;

import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.model.SearchCount;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import org.springframework.stereotype.Service;

@Service
public class SearchCountService implements CountSearchUseCase {

    private final SearchRepository searchRepository;

    public SearchCountService(SearchRepository searchRepository) {
        this.searchRepository = searchRepository;
    }

    @Override
    public SearchCount countBySearchId(String searchId) {
        RegisteredSearch searchRecord = searchRepository.findBySearchId(searchId)
                .orElseThrow(() -> new SearchNotFoundException(searchId));

        long count = searchRepository.countByStay(searchRecord.stay());

        return new SearchCount(searchRecord.searchId(), searchRecord.stay(), count);
    }
}
