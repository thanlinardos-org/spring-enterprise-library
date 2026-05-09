package com.thanlinardos.spring_enterprise_library.batch;

import com.thanlinardos.spring_enterprise_library.batch.properties.BatchSchedulerConfig;
import com.thanlinardos.spring_enterprise_library.batch.properties.BatchTaskSchedulerRegistration;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.model.InstantInterval;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Getter
public abstract class BaseBatchRunTimer<C extends BatchSchedulerConfig> {

    private final ThreadPoolTaskScheduler taskScheduler;
    private final Map<String, BatchTaskSchedulerRegistration<C>> registeredSchedulers;
    private final long schedulingWindowSeconds;

    protected void initRuns(ConcurrentMap<String, Task> batchRuns) {
        this.registeredSchedulers.values().stream()
                .filter(BatchTaskSchedulerRegistration::isRunOnStartUp)
                .forEach(registration -> scheduleRunOnStartUp(registration, batchRuns));
    }

    protected void scheduleRuns(ConcurrentMap<String, Task> batchRuns) {
        Map<String, Task> newJobRuns = batchRuns.values().stream()
                .filter(Predicate.not(Task::isScheduled))
                .filter(this::isInsideSchedulingWindow)
                .map(this::scheduleRun)
                .collect(Collectors.toMap(Task::getName, Function.identity()));
        batchRuns.putAll(newJobRuns);
    }

    private void scheduleRunOnStartUp(BatchTaskSchedulerRegistration<?> registration, ConcurrentMap<String, Task> batchRuns) {
        String schedulerName = registration.config().getName();
        Instant now = TimeFactory.getInstant();
        ScheduledFuture<?> scheduledFuture = this.taskScheduler.schedule(registration.runnable(), now);
        Task run = new Task(schedulerName, scheduledFuture, 0, now);
        batchRuns.put(schedulerName, run);
        log.info("Scheduled task scheduler '{}' to run on startup.", schedulerName);
    }

    private Task scheduleRun(Task run) {
        Runnable runnable = registeredSchedulers.get(run.getName()).runnable();
        ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(runnable, run.getRunTime());

        Task newRun = Task.forReschedule(run, scheduledFuture);
        log.info("Scheduled new job run: {}", newRun);
        return newRun;
    }

    private boolean isInsideSchedulingWindow(Task run) {
        Instant now = TimeFactory.getInstant();
        InstantInterval schedulingWindow = new InstantInterval(now, now.plusSeconds(schedulingWindowSeconds));
        return schedulingWindow.contains(run.getRunTime());
    }
}
