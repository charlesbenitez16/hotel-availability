package com.mindata.hotelavailability.infrastructure.adapter.in.web;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.model.SearchCount;
import com.mindata.hotelavailability.domain.port.in.CountSearchUseCase;
import com.mindata.hotelavailability.domain.port.in.RegisterSearchUseCase;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchCountResponseDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchIdResponseDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchRequestDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.mapper.SearchWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Hotel Availability Search", description = "Register and query hotel availability searches")
public class SearchController {

    private static final Logger events = LoggerFactory.getLogger("events");

    private final RegisterSearchUseCase registerSearchUseCase;
    private final CountSearchUseCase countSearchUseCase;
    private final SearchWebMapper mapper;

    public SearchController(
            RegisterSearchUseCase registerSearchUseCase,
            CountSearchUseCase countSearchUseCase,
            SearchWebMapper mapper) {
        this.registerSearchUseCase = registerSearchUseCase;
        this.countSearchUseCase = countSearchUseCase;
        this.mapper = mapper;
    }

    @Operation(summary = "Register a new hotel availability search",
            description = "Validates the payload, converts it into an immutable domain object and "
                    + "publishes it to the hotel_availability_searches Kafka topic.")
    @ApiResponse(responseCode = "201", description = "Search registered",
            content = @Content(schema = @Schema(implementation = SearchIdResponseDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content)
    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchIdResponseDto> search(@Valid @RequestBody SearchRequestDto requestDto) {
        HotelSearchQuery stay = mapper.toDomain(requestDto);
        RegisteredSearch searchRecord = registerSearchUseCase.registerSearch(stay);
        events.info("POST /search -> 201 searchId={} hotelId={} checkIn={} checkOut={} ages={}",
                searchRecord.searchId(), stay.hotelId(), stay.checkIn(), stay.checkOut(), stay.ages());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(searchRecord));
    }

    @Operation(summary = "Count identical searches",
            description = "Returns how many persisted searches share the exact same stay "
                    + "(hotel, dates and ages, in the same order) as the search identified by searchId. "
                    + "Because persistence happens asynchronously via Kafka, a very recently created "
                    + "searchId may briefly return 404 until the consumer has processed it.")
    @ApiResponse(responseCode = "200", description = "Count resolved",
            content = @Content(schema = @Schema(implementation = SearchCountResponseDto.class)))
    @ApiResponse(responseCode = "404", description = "Unknown searchId", content = @Content)
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SearchCountResponseDto> count(
            @Parameter(description = "Identifier returned by POST /search", required = true)
            @RequestParam("searchId") @NotBlank String searchId) {
        SearchCount searchCount = countSearchUseCase.countBySearchId(searchId);
        events.info("GET /count -> 200 searchId={} count={}", searchId, searchCount.count());
        return ResponseEntity.ok(mapper.toResponse(searchCount));
    }
}
