package com.mindata.hotelavailability.domain.port.in;

import com.mindata.hotelavailability.domain.model.RegisteredSearch;

public interface PersistSearchUseCase {

    void persist(RegisteredSearch searchRecord);
}
