package com.thanlinardos.spring_enterprise_library.batch;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.annotations.TimeFactoryExtension;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchRunTimerConfigProperties;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchSchedulerConfig;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchTaskSchedulerRegistration;
import com.thanlinardos.spring_enterprise_library.batch.properties.api.BatchSyncProperties;
import com.thanlinardos.spring_enterprise_library.stubs.StubScheduledFuture;
import com.thanlinardos.spring_enterprise_library.stubs.StubTaskScheduler;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.scheduling.TaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
@ExtendWith(TimeFactoryExtension.class)
class BaseBatchRunTimerTest {

    private static final int SCHEDULING_WINDOW_SECONDS = 30;

    @Test
    void initAndScheduleRuns_shouldScheduleStartupAndWindowedTasks() {
        StubScheduledFuture future = new StubScheduledFuture();
        StubTaskScheduler scheduler = new StubTaskScheduler(future);

        TestBatchRunTimer timer = getTestBatchRunTimer(scheduler);
        ConcurrentHashMap<String, Task> runs = new ConcurrentHashMap<>();

        timer.initRuns(runs);
        assertEquals(1, runs.size());

        runs.put("normal", Task.forRegister("normal", TimeFactory.getInstant().plusSeconds(10)));
        timer.scheduleRuns(runs);

        assertTrue(scheduler.getScheduleCount() >= 2);
    }

    private static TestBatchRunTimer getTestBatchRunTimer(StubTaskScheduler scheduler) {
        TestConfig startupConfig = new TestConfig("startup", true);
        TestConfig normalConfig = new TestConfig("normal", false);

        Map<String, BatchTaskSchedulerRegistration<TestConfig>> registrations = Map.of(
                "startup", new BatchTaskSchedulerRegistration<>(startupConfig, () -> {
                }),
                "normal", new BatchTaskSchedulerRegistration<>(normalConfig, () -> {
                })
        );

        return new TestBatchRunTimer(scheduler, registrations);
    }

    private static final class TestBatchRunTimer extends BaseBatchRunTimer<TestConfig> {

        private TestBatchRunTimer(TaskScheduler taskScheduler,
                                  Map<String, BatchTaskSchedulerRegistration<TestConfig>> registeredSchedulers) {
            super(taskScheduler, registeredSchedulers, SCHEDULING_WINDOW_SECONDS);
        }
    }

    private static final class TestConfig extends BatchSchedulerConfig {
        private final String name;

        private TestConfig(String name, boolean runOnStartup) {
            super(new BatchSyncProperties() {
                      @Override
                      public int backoffStepSize() {
                          return 1;
                      }

                      @Override
                      public int maxDelay() {
                          return 5;
                      }

                      @Override
                      public int maxTaskRetries() {
                          return 2;
                      }

                      @Override
                      public int maxExecutionAttempts() {
                          return 2;
                      }

                      @Override
                      public int maxLeaseExpiryPercent() {
                          return 80;
                      }

                      @Override
                      public boolean runOnStartup() {
                          return runOnStartup;
                      }
                  },
                    new BatchRunTimerConfigProperties(1000, SCHEDULING_WINDOW_SECONDS));
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}


