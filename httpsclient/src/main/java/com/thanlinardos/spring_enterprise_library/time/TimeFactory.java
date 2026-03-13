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
     * Gets the accuracy of the date provider.
     *
     * @return the accuracy of the date provider.
     */
    public static TimeUnit getAccuracy() {
        return timeProvider.accuracy();
    }

    /**
     * Gets the current date.
     *
     * @return the current date.
     */
    public static LocalDate getDate() {
        return timeProvider.getCurrentDate();
    }

    /**
     * Gets the current date time.
     *
     * @return the current date time.
     */
    public static LocalDateTime getDateTime() {
        return timeProvider.getCurrentDateTime();
    }

    /**
     * Gets the current instant.
     *
     * @return the current instant.
     */
    public static Instant getInstant() {
        return timeProvider.getCurrentInstant();
    }

    /**
     * Gets the maximum date that can be used in the system.
     *
     * @return the maximum date that can be used in the system.
     */
    public static LocalDate getMaxDate() {
        return timeProvider.maxDate();
    }

    /**
     * Gets the minimum date that can be used in the system.
     *
     * @return the minimum date that can be used in the system.
     */
    public static LocalDate getMinDate() {
        return timeProvider.minDate();
    }

    /**
     * Gets the maximum date time that can be used in the system.
     *
     * @return the maximum date time that can be used in the system.
     */
    public static LocalDateTime getMaxDateTime() {
        return timeProvider.maxDateTime();
    }

    /**
     * Gets the minimum date time that can be used in the system.
     *
     * @return the minimum date time that can be used in the system.
     */
    public static LocalDateTime getMinDateTime() {
        return timeProvider.minDateTime();
    }

    /**
     * Gets the minimum instant that can be used in the system.
     *
     * @return the minimum instant that can be used in the system.
     */
    public static Instant getMaxInstant() {
        return getMaxDateTime().toInstant(getDefaultZone());
    }

    /**
     * Gets the minimum instant that can be used in the system.
     *
     * @return the minimum instant that can be used in the system.
     */
    public static Instant getMinInstant() {
        return getMinDateTime().toInstant(getDefaultZone());
    }

    /**
     * Gets current time zone.
     *
     * @return the current time zone as a ZoneId object.
     */
    public static ZoneId getDefaultZoneId() {
        return timeProvider.zoneId();
    }

    /**
     * Gets current time zone.
     *
     * @return the current time zone as a ZoneOffset object.
     */
    public static ZoneOffset getDefaultZone() {
        return timeProvider.getDefaultZone();
    }
}
