package com.mindata.hotelavailability.domain.port.out;

import com.mindata.hotelavailability.domain.model.RegisteredSearch;

public interface SearchEventPublisher {

    void publish(RegisteredSearch searchRecord);
}
