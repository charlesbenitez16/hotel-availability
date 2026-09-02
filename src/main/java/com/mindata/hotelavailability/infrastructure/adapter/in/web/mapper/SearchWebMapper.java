package com.mindata.hotelavailability.infrastructure.adapter.in.web.mapper;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;
import com.mindata.hotelavailability.domain.model.SearchCount;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchCountResponseDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchIdResponseDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchRequestDto;
import com.mindata.hotelavailability.infrastructure.adapter.in.web.dto.SearchStayDto;
import org.springframework.stereotype.Component;

@Component
public class SearchWebMapper {

    public HotelSearchQuery toDomain(SearchRequestDto dto) {
        return new HotelSearchQuery(dto.hotelId(), dto.checkIn(), dto.checkOut(), dto.ages());
    }

    public SearchIdResponseDto toResponse(RegisteredSearch searchRecord) {
        return new SearchIdResponseDto(searchRecord.searchId());
    }

    public SearchCountResponseDto toResponse(SearchCount searchCount) {
        SearchStayDto stayDto = toStayDto(searchCount.stay());
        return new SearchCountResponseDto(searchCount.searchId(), stayDto, searchCount.count());
    }

    private SearchStayDto toStayDto(HotelSearchQuery stay) {
        return new SearchStayDto(stay.hotelId(), stay.checkIn(), stay.checkOut(), stay.ages());
    }
}
