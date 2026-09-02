package com.mindata.hotelavailability.infrastructure.adapter.in.web;

import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.model.SearchCount;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.in.RegisterSearchUseCase;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.exception.GlobalExceptionHandler;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.mapper.SearchWebMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchController.class)
@Import({SearchWebMapper.class, GlobalExceptionHandler.class})
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RegisterSearchUseCase registerSearchUseCase;

    @MockitoBean
    private CountSearchUseCase countSearchUseCase;

    private HotelSearchQuery stay;
    private RegisteredSearch record;

    @BeforeEach
    void setUp() {
        stay = new HotelSearchQuery(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30, 29, 1, 3));
        record = new RegisteredSearch("search-id", stay, Instant.parse("2023-12-01T10:15:30Z"));
    }

    @Test
    void searchShouldReturn201WithSearchId() throws Exception {
        when(registerSearchUseCase.registerSearch(any())).thenReturn(record);

        mockMvc.perform(post("/search")
                        .contentType("application/json")
                        .content(validRequestJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.searchId").value("search-id"));

        verify(registerSearchUseCase).registerSearch(stay);
    }

    @Test
    void searchShouldReturn400WhenHotelIdIsBlank() throws Exception {
        Map<String, Object> payload = Map.of(
                "hotelId", "",
                "checkIn", "29/12/2023",
                "checkOut", "31/12/2023",
                "ages", List.of(30, 29, 1, 3));

        mockMvc.perform(post("/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchShouldReturn400WhenCheckInIsAfterCheckOut() throws Exception {
        Map<String, Object> payload = Map.of(
                "hotelId", "1234aBc",
                "checkIn", "31/12/2023",
                "checkOut", "29/12/2023",
                "ages", List.of(30));

        mockMvc.perform(post("/search")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchShouldReturn400WhenDateFormatIsInvalid() throws Exception {
        String malformedJson = """
                {"hotelId":"1234aBc","checkIn":"2023-12-29","checkOut":"31/12/2023","ages":[30]}
                """;

        mockMvc.perform(post("/search")
                        .contentType("application/json")
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void countShouldReturn200WithSearchAndCount() throws Exception {
        SearchCount searchCount = new SearchCount("search-id", stay, 100L);
        when(countSearchUseCase.countBySearchId("search-id")).thenReturn(searchCount);

        mockMvc.perform(get("/count").param("searchId", "search-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchId").value("search-id"))
                .andExpect(jsonPath("$.count").value(100))
                .andExpect(jsonPath("$.search.hotelId").value("1234aBc"))
                .andExpect(jsonPath("$.search.ages[0]").value(30))
                .andExpect(jsonPath("$.search.ages[1]").value(29));

        verify(countSearchUseCase).countBySearchId(eq("search-id"));
    }

    @Test
    void countShouldReturn404ForUnknownSearchId() throws Exception {
        when(countSearchUseCase.countBySearchId("unknown")).thenThrow(new SearchNotFoundException("unknown"));

        mockMvc.perform(get("/count").param("searchId", "unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void countShouldReturn400WhenSearchIdParameterIsMissing() throws Exception {
        mockMvc.perform(get("/count")).andExpect(status().isBadRequest());
    }

    @Test
    void countShouldReturn400WhenSearchIdParameterIsBlank() throws Exception {
        mockMvc.perform(get("/count").param("searchId", "   "))
                .andExpect(status().isBadRequest());
    }

    @Test
    void statusCodesShouldMatchExpectationsAcrossBothEndpoints() throws Exception {
        when(registerSearchUseCase.registerSearch(any())).thenReturn(record);
        when(countSearchUseCase.countBySearchId("search-id")).thenReturn(new SearchCount("search-id", stay, 5L));

        var searchStatus = mockMvc.perform(post("/search")
                        .contentType("application/json")
                        .content(validRequestJson()))
                .andReturn().getResponse().getStatus();
        var countStatus = mockMvc.perform(get("/count").param("searchId", "search-id"))
                .andReturn().getResponse().getStatus();

        assertAll(
                (Executable) () -> org.junit.jupiter.api.Assertions.assertEquals(201, searchStatus),
                (Executable) () -> org.junit.jupiter.api.Assertions.assertEquals(200, countStatus)
        );
    }

    private String validRequestJson() throws Exception {
        Map<String, Object> payload = Map.of(
                "hotelId", "1234aBc",
                "checkIn", "29/12/2023",
                "checkOut", "31/12/2023",
                "ages", List.of(30, 29, 1, 3));
        return objectMapper.writeValueAsString(payload);
    }
}
