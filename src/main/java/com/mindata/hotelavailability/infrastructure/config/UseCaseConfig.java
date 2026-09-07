package com.mindata.hotelavailability.infrastructure.config;

import com.mindata.hotelavailability.application.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.application.port.in.PersistSearchUseCase;
import com.mindata.hotelavailability.application.port.in.RegisterSearchUseCase;
import com.mindata.hotelavailability.application.service.SearchCountService;
import com.mindata.hotelavailability.application.service.SearchPersistenceService;
import com.mindata.hotelavailability.application.service.SearchRegistrationService;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import com.mindata.hotelavailability.domain.port.out.SearchIdGenerator;
import com.mindata.hotelavailability.domain.port.out.SearchRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.concurrent.ExecutorService;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegisterSearchUseCase registerSearchUseCase(
            SearchEventPublisher searchEventPublisher, SearchIdGenerator searchIdGenerator, Clock clock) {
        return new SearchRegistrationService(searchEventPublisher, searchIdGenerator, clock);
    }

    @Bean
    public PersistSearchUseCase persistSearchUseCase(
            SearchRepository searchRepository, ExecutorService virtualThreadExecutor) {
        return new SearchPersistenceService(searchRepository, virtualThreadExecutor);
    }

    @Bean
    public CountSearchUseCase countSearchUseCase(SearchRepository searchRepository) {
        return new SearchCountService(searchRepository);
    }
}
