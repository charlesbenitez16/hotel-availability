package com.mindata.hotelavailability.infrastructure.adapter.out.id;

import com.mindata.hotelavailability.domain.port.out.SearchIdGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidSearchIdGenerator implements SearchIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
