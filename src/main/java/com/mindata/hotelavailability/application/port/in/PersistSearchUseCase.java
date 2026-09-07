package com.mindata.hotelavailability.application.port.in;

import com.mindata.hotelavailability.domain.model.RegisteredSearch;

public interface PersistSearchUseCase {

    void persist(RegisteredSearch searchRecord);
}
