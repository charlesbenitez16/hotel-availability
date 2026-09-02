package com.mindata.hotelavailability.infrastructure.adapter.out.id;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class UuidSearchIdGeneratorTest {

    private final UuidSearchIdGenerator generator = new UuidSearchIdGenerator();

    @Test
    void shouldGenerateAValidUuid() {
        String id = generator.generate();

        assertThat(id).isNotBlank();
        assertThatCode(() -> UUID.fromString(id)).doesNotThrowAnyException();
    }

    @Test
    void shouldGenerateDifferentIdsOnEachCall() {
        assertThat(generator.generate()).isNotEqualTo(generator.generate());
    }
}
