package com.thanlinardos.spring_enterprise_library.time.utils;

import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants.NULL_AS_MAX_INSTANT_COMPARATOR;
import static com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants.NULL_AS_MIN_INSTANT_COMPARATOR;

/**
 * Utility class for working with {@link Instant} objects.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstantUtils {

    /**
     * Parses an Instant string using the default parser in {@link Instant#parse(CharSequence)}.
     *
     * @param start the Instant string to parse.
     * @return the parsed Instant, or null if the input string is empty.
     */
    public static Instant parseInstant(String start) {
        return parseInstant(start, Instant::parse);
    }

    /**
     * Parses an Instant string using the provided parser function.
     *
     * @param date   the Instant string to parse.
     * @param parser the function to parse the Instant.
     * @return the parsed Instant, or null if the input string is empty.
     */
    public static Instant parseInstant(@Nullable String date, Function<String, Instant> parser) {
        return StringUtils.hasLength(date) ? parser.apply(date) : null;
    }

    /**
     * Converts a LocalDate to an Instant at the start of the day in the default time zone.
     *
     * @param date the LocalDate to convert.
     * @return the corresponding Instant, or null if the input date is null.
     */
    @Nullable
    public static Instant toStartOfDate(@Nullable LocalDate date) {
        return date == null ? null : date.atStartOfDay().toInstant(TimeFactory.getDefaultZone());
    }

    /**
     * Converts a LocalDate to an Instant at the start of the day in the specified ZoneOffset.
     *
     * @param date       the LocalDate to convert.
     * @param zoneOffset the ZoneOffset to use for conversion.
     * @return the corresponding Instant, or null if the input date is null.
     */
    @Nullable
    public static Instant toStartOfDate(@Nullable LocalDate date, ZoneOffset zoneOffset) {
        return date == null ? null : date.atStartOfDay().toInstant(zoneOffset);
    }

    /**
     * Converts a LocalDate to an Instant at the end of the given date's day
     * in the default time zone and represented with the default accuracy.
     * <p>
     * For example, if the default accuracy is MILLISECONDS,
     * then result will be: '{@code date}T23:59:59.999'.
     *
     * @param date the LocalDate to convert.
     * @return the corresponding Instant, or null if the input date is null.
     */
    @Nullable
    public static Instant toEndOfLocalDate(@Nullable LocalDate date) {
        return date == null ? null : subtractSingle(toStartOfDate(date.plusDays(1)));
    }

    /**
     * Converts a LocalDate to an Instant at the end of the day in the specified ZoneOffset and accuracy.
     * See {@link InstantUtils#toEndOfLocalDate(LocalDate)} for details.
     *
     * @param date       the LocalDate to convert.
     * @param accuracy   the TimeUnit accuracy to use to represent the resulting Instant.
     * @param zoneOffset the ZoneOffset to use for conversion.
     * @return the corresponding Instant, or null if the input date is null.
     */
    @Nullable
    public static Instant toEndOfLocalDate(@Nullable LocalDate date, TimeUnit accuracy, ZoneOffset zoneOffset) {
        return date == null ? null : subtractSingle(toStartOfDate(date.plusDays(1), zoneOffset), accuracy);
    }

    /**
     * Converts an Instant to a LocalDate in the default time zone.
     *
     * @param instant the Instant to convert.
     * @return the corresponding LocalDate, or null if the input Instant is null.
     */
    @Nullable
    public static LocalDate toLocalDate(@Nullable Instant instant) {
        return instant == null ? null : instant.atZone(TimeFactory.getDefaultZone()).toLocalDate();
    }

    /**
     * Checks if instant1 is before instant2, treating null as {@link TimeFactory#getMinInstant()}.
     *
     * @param instant1 The first date to compare.
     * @param instant2 The second date to compare.
     * @return true if instant1 is before instant2, false otherwise.
     */
    public static boolean isBeforeNullAsMin(@Nullable Instant instant1, @Nullable Instant instant2) {
        return instant1 == null || (instant2 != null && instant1.isBefore(instant2));
    }

    /**
     * Checks if instant1 is after instant2, treating null as {@link TimeFactory#getMaxInstant()}.
     *
     * @param instant1 The first date to compare.
     * @param instant2 The second date to compare.
     * @return true if instant1 is after instant2, false otherwise.
     */
    public static boolean isAfterNullAsMax(@Nullable Instant instant1, @Nullable Instant instant2) {
        return instant1 == null || (instant2 != null && instant1.isAfter(instant2));
    }

    /**
     * Checks if instant1 is before or equal to instant2, treating null as {@link TimeFactory#getMaxInstant()}.
     *
     * @param instant1 The first date to compare.
     * @param instant2 The second date to compare.
     * @return true if instant1 is before or equal to instant2, false otherwise.
     */
    public static boolean isBeforeOrEqual(@Nullable Instant instant1, @Nullable Instant instant2) {
        return Objects.equals(instant1, instant2) || isBeforeNullAsMin(instant1, instant2);
    }

    /**
     * Checks if instant1 is after or equal to instant2, treating null as {@link TimeFactory#getMinInstant()}.
     *
     * @param instant1 The first date to compare.
     * @param instant2 The second date to compare.
     * @return true if instant1 is after or equal to instant2, false otherwise.
     */
    public static boolean isAfterOrEqual(@Nullable Instant instant1, @Nullable Instant instant2) {
        return Objects.equals(instant1, instant2) || isAfterNullAsMax(instant1, instant2);
    }

    /**
     * Returns the maximum of two Instants, treating null as {@link TimeFactory#getMaxInstant()}.
     *
     * @param instant1 the first instant.
     * @param instant2 the second instant.
     * @return the maximum of the two instants.
     */
    public static Instant maxNullAsMax(@Nullable Instant instant1, @Nullable Instant instant2) {
        return NULL_AS_MAX_INSTANT_COMPARATOR.compare(instant1, instant2) >= 0 ? instant1 : instant2;
    }

    /**
     * Returns the minimum of two Instants, treating null as {@link TimeFactory#getMinInstant()}.
     *
     * @param instant1 the first instant.
     * @param instant2 the second instant.
     * @return the minimum of the two instants.
     */
    public static Instant minNullAsMin(@Nullable Instant instant1, @Nullable Instant instant2) {
        return NULL_AS_MIN_INSTANT_COMPARATOR.compare(instant1, instant2) <= 0 ? instant1 : instant2;
    }

    /**
     * Returns the maximum of two Instants, treating null as {@link TimeFactory#getMaxInstant()}.
     *
     * @param instant1 the first instant.
     * @param instant2 the second instant.
     * @return the maximum of the two instants.
     */
    public static Instant maxNullAsMin(@Nullable Instant instant1, @Nullable Instant instant2) {
        return NULL_AS_MIN_INSTANT_COMPARATOR.compare(instant1, instant2) >= 0 ? instant1 : instant2;
    }

    /**
     * Returns the minimum of two Instants, treating null as {@link TimeFactory#getMaxInstant()}.
     *
     * @param instant1 the first instant.
     * @param instant2 the second instant.
     * @return the minimum of the two instants.
     */
    public static Instant minNullAsMax(@Nullable Instant instant1, @Nullable Instant instant2) {
        return NULL_AS_MAX_INSTANT_COMPARATOR.compare(instant1, instant2) <= 0 ? instant1 : instant2;
    }

    /**
     * Adds the given amount of the default accuracy to the given instant.
     *
     * @param instant the instant to add to.
     * @param amount  the amount to add.
     * @return the resulting instant, or null if the input instant is null.
     */
    @Nullable
    public static Instant addDefault(@Nullable Instant instant, long amount) {
        return instant == null ? null : instant.plus(amount, TimeFactory.getAccuracy().toChronoUnit());
    }

    /**
     * Adds a single unit of the default accuracy to the given instant.
     *
     * @param instant the instant to add to.
     * @return the resulting instant, or null if the input instant is null.
     */
    @Nullable
    public static Instant addSingle(@Nullable Instant instant) {
        return addDefault(instant, 1);
    }

    /**
     * Adds the given amount of the default accuracy to the given instant.
     *
     * @param instant the instant to add to.
     * @param amount  the amount to add.
     * @return the resulting instant, or null if the input instant is null.
     */
    @Nullable
    public static Instant subtractDefault(@Nullable Instant instant, long amount) {
        return instant == null ? null : instant.minus(amount, TimeFactory.getAccuracy().toChronoUnit());
    }

    /**
     * Subtracts a single unit of the default accuracy from the given instant.
     *
     * @param instant the instant to subtract from.
     * @return the resulting instant, or null if the input instant is null.
     */
    @Nullable
    public static Instant subtractSingle(@Nullable Instant instant) {
        return subtractDefault(instant, 1);
    }

    /**
     * Subtracts the given amount of the specified accuracy from the given instant.
     *
     * @param temporal the instant to subtract from.
     * @param amount   the amount to subtract.
     * @param accuracy the TimeUnit accuracy to use for the subtraction.
     * @return the resulting instant, or null if the input instant is null.
     */
    @Nullable
    public static Instant subtractDefault(@Nullable Instant temporal, long amount, TimeUnit accuracy) {
        return temporal == null ? null : temporal.minus(amount, accuracy.toChronoUnit());
    }

    /**
     * Subtracts a single unit of the specified accuracy from the given instant.
     *
     * @param temporal the instant to subtract from.
     * @param accuracy the TimeUnit accuracy to use for the subtraction.
     * @return the resulting instant, or null if the input instant is null.
     */
    @Nullable
    public static Instant subtractSingle(@Nullable Instant temporal, TimeUnit accuracy) {
        return subtractDefault(temporal, 1, accuracy);
    }
}
