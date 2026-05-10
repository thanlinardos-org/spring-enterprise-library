package com.thanlinardos.spring_enterprise_library.batch;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@CoreTest
class TaskTest {

    @Test
    void forRegister_shouldCreateUnscheduledTaskWithRetryZero() {
        Instant runTime = Instant.parse("2025-01-01T00:00:00Z");

        Task task = Task.forRegister("job", runTime);

        assertEquals("job", task.getName());
        assertEquals(0, task.getRetryCount());
        assertEquals(runTime, task.getRunTime());
        assertFalse(task.isScheduled());
        assertTrue(task.isNotScheduledOrIsDone());
    }

    @Test
    void forReschedule_shouldReuseTaskDetailsAndSetFuture() {
        Instant runTime = Instant.parse("2025-01-01T00:00:00Z");
        Task original = new Task("job", 2, runTime);
        ScheduledFuture<?> future = mock(ScheduledFuture.class);

        Task rescheduled = Task.forReschedule(original, future);

        assertEquals("job", rescheduled.getName());
        assertEquals(2, rescheduled.getRetryCount());
        assertEquals(runTime, rescheduled.getRunTime());
        assertTrue(rescheduled.isScheduled());
    }

    @Test
    void isScheduledRetry_shouldRequireScheduledNotDoneAndRetryPositive() {
        Instant runTime = Instant.parse("2025-01-01T00:00:00Z");
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        when(future.isDone()).thenReturn(false);

        Task retryTask = new Task("job", future, 1, runTime);
        Task noRetryTask = new Task("job", future, 0, runTime);

        assertTrue(retryTask.isScheduledRetry());
        assertFalse(noRetryTask.isScheduledRetry());
    }

    @Test
    void scheduledStateMethods_shouldReflectFutureStatus() {
        Instant runTime = Instant.parse("2025-01-01T00:00:00Z");
        ScheduledFuture<?> doneCanceledFuture = mock(ScheduledFuture.class);
        when(doneCanceledFuture.isDone()).thenReturn(true);
        when(doneCanceledFuture.isCancelled()).thenReturn(true);

        Task task = new Task("job", doneCanceledFuture, 1, runTime);

        assertTrue(task.isScheduledDone());
        assertTrue(task.isScheduledCanceled());
        assertTrue(task.isNotScheduledOrIsDone());
    }

    @Test
    void toString_shouldIncludeMainFieldsAndRuntimeFlags() {
        Instant runTime = Instant.parse("2025-01-01T00:00:00Z");
        ScheduledFuture<?> future = mock(ScheduledFuture.class);
        when(future.isDone()).thenReturn(false);
        when(future.isCancelled()).thenReturn(false);

        Task task = new Task("job", future, 3, runTime);
        task.setMarkedForCancellation(true);

        String value = task.toString();

        assertTrue(value.contains("name='job'"));
        assertTrue(value.contains("retryCount=3"));
        assertTrue(value.contains("isMarkedForCancellation=true"));
        assertTrue(value.contains("isScheduled=true"));
        assertTrue(value.contains("isDone=false"));
        assertTrue(value.contains("isCanceled=false"));
    }
}

