package com.thanlinardos.spring_enterprise_library.time;

import com.thanlinardos.spring_enterprise_library.time.api.TimeProvider;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * Factory class for obtaining the current date and time, as well as time zone information.
 * This class uses a TimeProvider to get the current date and time, allowing for easy testing and customization.
 */
@Lazy(false)
@Component
public class TimeFactory {

    private static TimeProvider timeProvider;

    /**
     * Constructor for TimeFactory.
     *
     * @param timeProvider the TimeProvider to use for getting the current date and time.
     */
    @SuppressWarnings({"java:S3010", "java:S1118"}) // necessary to have static access to time constants
    public TimeFactory(TimeProvider timeProvider) {
        TimeFactory.timeProvider = timeProvider;
    }

    /**
     * Asserts that the TimeProvider has been initialized by Spring before any static method is called.
     * This guards against calling TimeFactory static methods in bean constructors before Spring
     * has had a chance to instantiate TimeFactory (e.g. when lazy-initialization is enabled globally).
     *
     * @throws IllegalStateException if TimeFactory has not been initialized yet.
     */
    private static void assertInitialized() {
        if (timeProvider == null) {
            throw new IllegalStateException(
                    """
                            TimeFactory has not been initialized yet. \
                            Ensure that TimeFactory is instantiated by Spring before calling its static methods. \
                            If using spring.main.lazy-initialization=true, add @DependsOn("timeFactory") \
                            to any bean that calls TimeFactory in its constructor."""
            );
        }
    }

    /**
     * Gets the accuracy of the date provider.
     *
     * @return the accuracy of the date provider.
     */
    public static TimeUnit getAccuracy() {
        assertInitialized();
        return timeProvider.accuracy();
    }

    public static LocalDate getDate() {
        assertInitialized();
        return timeProvider.getCurrentDate();
    }

    public static LocalDateTime getDateTime() {
        assertInitialized();
        return timeProvider.getCurrentDateTime();
    }

    public static Instant getInstant() {
        assertInitialized();
        return timeProvider.getCurrentInstant();
    }

    public static LocalDate getMaxDate() {
        assertInitialized();
        return timeProvider.maxDate();
    }

    public static LocalDate getMinDate() {
        assertInitialized();
        return timeProvider.minDate();
    }

    public static LocalDateTime getMaxDateTime() {
        assertInitialized();
        return timeProvider.maxDateTime();
    }

    public static LocalDateTime getMinDateTime() {
        assertInitialized();
        return timeProvider.minDateTime();
    }

    public static Instant getMaxInstant() {
        assertInitialized();
        return getMaxDateTime().toInstant(getDefaultZone());
    }

    public static Instant getMinInstant() {
        assertInitialized();
        return getMinDateTime().toInstant(getDefaultZone());
    }

    public static ZoneId getDefaultZoneId() {
        assertInitialized();
        return timeProvider.zoneId();
    }

    public static ZoneOffset getDefaultZone() {
        assertInitialized();
        return timeProvider.getDefaultZone();
    }
}
