package com.thanlinardos.spring_enterprise_library.batch;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchRunTimerConfigProperties;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchSchedulerConfig;
import com.thanlinardos.spring_enterprise_library.batch.properties.api.BatchSyncProperties;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@CoreTest
class BatchTaskSchedulerTest {

    @Test
    void start_whenExecuteSucceeds_shouldRegisterNextRun() {
        ThreadPoolTaskScheduler scheduler = mock(ThreadPoolTaskScheduler.class);
        when(scheduler.schedule(any(Runnable.class), any(Instant.class))).thenReturn(mock(ScheduledFuture.class));

        TestBatchTaskScheduler taskScheduler = new TestBatchTaskScheduler(scheduler, new TestConfig("batch", 2, 2), TimeFactory.getInstant());
        taskScheduler.nextRun = TimeFactory.getInstant();

        taskScheduler.start();

        assertNotNull(taskScheduler.getBatchRuns().get("batch"));
    }

    @Test
    void start_whenExecuteFails_shouldCancelAndScheduleAttempt() {
        ThreadPoolTaskScheduler scheduler = mock(ThreadPoolTaskScheduler.class);
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        when(scheduler.schedule(any(Runnable.class), any(Instant.class))).thenReturn(mock(ScheduledFuture.class));

        TestBatchTaskScheduler taskScheduler = new TestBatchTaskScheduler(scheduler, new TestConfig("batch", 2, 2), TimeFactory.getInstant());
        taskScheduler.throwOnExecute = true;
        taskScheduler.getScheduledTasks().put("x", new Task("x", future, 0, TimeFactory.getInstant()));

        taskScheduler.start();

        verify(future).cancel(true);
        assertNotNull(taskScheduler.getBatchRuns().get("batch"));
    }

    @Test
    void retryAndCancelHelpers_shouldHandleTaskStates() {
        ThreadPoolTaskScheduler scheduler = mock(ThreadPoolTaskScheduler.class);
        ScheduledFuture<?> retryFuture = mock(ScheduledFuture.class);
        ScheduledFuture<?> cancelFuture = mock(ScheduledFuture.class);
        doAnswer(invocation -> retryFuture).when(scheduler).schedule(any(Runnable.class), any(Instant.class));
        when(cancelFuture.cancel(false)).thenReturn(true);

        TestBatchTaskScheduler taskScheduler = new TestBatchTaskScheduler(scheduler, new TestConfig("batch", 1, 1), TimeFactory.getInstant());
        Task task = new Task("task-1", retryFuture, 0, TimeFactory.getInstant());
        taskScheduler.getScheduledTasks().put("task-1", task);

        Exception e = new RuntimeException("boom");
        taskScheduler.retryFailedTask(e, "task-1", () -> {});
        assertTrue(taskScheduler.getScheduledTasks().containsKey("task-1"));

        Task marked = new Task("task-2", cancelFuture, 0, TimeFactory.getInstant());
        marked.setMarkedForCancellation(true);
        taskScheduler.getScheduledTasks().put("task-2", marked);

        taskScheduler.cancelMarkedTasks();
        assertFalse(taskScheduler.getScheduledTasks().containsKey("task-2"));
        assertTrue(taskScheduler.isTaskNotScheduled("missing"));
    }

    private static final class TestBatchTaskScheduler extends BatchTaskScheduler<TestConfig> {
        private final ConcurrentMap<String, Task> batchRuns = new ConcurrentHashMap<>();
        private final Logger logger = mock(Logger.class);
        private boolean throwOnExecute = false;
        private Instant nextRun;

        private TestBatchTaskScheduler(ThreadPoolTaskScheduler taskScheduler, TestConfig config, Instant instant) {
            super(taskScheduler, config);
            nextRun = instant;
        }

        @Override
        protected Logger getLogger() {
            return logger;
        }

        @Override
        protected Instant execute() {
            if (throwOnExecute) {
                throw new RuntimeException("execute-failed");
            }
            return nextRun;
        }

        @Override
        protected ConcurrentMap<String, Task> getBatchRuns() {
            return batchRuns;
        }

        @Override
        protected String getTaskName(Object... args) {
            return "task";
        }

        @Override
        protected void logTaskLevel(String taskName, String message, org.slf4j.event.Level level, Object... args) {
            // Avoid coupling tests to logger builder internals.
        }

    }

    private static final class TestConfig extends BatchSchedulerConfig {
        private final String name;

        private TestConfig(String name, int maxTaskRetries, int maxExecutionAttempts) {
            super(new BatchSyncProperties() {
                      @Override
                      public int backoffStepSize() { return 1; }

                      @Override
                      public int maxDelay() { return 3; }

                      @Override
                      public int maxTaskRetries() { return maxTaskRetries; }

                      @Override
                      public int maxExecutionAttempts() { return maxExecutionAttempts; }

                      @Override
                      public int maxLeaseExpiryPercent() { return 80; }

                      @Override
                      public boolean runOnStartup() { return false; }
                  },
                    new BatchRunTimerConfigProperties(5000, 30));
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}


