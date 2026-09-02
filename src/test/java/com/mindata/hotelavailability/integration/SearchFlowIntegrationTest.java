package com.mindata.hotelavailability.integration;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end: POST /search -> Kafka -> consumer persists on a virtual thread
 * -> GET /count sees it. Real embedded Kafka + H2, so it's actually async and
 * we poll with Awaitility.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "hotel_availability_searches")
class SearchFlowIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterASearchAndEventuallyReportItsCount() {
        Map<String, Object> payload = searchPayload("1234aBc");

        ResponseEntity<Map> searchResponse = restTemplate.postForEntity("/search", payload, Map.class);

        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String searchId = (String) searchResponse.getBody().get("searchId");
        assertThat(searchId).isNotBlank();

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    ResponseEntity<Map> countResponse =
                            restTemplate.getForEntity("/count?searchId={id}", Map.class, searchId);

                    assertThat(countResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(countResponse.getBody().get("count")).isEqualTo(1);
                });
    }

    @Test
    void identicalSearchesShouldAccumulateInTheCount() {
        Map<String, Object> payload = searchPayload("accumulationTestHotel");

        restTemplate.postForEntity("/search", payload, Map.class);
        ResponseEntity<Map> secondSearchResponse = restTemplate.postForEntity("/search", payload, Map.class);
        String secondSearchId = (String) secondSearchResponse.getBody().get("searchId");

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    ResponseEntity<Map> countResponse =
                            restTemplate.getForEntity("/count?searchId={id}", Map.class, secondSearchId);

                    assertThat(countResponse.getBody().get("count")).isEqualTo(2);
                });
    }

    @Test
    void shouldReturn404WhileTheSearchHasNotBeenConsumedYet() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "/count?searchId={id}", Map.class, "never-existed-search-id");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Map<String, Object> searchPayload(String hotelId) {
        return Map.of(
                "hotelId", hotelId,
                "checkIn", "29/12/2023",
                "checkOut", "31/12/2023",
                "ages", List.of(30, 29, 1, 3));
    }
}
