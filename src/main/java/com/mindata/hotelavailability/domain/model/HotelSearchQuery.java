package com.mindata.hotelavailability.domain.model;

import com.mindata.hotelavailability.domain.exception.InvalidDateRangeException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public record HotelSearchQuery(String hotelId, LocalDate checkIn, LocalDate checkOut, List<Integer> ages) {

    public HotelSearchQuery {
        Objects.requireNonNull(hotelId, "hotelId must not be null");
        Objects.requireNonNull(checkIn, "checkIn must not be null");
        Objects.requireNonNull(checkOut, "checkOut must not be null");
        Objects.requireNonNull(ages, "ages must not be null");

        if (hotelId.isBlank()) {
            throw new IllegalArgumentException("hotelId must not be blank");
        }
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidDateRangeException(checkIn, checkOut);
        }

        ages = List.copyOf(ages); // defensive, unmodifiable copy
    }
}
