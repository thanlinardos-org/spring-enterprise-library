package com.thanlinardos.spring_enterprise_library.batch.properties;

public record BatchTaskSchedulerRegistration<C extends BatchSchedulerConfig>(C config, Runnable runnable) {

    public boolean isRunOnStartUp() {
        return config.isRunOnStartUp();
    }
}
