package com.thanlinardos.spring_enterprise_library.batch.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed binding for the batch run-timer settings.
 *
 * @param frequencyMs           how often (in ms) the run-timer fires to check for due batch jobs.
 * @param scheduleWindowSeconds window (in seconds) within which a job is considered due for scheduling.
 */
@ConfigurationProperties(prefix = "thanlinardos.springenterpriselibrary.batch.run-timer")
public record BatchRunTimerConfigProperties(long frequencyMs, long scheduleWindowSeconds) {
}