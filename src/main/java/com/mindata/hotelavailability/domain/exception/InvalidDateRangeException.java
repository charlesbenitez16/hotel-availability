package com.mindata.hotelavailability.domain.exception;

import java.time.LocalDate;

public class InvalidDateRangeException extends RuntimeException {

    public InvalidDateRangeException(LocalDate checkIn, LocalDate checkOut) {
        super("checkIn (%s) must be strictly before checkOut (%s)".formatted(checkIn, checkOut));
    }
}
