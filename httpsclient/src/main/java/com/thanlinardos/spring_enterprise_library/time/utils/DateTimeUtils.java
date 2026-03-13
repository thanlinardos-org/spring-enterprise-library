package com.thanlinardos.spring_enterprise_library.time.utils;

import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants.NULL_AS_MAX_DATE_TIME_COMPARATOR;
import static com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants.NULL_AS_MIN_DATE_TIME_COMPARATOR;

/**
 * Utility class for working with {@link LocalDateTime} objects.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    /**
     * Parses a dateTime string using the default parser in {@link LocalDateTime#parse(CharSequence)}.
     *
     * @param start the dateTime string to parse.
     * @return the parsed dateTime, or null if the input string is empty.
     */
    public static LocalDateTime parseDateTime(String start) {
        return parseDateTime(start, LocalDateTime::parse);
    }

    /**
     * Parses an dateTime string using the provided parser function.
     *
     * @param date   the dateTime string to parse.
     * @param parser the function to parse the dateTime.
     * @return the parsed dateTime, or null if the input string is empty.
     */
    public static LocalDateTime parseDateTime(@Nullable String date, Function<String, LocalDateTime> parser) {
        return StringUtils.hasLength(date) ? parser.apply(date) : null;
    }

    /**
     * Converts a {@link LocalDate} to a {@link LocalDateTime} at the start of the day (00:00).
     *
     * @param date the date to convert.
     * @return the converted dateTime, or null if the input date is null.
     */
    @Nullable
    public static LocalDateTime toStartOfDate(@Nullable LocalDate date) {
        return date == null ? null : date.atStartOfDay();
    }

    /**
     * Converts a {@link LocalDate} to a {@link LocalDateTime} at the end of the day (23:59:59.999...).
     * This is achieved by converting the date to the start of the next day and then subtracting a single unit of the default accuracy.
     * <p>
     * For example, if the default accuracy is set to MILLISECONDS, the resulting time will be 23:59:59.999.
     *
     * @param date the date to convert.
     * @return the converted dateTime, or null if the input date is null.
     */
    @Nullable
    public static LocalDateTime toEndOfDate(@Nullable LocalDate date) {
        return date == null ? null : subtractSingle(toStartOfDate(date.plusDays(1)));
    }

    /**
     * Converts a {@link LocalDate} to a {@link LocalDateTime} at the end of the day (23:59:59.999...).
     * See {@link DateTimeUtils#toEndOfDate(LocalDate)} for details.
     *
     * @param date     the date to convert.
     * @param accuracy the accuracy to use for subtraction, overriding the default.
     * @return the converted dateTime, or null if the input date is null.
     */
    @Nullable
    public static LocalDateTime toEndOfDate(@Nullable LocalDate date, TimeUnit accuracy) {
        return date == null ? null : subtractSingle(toStartOfDate(date.plusDays(1)), accuracy);
    }

    /**
     * Converts a {@link LocalDateTime} to a {@link LocalDate} with the default time zone.
     *
     * @param dateTime the dateTime to convert.
     * @return the converted date, or null if the input dateTime is null.
     */
    @Nullable
    public static LocalDate toDate(@Nullable LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(TimeFactory.getDefaultZone()).toLocalDate();
    }

    /**
     * Checks if dateTime1 is before dateTime2, treating null as {@link TimeFactory#getMinDate()}.
     *
     * @param dateTime1 The first date to compare.
     * @param dateTime2 The second date to compare.
     * @return true if dateTime1 is before dateTime2, false otherwise.
     */
    public static boolean isBeforeNullAsMin(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return dateTime1 == null || (dateTime2 != null && dateTime1.isBefore(dateTime2));
    }

    /**
     * Checks if dateTime1 is after dateTime2, treating null as {@link TimeFactory#getMaxDate()}.
     *
     * @param dateTime1 The first date to compare.
     * @param dateTime2 The second date to compare.
     * @return true if dateTime1 is after dateTime2, false otherwise.
     */
    public static boolean isAfterNullAsMax(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return dateTime1 == null || (dateTime2 != null && dateTime1.isAfter(dateTime2));
    }

    /**
     * Checks if dateTime1 is before or equal to dateTime2, treating null as {@link TimeFactory#getMinDate()}.
     *
     * @param dateTime1 The first date to compare.
     * @param dateTime2 The second date to compare.
     * @return true if dateTime1 is before or equal to dateTime2, false otherwise.
     */
    public static boolean isBeforeOrEqual(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return Objects.equals(dateTime1, dateTime2) || isBeforeNullAsMin(dateTime1, dateTime2);
    }

    /**
     * Checks if dateTime1 is after or equal to dateTime2, treating null as {@link TimeFactory#getMaxDate()}.
     *
     * @param dateTime1 The first date to compare.
     * @param dateTime2 The second date to compare.
     * @return true if dateTime1 is after or equal to dateTime2, false otherwise.
     */
    public static boolean isAfterOrEqual(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return Objects.equals(dateTime1, dateTime2) || isAfterNullAsMax(dateTime1, dateTime2);
    }

    /**
     * Returns the maximum of two dateTimes, treating null as {@link TimeFactory#getMaxDateTime()}.
     *
     * @param dateTime1 the first dateTime.
     * @param dateTime2 the second dateTime.
     * @return the maximum of the two dateTimes.
     */
    public static LocalDateTime maxNullAsMax(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return NULL_AS_MAX_DATE_TIME_COMPARATOR.compare(dateTime1, dateTime2) >= 0 ? dateTime1 : dateTime2;
    }

    /**
     * Returns the minimum of two dateTimes, treating null as {@link TimeFactory#getMinDateTime()}.
     *
     * @param dateTime1 the first dateTime.
     * @param dateTime2 the second dateTime.
     * @return the minimum of the two dateTimes.
     */
    public static LocalDateTime minNullAsMin(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return NULL_AS_MIN_DATE_TIME_COMPARATOR.compare(dateTime1, dateTime2) <= 0 ? dateTime1 : dateTime2;
    }

    /**
     * Returns the maximum of two dateTimes, treating null as {@link TimeFactory#getMinDateTime()}.
     *
     * @param dateTime1 the first dateTime.
     * @param dateTime2 the second dateTime.
     * @return the maximum of the two dateTimes.
     */
    public static LocalDateTime maxNullAsMin(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return NULL_AS_MIN_DATE_TIME_COMPARATOR.compare(dateTime1, dateTime2) >= 0 ? dateTime1 : dateTime2;
    }

    /**
     * Returns the minimum of two dateTimes, treating null as {@link TimeFactory#getMaxDateTime()}.
     *
     * @param dateTime1 the first dateTime.
     * @param dateTime2 the second dateTime.
     * @return the minimum of the two dateTimes.
     */
    public static LocalDateTime minNullAsMax(@Nullable LocalDateTime dateTime1, @Nullable LocalDateTime dateTime2) {
        return NULL_AS_MAX_DATE_TIME_COMPARATOR.compare(dateTime1, dateTime2) <= 0 ? dateTime1 : dateTime2;
    }

    /**
     * Adds the given amount of the accuracy configured in {@link TimeFactory#getAccuracy()} to the given dateTime.
     * <p>
     * For example if the default accuracy is <i>MILLISECONDS</i>, and the amount is 5, then 5 millis will be added to the given dateTime.
     *
     * @param dateTime the dateTime to add to.
     * @param amount   the amount to add.
     * @return the adjusted dateTime, or null if the given dateTime is null.
     */
    @Nullable
    public static LocalDateTime addDefault(@Nullable LocalDateTime dateTime, long amount) {
        return dateTime == null ? null : dateTime.plus(amount, TimeFactory.getAccuracy().toChronoUnit());
    }

    /**
     * Adds a single unit of the accuracy configured in {@link TimeFactory#getAccuracy()} to the given dateTime.
     *
     * @param dateTime the dateTime to add to.
     * @return the adjusted dateTime, or null if the given dateTime is null.
     */
    @Nullable
    public static LocalDateTime addSingle(@Nullable LocalDateTime dateTime) {
        return addDefault(dateTime, 1);
    }

    /**
     * Subtracts the given amount of the accuracy configured in {@link TimeFactory#getAccuracy()} from the given dateTime.
     * <p>
     * For example if the default accuracy is <i>MILLISECONDS</i>, and the amount is 5, then 5 millis will be subtracted from the given dateTime.
     *
     * @param dateTime the dateTime to subtract from.
     * @param amount   the amount to subtract.
     * @return the adjusted dateTime, or null if the given dateTime is null.
     */
    @Nullable
    public static LocalDateTime subtractDefault(@Nullable LocalDateTime dateTime, long amount) {
        return dateTime == null ? null : dateTime.minus(amount, TimeFactory.getAccuracy().toChronoUnit());
    }

    /**
     * Subtracts a single unit of the accuracy configured in {@link TimeFactory#getAccuracy()} from the given dateTime.
     *
     * @param dateTime the dateTime to subtract from.
     * @return the adjusted dateTime, or null if the given dateTime is null.
     */
    @Nullable
    public static LocalDateTime subtractSingle(@Nullable LocalDateTime dateTime) {
        return subtractDefault(dateTime, 1);
    }

    /**
     * Subtracts the given amount of the specified accuracy from the given dateTime.
     *
     * @param dateTime the dateTime to subtract from.
     * @param amount   the amount to subtract.
     * @param accuracy the accuracy to use for subtraction, overriding the default.
     * @return the adjusted dateTime, or null if the given dateTime is null.
     */
    @Nullable
    public static LocalDateTime subtractDefault(@Nullable LocalDateTime dateTime, long amount, TimeUnit accuracy) {
        return dateTime == null ? null : dateTime.minus(amount, accuracy.toChronoUnit());
    }

    /**
     * Subtracts a single unit of the specified accuracy from the given dateTime.
     *
     * @param dateTime the dateTime to subtract from.
     * @param accuracy the accuracy to use for subtraction, overriding the default.
     * @return the adjusted dateTime, or null if the given dateTime is null.
     */
    @Nullable
    public static LocalDateTime subtractSingle(@Nullable LocalDateTime dateTime, TimeUnit accuracy) {
        return subtractDefault(dateTime, 1, accuracy);
    }
}
