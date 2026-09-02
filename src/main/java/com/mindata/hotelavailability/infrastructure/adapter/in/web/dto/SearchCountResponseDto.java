package com.mindata.hotelavailability.infrastructure.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SearchCountResponse", description = "Number of identical searches for a given searchId")
public record SearchCountResponseDto(

        @Schema(example = "3f29b6b0-df7b-4e3a-9c9a-2f7e9a6a9a11")
        String searchId,

        SearchStayDto search,

        @Schema(example = "100")
        long count
) {
}
