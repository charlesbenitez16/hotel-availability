package com.mindata.hotelavailability.infrastructure.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "searches", indexes = {
        @Index(name = "idx_searches_stay", columnList = "hotel_id, check_in, check_out, ages_key")
})
public class SearchJpaEntity implements Persistable<String> {

    @Id
    @Column(name = "search_id", nullable = false, updatable = false, length = 36)
    private String searchId;

    @Column(name = "hotel_id", nullable = false, updatable = false, length = 64)
    private String hotelId;

    @Column(name = "check_in", nullable = false, updatable = false)
    private LocalDate checkIn;

    @Column(name = "check_out", nullable = false, updatable = false)
    private LocalDate checkOut;

    @Column(name = "ages_key", nullable = false, updatable = false, length = 255)
    private String agesKey;

    @Column(name = "registered_at", nullable = false, updatable = false)
    private Instant registeredAt;

    @Transient
    private boolean isNew;

    protected SearchJpaEntity() {
        // required by JPA; entities hydrated from the database are never "new"
        this.isNew = false;
    }

    public SearchJpaEntity(String searchId, String hotelId, LocalDate checkIn, LocalDate checkOut,
                            String agesKey, Instant registeredAt) {
        this.searchId = searchId;
        this.hotelId = hotelId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.agesKey = agesKey;
        this.registeredAt = registeredAt;
        this.isNew = true;
    }

    @Override
    public String getId() {
        return searchId;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public String getSearchId() {
        return searchId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public String getAgesKey() {
        return agesKey;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }
}
