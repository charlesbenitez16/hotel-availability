package com.mindata.hotelavailability.domain.model;

import java.time.Instant;
import java.util.Objects;

public record RegisteredSearch(String searchId, HotelSearchQuery stay, Instant registeredAt) {

    public RegisteredSearch {
        Objects.requireNonNull(searchId, "searchId must not be null");
        Objects.requireNonNull(stay, "stay must not be null");
        Objects.requireNonNull(registeredAt, "registeredAt must not be null");

        if (searchId.isBlank()) {
            throw new IllegalArgumentException("searchId must not be blank");
        }
    }
}
