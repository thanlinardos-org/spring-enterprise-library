package com.thanlinardos.spring_enterprise_library.stubs;

import jakarta.annotation.Nonnull;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class StubTaskScheduler implements TaskScheduler {
    private final AtomicInteger scheduleCount = new AtomicInteger();
    private final ScheduledFuture<?> future;

    public StubTaskScheduler(ScheduledFuture<?> future) {
        this.future = future;
    }

    public int getScheduleCount() {
        return scheduleCount.get();
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> schedule(@Nonnull Runnable task, @Nonnull Instant startTime) {
        scheduleCount.incrementAndGet();
        return future;
    }

    @Override
    public ScheduledFuture<?> schedule(@Nonnull Runnable task, @Nonnull Trigger trigger) {
        return null;
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@Nonnull Runnable task, @Nonnull Instant startTime, @Nonnull Duration period) {
        return new StubScheduledFuture();
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@Nonnull Runnable task, @Nonnull Duration period) {
        return new StubScheduledFuture();
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(@Nonnull Runnable task, @Nonnull Instant startTime, @Nonnull Duration delay) {
        return new StubScheduledFuture();
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(@Nonnull Runnable task, @Nonnull Duration delay) {
        return new StubScheduledFuture();
    }
}
