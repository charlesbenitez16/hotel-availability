package com.mindata.hotelavailability.infrastructure.adapter.in.web.exception;

import com.mindata.hotelavailability.domain.exception.InvalidDateRangeException;
import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldMapConstraintViolationExceptionTo400() {
        ResponseEntity<ApiError> response =
                handler.handleConstraintViolation(new ConstraintViolationException(Set.of()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().error()).isEqualTo("Validation failed");
    }

    @Test
    void shouldMapHttpMessageNotReadableExceptionTo400() {
        ResponseEntity<ApiError> response =
                handler.handleNotReadable(new HttpMessageNotReadableException("bad json"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().details()).isNotEmpty();
    }

    @Test
    void shouldMapMissingServletRequestParameterExceptionTo400() {
        ResponseEntity<ApiError> response =
                handler.handleMissingParameter(new MissingServletRequestParameterException("searchId", "String"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldMapInvalidDateRangeExceptionTo400() {
        InvalidDateRangeException exception =
                new InvalidDateRangeException(LocalDate.of(2023, 12, 31), LocalDate.of(2023, 12, 29));

        ResponseEntity<ApiError> response = handler.handleInvalidDateRange(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().error()).isEqualTo("Invalid date range");
        assertThat(response.getBody().details().get(0)).contains("must be strictly before");
    }

    @Test
    void shouldMapSearchNotFoundExceptionTo404() {
        ResponseEntity<ApiError> response = handler.handleSearchNotFound(new SearchNotFoundException("search-id"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().error()).isEqualTo("Search not found");
    }

    @Test
    void shouldMapUnexpectedExceptionTo500() {
        ResponseEntity<ApiError> response = handler.handleUnexpected(new RuntimeException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().error()).isEqualTo("Internal server error");
    }
}
