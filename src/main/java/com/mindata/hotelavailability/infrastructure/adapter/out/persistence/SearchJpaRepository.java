package com.mindata.hotelavailability.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface SearchJpaRepository extends JpaRepository<SearchJpaEntity, String> {

    @Query("""
            select count(s) from SearchJpaEntity s
            where s.hotelId = :hotelId
              and s.checkIn = :checkIn
              and s.checkOut = :checkOut
              and s.agesKey = :agesKey
            """)
    long countByStay(
            @Param("hotelId") String hotelId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("agesKey") String agesKey);
}
