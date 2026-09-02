package com.mindata.hotelavailability.infrastructure.adapter.in.web.exception;

import com.mindata.hotelavailability.domain.exception.InvalidDateRangeException;
import com.mindata.hotelavailability.domain.exception.SearchNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final Logger events = LoggerFactory.getLogger("events");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        List<String> details = exception.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();
        List<String> globalDetails = exception.getBindingResult().getGlobalErrors().stream()
                .map(objectError -> objectError.getDefaultMessage())
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed",
                concat(details, globalDetails));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> details = exception.getConstraintViolations().stream()
                .map(violation -> "%s: %s".formatted(violation.getPropertyPath(), violation.getMessage()))
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiError> handleHandlerMethodValidation(HandlerMethodValidationException exception) {
        List<String> details = exception.getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation failed", details);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Malformed request body",
                List.of("The request body could not be parsed. Check field types and the dd/MM/yyyy date format."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Missing request parameter", List.of(exception.getMessage()));
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ApiError> handleInvalidDateRange(InvalidDateRangeException exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid date range", List.of(exception.getMessage()));
    }

    @ExceptionHandler(SearchNotFoundException.class)
    public ResponseEntity<ApiError> handleSearchNotFound(SearchNotFoundException exception) {
        return buildResponse(HttpStatus.NOT_FOUND, "Search not found", List.of(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception exception) {
        log.error("Unexpected error handling request", exception);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error",
                List.of("An unexpected error occurred."));
    }

    private ResponseEntity<ApiError> buildResponse(HttpStatus status, String error, List<String> details) {
        events.warn("request failed -> {} {} {}", status.value(), error, details);
        ApiError body = new ApiError(Instant.now(), status.value(), error, details);
        return ResponseEntity.status(status).body(body);
    }

    private List<String> concat(List<String> first, List<String> second) {
        return java.util.stream.Stream.concat(first.stream(), second.stream()).toList();
    }
}
