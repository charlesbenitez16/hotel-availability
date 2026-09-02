package com.mindata.hotelavailability.infrastructure.adapter.in.web.validation;

import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, SearchRequestDto> {

    @Override
    public boolean isValid(SearchRequestDto dto, ConstraintValidatorContext context) {
        if (dto == null || dto.checkIn() == null || dto.checkOut() == null) {
            // Nullness is already reported by @NotNull on the individual fields.
            return true;
        }
        return dto.checkIn().isBefore(dto.checkOut());
    }
}
