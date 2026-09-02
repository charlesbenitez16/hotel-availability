package com.mindata.hotelavailability.infrastructure.adapter.out.messaging;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaSearchEventPublisherTest {

    private static final String TOPIC = "hotel_availability_searches";

    @Mock
    private KafkaTemplate<String, SearchEventMessage> kafkaTemplate;

    private KafkaSearchEventPublisher publisher;
    private RegisteredSearch record;

    @BeforeEach
    void setUp() {
        publisher = new KafkaSearchEventPublisher(kafkaTemplate, TOPIC);
        HotelSearchQuery stay = new HotelSearchQuery(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
        record = new RegisteredSearch("search-id", stay, Instant.parse("2023-12-01T10:15:30Z"));
    }

    @Test
    void shouldSendTheMessageToTheConfiguredTopicUsingSearchIdAsKey() {
        @SuppressWarnings("unchecked")
        SendResult<String, SearchEventMessage> sendResult = mock(SendResult.class);
        RecordMetadata metadata = mock(RecordMetadata.class);
        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(kafkaTemplate.send(eq(TOPIC), eq("search-id"), any()))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        publisher.publish(record);

        ArgumentCaptor<SearchEventMessage> captor = ArgumentCaptor.forClass(SearchEventMessage.class);
        verify(kafkaTemplate).send(eq(TOPIC), eq("search-id"), captor.capture());
        assertThat(captor.getValue().searchId()).isEqualTo("search-id");
        assertThat(captor.getValue().hotelId()).isEqualTo("1234aBc");
    }

    @Test
    void shouldNotThrowWhenTheSendFutureCompletesExceptionally() {
        CompletableFuture<SendResult<String, SearchEventMessage>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("broker unavailable"));
        when(kafkaTemplate.send(eq(TOPIC), eq("search-id"), any())).thenReturn(failedFuture);

        publisher.publish(record);

        verify(kafkaTemplate).send(eq(TOPIC), eq("search-id"), any());
    }
}
