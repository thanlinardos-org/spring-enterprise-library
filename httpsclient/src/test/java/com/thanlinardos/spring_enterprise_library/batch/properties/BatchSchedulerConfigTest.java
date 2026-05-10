package com.thanlinardos.spring_enterprise_library.batch.properties;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.batch.properties.api.BatchSyncProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@CoreTest
class BatchSchedulerConfigTest {

    @Test
    void constructor_shouldMapAllPropertiesToFields() {
        BatchSyncProperties sync = new BatchSyncProperties() {
            @Override
            public int backoffStepSize() {
                return 2;
            }

            @Override
            public int maxDelay() {
                return 30;
            }

            @Override
            public int maxTaskRetries() {
                return 5;
            }

            @Override
            public int maxExecutionAttempts() {
                return 9;
            }

            @Override
            public int maxLeaseExpiryPercent() {
                return 80;
            }

            @Override
            public boolean runOnStartup() {
                return false;
            }
        };

        BatchRunTimerConfigProperties timer = new BatchRunTimerConfigProperties(1000L, 10L);
        TestBatchSchedulerConfig config = new TestBatchSchedulerConfig(sync, timer);

        assertEquals("test-batch", config.getName());
        assertEquals(2, config.getBackOffStepSize());
        assertEquals(30, config.getMaxDelay());
        assertEquals(5, config.getMaxTaskRetries());
        assertEquals(9, config.getMaxExecutionAttempts());
        assertFalse(config.isRunOnStartUp());
        assertEquals(1000L, config.getTimerFrequencyMs());
        assertEquals(10L, config.getTimerScheduleWindowSeconds());
    }

    private static final class TestBatchSchedulerConfig extends BatchSchedulerConfig {

        private TestBatchSchedulerConfig(BatchSyncProperties syncProps, BatchRunTimerConfigProperties runTimerProps) {
            super(syncProps, runTimerProps);
        }

        @Override
        public String getName() {
            return "test-batch";
        }
    }
}

