package com.mindata.hotelavailability.infrastructure.adapter.out.messaging;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SearchEventMessage(
        String searchId,
        String hotelId,
        LocalDate checkIn,
        LocalDate checkOut,
        List<Integer> ages,
        Instant registeredAt
) {

    @JsonCreator
    public SearchEventMessage(
            @JsonProperty("searchId") String searchId,
            @JsonProperty("hotelId") String hotelId,
            @JsonProperty("checkIn") LocalDate checkIn,
            @JsonProperty("checkOut") LocalDate checkOut,
            @JsonProperty("ages") List<Integer> ages,
            @JsonProperty("registeredAt") Instant registeredAt) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.ages = ages;
        this.registeredAt = registeredAt;
    }

    public static SearchEventMessage from(RegisteredSearch searchRecord) {
        HotelSearchQuery stay = searchRecord.stay();
        return new SearchEventMessage(
                searchRecord.searchId(),
                stay.hotelId(),
                stay.checkIn(),
                stay.checkOut(),
                stay.ages(),
                searchRecord.registeredAt());
    }

    public RegisteredSearch toDomain() {
        HotelSearchQuery stay = new HotelSearchQuery(hotelId, checkIn, checkOut, ages);
        return new RegisteredSearch(searchId, stay, registeredAt);
    }
}
