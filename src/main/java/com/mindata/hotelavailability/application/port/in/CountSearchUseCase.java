package com.mindata.hotelavailability.application.port.in;

import com.mindata.hotelavailability.domain.model.SearchCount;

public interface CountSearchUseCase {

    SearchCount countBySearchId(String searchId);
}
