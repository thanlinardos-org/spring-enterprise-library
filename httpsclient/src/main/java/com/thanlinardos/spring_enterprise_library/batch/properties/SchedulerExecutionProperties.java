package com.thanlinardos.spring_enterprise_library.batch.properties;

public record SchedulerExecutionProperties(int maxExecutionAttempts, boolean runOnStartUp, long timerFrequencyMs, long timerScheduleWindowSeconds) {
}
