package com.mindata.hotelavailability.infrastructure.adapter.in.web.validation;

import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchRequestDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DateRangeValidatorTest {

    private final DateRangeValidator validator = new DateRangeValidator();

    @Test
    void shouldAcceptCheckInBeforeCheckOut() {
        SearchRequestDto dto = new SearchRequestDto(
                "1234aBc", LocalDate.of(2023, 12, 29), LocalDate.of(2023, 12, 31), List.of(30));

        assertThat(validator.isValid(dto, null)).isTrue();
    }

    @Test
    void shouldRejectCheckInEqualToCheckOut() {
        LocalDate sameDay = LocalDate.of(2023, 12, 29);
        SearchRequestDto dto = new SearchRequestDto("1234aBc", sameDay, sameDay, List.of(30));

        assertThat(validator.isValid(dto, null)).isFalse();
    }

    @Test
    void shouldRejectCheckInAfterCheckOut() {
        SearchRequestDto dto = new SearchRequestDto(
                "1234aBc", LocalDate.of(2023, 12, 31), LocalDate.of(2023, 12, 29), List.of(30));

        assertThat(validator.isValid(dto, null)).isFalse();
    }

    @Test
    void shouldDelegateNullDatesToNotNullConstraint() {
        SearchRequestDto dto = new SearchRequestDto("1234aBc", null, null, List.of(30));

        assertThat(validator.isValid(dto, null)).isTrue();
    }
}
