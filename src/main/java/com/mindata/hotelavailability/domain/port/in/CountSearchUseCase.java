package com.mindata.hotelavailability.domain.port.in;

import com.mindata.hotelavailability.domain.model.SearchCount;

public interface CountSearchUseCase {

    SearchCount countBySearchId(String searchId);
}
