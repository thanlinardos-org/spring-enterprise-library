package com.thanlinardos.spring_enterprise_library.batch.properties;

public record TaskExecutionProperties(int backOffStepSize, int maxDelay, int maxTaskRetries) {
}
