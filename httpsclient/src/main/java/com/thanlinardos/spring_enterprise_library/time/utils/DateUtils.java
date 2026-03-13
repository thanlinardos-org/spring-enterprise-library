package com.thanlinardos.spring_enterprise_library.time.utils;

import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.function.Function;

import static com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants.NULL_AS_MAX_COMPARATOR;
import static com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants.NULL_AS_MIN_COMPARATOR;

/**
 * Utility class for date operations.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    /**
     * Gets the first day of the given year.
     *
     * @param year the year to get the first day of.
     * @return the first day of the given year.
     */
    public static LocalDate getLastDayOfYear(Year year) {
        return year.plusYears(1).atDay(1).minusDays(1);
    }

    /**
     * Converts epoch milliseconds to {@link LocalDateTime} in the system default time zone {@link TimeFactory#getDefaultZoneId()}.
     *
     * @param epochMilli the epoch milliseconds to convert.
     * @return the corresponding {@link LocalDateTime}.
     */
    public static LocalDateTime getLocalDateTimeFromEpochMilli(Long epochMilli) {
        return epochMilliToZonedDateTime(epochMilli).toLocalDateTime();
    }

    /**
     * Converts epoch milliseconds to {@link LocalDate} in the system default time zone {@link TimeFactory#getDefaultZoneId()}.
     *
     * @param epochMilli the epoch milliseconds to convert.
     * @return the corresponding {@link LocalDate}.
     */
    public static LocalDate getLocalDateFromEpochMilli(Long epochMilli) {
        return epochMilliToZonedDateTime(epochMilli).toLocalDate();
    }

    /**
     * Converts epoch milliseconds to {@link ZonedDateTime} in the system default time zone {@link TimeFactory#getDefaultZoneId()}.
     *
     * @param epochMilli the epoch milliseconds to convert.
     * @return the corresponding {@link ZonedDateTime}.
     */
    public static ZonedDateTime epochMilliToZonedDateTime(Long epochMilli) {
        return Instant.ofEpochMilli(epochMilli).atZone(TimeFactory.getDefaultZoneId());
    }

    /**
     * Converts {@link LocalDateTime} to epoch milliseconds in the system default time zone {@link TimeFactory#getDefaultZoneId()}.
     *
     * @param localDateTime the {@link LocalDateTime} to convert.
     * @return the corresponding epoch milliseconds.
     */
    public static long getEpochMilliFromLocalDateTime(@Nonnull LocalDateTime localDateTime) {
        return localDateTime.atZone(TimeFactory.getDefaultZoneId())
                .toInstant()
                .toEpochMilli();
    }

    /**
     * Checks if date1 is before date2, treating null as {@link TimeFactory#getMinDate()}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is before date2, false otherwise.
     */
    public static boolean isBeforeNullAsMin(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return date1 == null || (date2 != null && date1.isBefore(date2));
    }

    /**
     * Checks if date1 is after date2, treating null as {@link TimeFactory#getMaxDate()}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is after date2, false otherwise.
     */
    public static boolean isAfterNullAsMax(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return date1 == null || (date2 != null && date1.isAfter(date2));
    }

    /**
     * Checks if date1 is before or equal to date2, treating null as {@link TimeFactory#getMinDate()}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is before or equal to date2, false otherwise.
     */
    public static boolean isBeforeOrEqual(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return Objects.equals(date1, date2) || isBeforeNullAsMin(date1, date2);
    }

    /**
     * Checks if date1 is after or equal to date2, treating null as {@link TimeFactory#getMaxDate()}.
     *
     * @param date1 The first date to compare.
     * @param date2 The second date to compare.
     * @return true if date1 is after or equal to date2, false otherwise.
     */
    public static boolean isAfterOrEqual(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return Objects.equals(date1, date2) || isAfterNullAsMax(date1, date2);
    }

    /**
     * Returns the maximum of two LocalDate values, treating null as the maximum possible date.
     *
     * @param date1 The first LocalDate value, which may be null.
     * @param date2 The second LocalDate value, which may be null.
     * @return The maximum of the two LocalDate values, or null if both are null.
     */
    public static LocalDate maxNullAsMax(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return NULL_AS_MAX_COMPARATOR.compare(date1, date2) >= 0 ? date1 : date2;
    }

    /**
     * Returns the minimum of two LocalDate values, treating null as the minimum possible date.
     *
     * @param date1 The first LocalDate value, which may be null.
     * @param date2 The second LocalDate value, which may be null.
     * @return The minimum of the two LocalDate values, or null if both are null.
     */
    public static LocalDate minNullAsMin(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return NULL_AS_MIN_COMPARATOR.compare(date1, date2) <= 0 ? date1 : date2;
    }

    /**
     * Returns the maximum of two LocalDate values, treating null as the minimum possible date.
     *
     * @param date1 The first LocalDate value, which may be null.
     * @param date2 The second LocalDate value, which may be null.
     * @return The maximum of the two LocalDate values, or null if both are null.
     */
    public static LocalDate maxNullAsMin(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return NULL_AS_MIN_COMPARATOR.compare(date1, date2) >= 0 ? date1 : date2;
    }

    /**
     * Returns the minimum of two LocalDate values, treating null as the maximum possible date.
     *
     * @param date1 The first LocalDate value, which may be null.
     * @param date2 The second LocalDate value, which may be null.
     * @return The minimum of the two LocalDate values, or null if both are null.
     */
    public static LocalDate minNullAsMax(@Nullable LocalDate date1, @Nullable LocalDate date2) {
        return NULL_AS_MAX_COMPARATOR.compare(date1, date2) <= 0 ? date1 : date2;
    }

    /**
     * Adds the specified number of days to the given date.
     *
     * @param date the date to add days to, may be null.
     * @param days the number of days to add, may be negative to subtract days.
     * @return the new date with the added days, or null if the input date was null.
     */
    @Nullable
    public static LocalDate addDays(@Nullable LocalDate date, int days) {
        return date == null ? null : date.plusDays(days);
    }

    /**
     * Adds one day to the given date.
     *
     * @param date the date to add a day to, may be null.
     * @return the new date with one day added, or null if the input date was null.
     */
    @Nullable
    public static LocalDate addDay(@Nullable LocalDate date) {
        return addDays(date, 1);
    }

    /**
     * Subtracts one day from the given date.
     *
     * @param date the date to subtract a day from, may be null.
     * @return the new date with one day subtracted, or null if the input date was null.
     */
    @Nullable
    public static LocalDate subtractDay(@Nullable LocalDate date) {
        return addDays(date, -1);
    }

    /**
     * Parses a date string into a LocalDate using the default parser from {@link LocalDate#parse(CharSequence)}.
     *
     * @param startDate the date string to parse.
     * @return the parsed LocalDate, or null if the input string is empty.
     */
    public static LocalDate parseLocalDate(String startDate) {
        return parseDate(startDate, LocalDate::parse);
    }

    /**
     * Parses a date string using the provided parser function.
     *
     * @param date   the date string to parse.
     * @param parser the function to parse the date.
     * @return the parsed LocalDate, or null if the input string is empty.
     */
    public static LocalDate parseDate(@Nullable String date, Function<String, LocalDate> parser) {
        return StringUtils.hasLength(date) ? parser.apply(date) : null;
    }
}
