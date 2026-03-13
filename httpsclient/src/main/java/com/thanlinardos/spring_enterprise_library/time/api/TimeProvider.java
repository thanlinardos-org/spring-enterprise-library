package com.thanlinardos.spring_enterprise_library.time.api;

import com.thanlinardos.spring_enterprise_library.time.model.InstantInterval;
import com.thanlinardos.spring_enterprise_library.time.model.TimeInterval;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * TimeProvider is an interface that provides methods to obtain the current date and time,
 * the default {@link TimeUnit} accuracy to use, the time zone and over configured values
 * that are used across the application when handling date and date-time data.
 */
public interface TimeProvider {

    /**
     * Gets the accuracy of the TimeProvider as TimeUnit.
     *
     * @return the accuracy as TimeUnit
     */
    TimeUnit accuracy();

    /**
     * Gets the default ZoneOffset of the TimeProvider.
     *
     * @return the default ZoneOffset
     */
    ZoneOffset getDefaultZone();

    /**
     * Gets the accuracy of the TimeProvider as ChronoUnit.
     *
     * @return the accuracy as ChronoUnit
     */
    ChronoUnit getChronoAccuracy();

    /**
     * Obtains the current date-time from the system clock in the specified time-zone.
     *
     * @return the current date-time of specific time zone
     */
    LocalDateTime getCurrentDateTime();

    /**
     * Obtains the current instant from the system clock in the default time-zone.
     *
     * @return the current instant of specific time zone
     */
    Instant getCurrentInstant();

    /**
     * Obtains the current date from the system clock in the specified time-zone.
     *
     * @return the current date of specific time zone
     */
    LocalDate getCurrentDate();

    /**
     * Obtains the current time in milliseconds from the system clock in the specified time-zone.
     *
     * @return the current time in milliseconds.
     */
    long getCurrentTimeMillis();

    /**
     * Obtains an instance of LocalDateTime from provided milliseconds in the specified time-zone.
     *
     * @param milliseconds the milliseconds to convert
     * @return LocalDateTime object from milliseconds
     */
    LocalDateTime fromMillis(long milliseconds);

    /**
     * Converts this LocalDateTime to the number of milliseconds from the epoch of 1970-01-01T00:00:00Z in the specified time-zone.
     *
     * @param ldt the LocalDateTime to convert
     * @return number of milliseconds from LocalDateTime object from epoch
     */
    long toMillis(LocalDateTime ldt);

    /**
     * Returns LocalDateTime with the time of the end of the day based on given LocalDate.
     * NOTE: Returned time will be 23:59 (where LocalTime.MAX is 23:59:59.999999999)
     *
     * @param date the given date
     * @return number of milliseconds from LocalDateTime object from epoch
     */
    LocalDateTime getEndOfDay(LocalDate date);

    /**
     * Returns the LocalDateTime with the start time of the day based on the given LocalDate.
     *
     * @param date the given date
     * @return number of milliseconds from LocalDateTime object from epoch
     */
    LocalDateTime getStartOfDay(LocalDate date);

    /**
     * Returns new instance of LocalDate with first day of quarter for given date.
     * <p>
     * getFirstDayOfQuarter(LocalDate.of(2016, 2, 29)) == LocalDate.of(2016,1,1)
     * getFirstDayOfQuarter(LocalDate.of(2017, 10, 16)) == LocalDate.of(2017,10,1)
     *
     * @param dateInQuarter - any date in the quarter
     * @return First day of quarter
     */
    LocalDate getFirstDayOfQuarter(LocalDate dateInQuarter);

    /**
     * Returns new instance of LocalDate with last day of quarter for given date.
     *
     * <p>
     * getFirstDayOfQuarter(LocalDate.of(2016, 2, 29)) == LocalDate.of(2016, 3, 31)
     * getFirstDayOfQuarter(LocalDate.of(2017, 10, 16)) == LocalDate.of(2017,12,31)
     *
     * @param dateInQuarter - any date in the quarter
     * @return First day of quarter
     */
    LocalDate getLastDayOfQuarter(LocalDate dateInQuarter);

    /**
     * Return new instance of LocalDate with first day of the year.
     *
     * @param date - any date in the year
     * @return First day of year
     */
    LocalDate getFirstDayOfYear(LocalDate date);

    /**
     * Return new instance of LocalDate with last day of the year.
     *
     * @param date - any date in the year
     * @return First day of year
     */
    LocalDate getLastDayOfYear(LocalDate date);

    /**
     * Gets the time zone of the TimeProvider.
     *
     * @return the time zone
     */
    ZoneId zoneId();

    /**
     * Gets the maximum date that can be used in the system.
     *
     * @return the maximum date.
     */
    LocalDate maxDate();

    /**
     * Gets the minimum date that can be used in the system.
     *
     * @return the minimum date.
     */
    LocalDate minDate();

    /**
     * Gets the maximum date time that can be used in the system.
     *
     * @return the maximum date time.
     */
    LocalDateTime maxDateTime();

    /**
     * Gets the minimum date time that can be used in the system.
     *
     * @return the minimum date time.
     */
    LocalDateTime minDateTime();

    /**
     * Creates an {@link InstantInterval} starting from now plus the given seconds.
     *
     * @param seconds the number of seconds to add to the current instant.
     * @return an {@link InstantInterval} starting from now plus the given seconds.
     */
    InstantInterval instantFromNowPlusSeconds(long seconds);

    /**
     * Creates a {@link TimeInterval} starting from now plus the given seconds.
     *
     * @param seconds the number of seconds to add to the current time.
     * @return a {@link TimeInterval} starting from now plus the given seconds.
     */
    TimeInterval timeFromNowPlusSeconds(long seconds);
}
