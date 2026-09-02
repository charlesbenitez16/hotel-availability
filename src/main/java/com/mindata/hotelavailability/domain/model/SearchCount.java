package com.mindata.hotelavailability.domain.model;

import java.util.Objects;

public record SearchCount(String searchId, HotelSearchQuery stay, long count) {

    public SearchCount {
        Objects.requireNonNull(searchId, "searchId must not be null");
        Objects.requireNonNull(stay, "stay must not be null");
        if (count < 0) {
            throw new IllegalArgumentException("count must not be negative");
        }
    }
}
