package com.mindata.hotelavailability.domain.model;

import com.mindata.hotelavailability.domain.exception.InvalidDateRangeException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HotelSearchQueryTest {

    private static final LocalDate CHECK_IN = LocalDate.of(2023, 12, 29);
    private static final LocalDate CHECK_OUT = LocalDate.of(2023, 12, 31);

    @Test
    void shouldExposeTheGivenValues() {
        HotelSearchQuery stay = new HotelSearchQuery("1234aBc", CHECK_IN, CHECK_OUT, List.of(30, 29, 1, 3));

        assertThat(stay.hotelId()).isEqualTo("1234aBc");
        assertThat(stay.checkIn()).isEqualTo(CHECK_IN);
        assertThat(stay.checkOut()).isEqualTo(CHECK_OUT);
        assertThat(stay.ages()).containsExactly(30, 29, 1, 3);
    }

    @Test
    void shouldBeImmutableAgainstExternalListMutation() {
        List<Integer> mutableAges = new ArrayList<>(List.of(30, 29, 1, 3));
        HotelSearchQuery stay = new HotelSearchQuery("1234aBc", CHECK_IN, CHECK_OUT, mutableAges);

        mutableAges.add(99);

        assertThat(stay.ages()).containsExactly(30, 29, 1, 3);
    }

    @Test
    void shouldRejectMutationOfTheReturnedAgesList() {
        HotelSearchQuery stay = new HotelSearchQuery("1234aBc", CHECK_IN, CHECK_OUT, List.of(30, 29));

        assertThatThrownBy(() -> stay.ages().add(1))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldConsiderAgeOrderSignificant() {
        HotelSearchQuery original = new HotelSearchQuery("1234aBc", CHECK_IN, CHECK_OUT, List.of(30, 29, 1, 3));
        HotelSearchQuery reordered = new HotelSearchQuery("1234aBc", CHECK_IN, CHECK_OUT, List.of(3, 29, 30, 1));

        assertThat(original).isNotEqualTo(reordered);
    }

    @Test
    void shouldRejectNullHotelId() {
        assertThatThrownBy(() -> new HotelSearchQuery(null, CHECK_IN, CHECK_OUT, List.of(30)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectBlankHotelId() {
        assertThatThrownBy(() -> new HotelSearchQuery("   ", CHECK_IN, CHECK_OUT, List.of(30)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectNullCheckIn() {
        assertThatThrownBy(() -> new HotelSearchQuery("1234aBc", null, CHECK_OUT, List.of(30)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectNullCheckOut() {
        assertThatThrownBy(() -> new HotelSearchQuery("1234aBc", CHECK_IN, null, List.of(30)))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectNullAges() {
        assertThatThrownBy(() -> new HotelSearchQuery("1234aBc", CHECK_IN, CHECK_OUT, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldRejectCheckInEqualToCheckOut() {
        assertThatThrownBy(() -> new HotelSearchQuery("1234aBc", CHECK_IN, CHECK_IN, List.of(30)))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("must be strictly before");
    }

    @Test
    void shouldRejectCheckInAfterCheckOut() {
        assertThatThrownBy(() -> new HotelSearchQuery("1234aBc", CHECK_OUT, CHECK_IN, List.of(30)))
                .isInstanceOf(InvalidDateRangeException.class);
    }
}
