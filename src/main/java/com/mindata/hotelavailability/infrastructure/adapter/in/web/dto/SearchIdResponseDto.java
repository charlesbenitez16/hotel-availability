package com.mindata.hotelavailability.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SearchIdResponse", description = "Identifier assigned to a newly registered search")
public record SearchIdResponseDto(

        @Schema(example = "3f29b6b0-df7b-4e3a-9c9a-2f7e9a6a9a11")
        String searchId
) {
}
