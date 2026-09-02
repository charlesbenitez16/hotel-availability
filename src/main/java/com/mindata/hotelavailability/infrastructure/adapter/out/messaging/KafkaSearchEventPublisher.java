package com.mindata.hotelavailability.infrastructure.adapter.out.messaging;

import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.port.out.SearchEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class KafkaSearchEventPublisher implements SearchEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaSearchEventPublisher.class);
    private static final Logger events = LoggerFactory.getLogger("events");

    private final KafkaTemplate<String, SearchEventMessage> kafkaTemplate;
    private final String topic;

    public KafkaSearchEventPublisher(
            KafkaTemplate<String, SearchEventMessage> kafkaTemplate,
            @Value("${app.kafka.topic.hotel-availability-searches}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(RegisteredSearch searchRecord) {
        SearchEventMessage message = SearchEventMessage.from(searchRecord);
        events.info("kafka publish -> topic={} searchId={} hotelId={}", topic, message.searchId(), message.hotelId());

        kafkaTemplate.send(topic, searchRecord.searchId(), message).whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Failed to publish search '{}' to topic '{}'", searchRecord.searchId(), topic, throwable);
            } else {
                log.debug("Search '{}' published to partition {}", searchRecord.searchId(),
                        result.getRecordMetadata().partition());
            }
        });
    }
}
