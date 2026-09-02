package com.mindata.hotelavailability.infrastructure.adapter.in.messaging;

import com.mindata.hotelavailability.domain.port.in.PersistSearchUseCase;
import com.mindata.hotelavailability.infrastructure.adapter.out.messaging.SearchEventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SearchEventConsumer {

    private static final Logger events = LoggerFactory.getLogger("events");

    private final PersistSearchUseCase persistSearchUseCase;

    public SearchEventConsumer(PersistSearchUseCase persistSearchUseCase) {
        this.persistSearchUseCase = persistSearchUseCase;
    }

    @KafkaListener(
            topics = "${app.kafka.topic.hotel-availability-searches}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void onSearchEvent(SearchEventMessage message) {
        events.info("kafka consume -> searchId={} hotelId={} checkIn={} checkOut={} ages={}",
                message.searchId(), message.hotelId(), message.checkIn(), message.checkOut(), message.ages());
        persistSearchUseCase.persist(message.toDomain());
    }
}
