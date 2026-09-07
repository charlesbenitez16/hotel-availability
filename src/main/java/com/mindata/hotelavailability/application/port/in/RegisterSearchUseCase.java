package com.mindata.hotelavailability.application.port.in;

import com.mindata.hotelavailability.domain.model.HotelSearchQuery;
import com.mindata.hotelavailability.domain.model.RegisteredSearch;

public interface RegisterSearchUseCase {

    RegisteredSearch registerSearch(HotelSearchQuery stay);
}
