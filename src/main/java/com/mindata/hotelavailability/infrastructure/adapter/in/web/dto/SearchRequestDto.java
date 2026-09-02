package com.mindata.hotelavailability.infrastructure.adapter.in.web.dto;

import com.mindata.hotelavailability.infrastructure.adapter.in.web.validation.ValidDateRange;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;
import java.util.List;

@ValidDateRange
@Schema(name = "SearchRequest", description = "Hotel availability search request")
public record SearchRequestDto(

        @Schema(example = "1234aBc")
        @NotBlank(message = "hotelId must not be blank")
        String hotelId,

        @Schema(example = "29/12/2023", type = "string", pattern = "dd/MM/yyyy")
        @NotNull(message = "checkIn is required")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkIn,

        @Schema(example = "31/12/2023", type = "string", pattern = "dd/MM/yyyy")
        @NotNull(message = "checkOut is required")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate checkOut,

        @Schema(example = "[30, 29, 1, 3]")
        @NotEmpty(message = "ages must contain at least one value")
        List<@NotNull @PositiveOrZero(message = "each age must be zero or positive") Integer> ages
) {
}
