package com.thanlinardos.spring_enterprise_library.batch.properties;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.batch.properties.api.BatchSyncProperties;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class BatchTaskSchedulerRegistrationTest {

    @Test
    void isRunOnStartUp_shouldDelegateToConfig() {
        BatchTaskSchedulerRegistration<TestBatchSchedulerConfig> startupEnabled = new BatchTaskSchedulerRegistration<>(
                new TestBatchSchedulerConfig(true), () -> {
                });
        BatchTaskSchedulerRegistration<TestBatchSchedulerConfig> startupDisabled = new BatchTaskSchedulerRegistration<>(
                new TestBatchSchedulerConfig(false), () -> {
                });

        assertTrue(startupEnabled.isRunOnStartUp());
        assertFalse(startupDisabled.isRunOnStartUp());
        assertNotNull(startupEnabled.runnable());
    }

    private static final class TestBatchSchedulerConfig extends BatchSchedulerConfig {

        private final boolean startup;

        private TestBatchSchedulerConfig(boolean startup) {
            super(new BatchSyncProperties() {
                      @Override
                      public int backoffStepSize() {
                          return 1;
                      }

                      @Override
                      public int maxDelay() {
                          return 1;
                      }

                      @Override
                      public int maxTaskRetries() {
                          return 1;
                      }

                      @Override
                      public int maxExecutionAttempts() {
                          return 1;
                      }

                      @Override
                      public int maxLeaseExpiryPercent() {
                          return 50;
                      }

                      @Override
                      public boolean runOnStartup() {
                          return startup;
                      }
                  },
                    new BatchRunTimerConfigProperties(100L, 2L));
            this.startup = startup;
        }

        @Override
        public String getName() {
            return startup ? "startup" : "no-startup";
        }
    }
}

