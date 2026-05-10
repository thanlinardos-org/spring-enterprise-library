package com.thanlinardos.spring_enterprise_library.batch.properties;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CoreTest
class BatchRunTimerConfigPropertiesTest {

    @Test
    void recordAccessors_shouldReturnConfiguredValues() {
        BatchRunTimerConfigProperties properties = new BatchRunTimerConfigProperties(250L, 8L);

        assertEquals(250L, properties.frequencyMs());
        assertEquals(8L, properties.scheduleWindowSeconds());
    }
}

