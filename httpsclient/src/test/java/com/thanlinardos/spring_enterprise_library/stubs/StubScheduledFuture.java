package com.thanlinardos.spring_enterprise_library.stubs;

import jakarta.annotation.Nonnull;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StubScheduledFuture implements ScheduledFuture<Void> {

    @Override
    public long getDelay(@Nonnull TimeUnit unit) {
        return 0;
    }

    @Override
    public int compareTo(@Nonnull Delayed o) {
        return 0;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public Void get() {
        return null;
    }

    @Override
    public Void get(long timeout, @Nonnull TimeUnit unit) {
        return null;
    }
}
