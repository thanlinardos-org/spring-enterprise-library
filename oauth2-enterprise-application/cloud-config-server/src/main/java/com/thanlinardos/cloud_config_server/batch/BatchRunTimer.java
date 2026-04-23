package com.thanlinardos.cloud_config_server.batch;

import com.thanlinardos.spring_enterprise_library.batch.BaseBatchRunTimer;
import com.thanlinardos.spring_enterprise_library.batch.BatchTaskScheduler;
import com.thanlinardos.spring_enterprise_library.batch.Task;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchTaskSchedulerRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Schedules batch runs of the {@link BatchTaskScheduler} based on their configuration and execution status:
 * <p>
 * - Maintains a map of currently scheduled and completed runs.
 * <p>
 * - Runs periodically and schedules new runs within a defined scheduling window.
 */
@Component
@Slf4j
@ConditionalOnProperty(value = {"batch.run-timer.enabled"}, havingValue = "true")
public final class BatchRunTimer extends BaseBatchRunTimer {

    private static final ConcurrentMap<String, Task> BATCH_RUNS = new ConcurrentHashMap<>();

    public BatchRunTimer(ThreadPoolTaskScheduler taskScheduler, Map<String, BatchTaskSchedulerRegistration<?>> registeredSchedulers, @Value("${batch.run-timer.schedule-window-seconds}") long schedulingWindowSeconds) {
        super(taskScheduler, registeredSchedulers, schedulingWindowSeconds);
        initRuns(BATCH_RUNS);
    }

    public static ConcurrentMap<String, Task> getBatchRuns() {
        return BATCH_RUNS;
    }

    @Scheduled(fixedDelayString = "${batch.run-timer.frequency-ms}", initialDelayString = "${batch.run-timer.initial-delay-ms}")
    public void scheduleRuns() {
        scheduleRuns(BATCH_RUNS);
    }
}
