package com.thanlinardos.spring_enterprise_library.batch.properties;

import com.thanlinardos.spring_enterprise_library.batch.properties.api.BatchSyncProperties;
import lombok.Getter;

/**
 * Base configuration for batch schedulers, holding all execution and timing parameters directly.
 */
@Getter
public abstract class BatchSchedulerConfig {

    /** Step size in seconds to increase the delay between retries. */
    private final int backOffStepSize;
    /** Maximum delay in seconds between retries. */
    private final int maxDelay;
    /** Maximum number of retries for a task. */
    private final int maxTaskRetries;
    /** Maximum number of execution attempts for the batch scheduler. */
    private final int maxExecutionAttempts;
    /** Whether the batch scheduler should run on application startup. */
    private final boolean runOnStartUp;
    /** Frequency in milliseconds at which the batch run timer checks for tasks to schedule. */
    private final long timerFrequencyMs;
    /** Scheduling window in seconds within which tasks can be scheduled. */
    private final long timerScheduleWindowSeconds;

    protected BatchSchedulerConfig(BatchSyncProperties syncProps, BatchRunTimerConfigProperties runTimerProps) {
        backOffStepSize = syncProps.backoffStepSize();
        maxDelay = syncProps.maxDelay();
        maxTaskRetries = syncProps.maxTaskRetries();
        maxExecutionAttempts = syncProps.maxExecutionAttempts();
        runOnStartUp = syncProps.runOnStartup();
        timerFrequencyMs = runTimerProps.frequencyMs();
        timerScheduleWindowSeconds = runTimerProps.scheduleWindowSeconds();
    }

    /**
     * The name of the batch scheduler.
     *
     * @return the batch scheduler name.
     */
    public abstract String getName();
}

