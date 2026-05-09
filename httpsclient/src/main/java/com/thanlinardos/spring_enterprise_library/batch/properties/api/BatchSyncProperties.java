package com.thanlinardos.spring_enterprise_library.batch.properties.api;

public interface BatchSyncProperties {

        /**
         * Seconds added per back-off step when a task fails.
         */
        int backoffStepSize();

        /**
         * Maximum back-off delay in seconds.
         */
        int maxDelay();

        /**
         * Maximum number of task-level retry attempts.
         */
        int maxTaskRetries();

        /**
         * Maximum number of scheduler execution attempts per cycle.
         */
        int maxExecutionAttempts();

        /**
         * Percentage of lease duration after which a secret is renewed (0-100).
         */
        int maxLeaseExpiryPercent();

        /**
         * Whether to trigger an initial run immediately on application startup.
         */
        boolean runOnStartup();
}
