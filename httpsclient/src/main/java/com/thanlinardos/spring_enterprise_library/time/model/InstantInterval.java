package com.thanlinardos.spring_enterprise_library.time.model;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.api.InstantTemporal;
import com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants;
import com.thanlinardos.spring_enterprise_library.time.utils.DateUtils;
import com.thanlinardos.spring_enterprise_library.time.utils.InstantUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.thanlinardos.spring_enterprise_library.objects.utils.ObjectUtils.applyIfBothNotNull;
import static com.thanlinardos.spring_enterprise_library.time.utils.DateUtils.parseLocalDate;
import static com.thanlinardos.spring_enterprise_library.objects.utils.ObjectUtils.isAllObjectsNotNullAndEquals;
import static com.thanlinardos.spring_enterprise_library.time.utils.InstantUtils.addSingle;
import static com.thanlinardos.spring_enterprise_library.time.utils.InstantUtils.parseInstant;
import static com.thanlinardos.spring_enterprise_library.time.utils.InstantUtils.subtractSingle;
import static com.thanlinardos.spring_enterprise_library.time.utils.InstantUtils.toEndOfLocalDate;
import static com.thanlinardos.spring_enterprise_library.time.utils.InstantUtils.toStartOfDate;

/**
 * Represents a time InstantInterval with a start and end date.
 * Provides utility methods for creating, manipulating, and analyzing Intervals.
 *
 * @param start the start date of the InstantInterval (nullable).
 * @param end   the end date of the InstantInterval (nullable).
 */
public record InstantInterval(@Nullable Instant start, @Nullable Instant end) implements Comparable<InstantInterval>, InstantTemporal {

    /**
     * Constructs an InstantInterval with the given start and end dates.
     *
     * @param start the start date of the InstantInterval (nullable)
     * @param end   the end date of the InstantInterval (nullable)
     * @throws IllegalArgumentException if the start date is after the end date
     */
    public InstantInterval {
        if (isNotValid(start, end)) {
            throw ErrorCode.ILLEGAL_ARGUMENT.createCoreException("Found start date {0} to be after end date {1} when constructing Interval.", new Object[]{start, end});
        }
    }

    /**
     * Constructs an {@link InstantInterval} for the given {@link LocalDate}, where the interval will span the whole given day.
     *
     * @param date the {@link LocalDate} to create the InstantInterval for.
     */
    public InstantInterval(LocalDate date) {
        this(toStartOfDate(date), toEndOfLocalDate(date));
    }

    /**
     * Constructs an {@link InstantInterval} for the given start and end {@link LocalDate}s.
     *
     * @param startDate the start {@link LocalDate} to create the InstantInterval for.
     * @param endDate   the end {@link LocalDate} to create the InstantInterval for.
     */
    public InstantInterval(LocalDate startDate, LocalDate endDate) {
        this(toStartOfDate(startDate), toEndOfLocalDate(endDate));
    }

    /**
     * Constructs an {@link InstantInterval} for the given start and end {@link LocalDate}s.
     *
     * @param startDate  the start {@link LocalDate} to create the InstantInterval for.
     * @param endDate    the end {@link LocalDate} to create the InstantInterval for.
     * @param accuracy   the accuracy to use when converting LocalDate to Instant.
     * @param zoneOffset the zoneOffset to use when converting LocalDate to Instant.
     */
    public InstantInterval(LocalDate startDate, LocalDate endDate, TimeUnit accuracy, ZoneOffset zoneOffset) {
        this(toStartOfDate(startDate, zoneOffset), toEndOfLocalDate(endDate, accuracy, zoneOffset));
    }

    /**
     * Constructs an {@link InstantInterval} for the given {@link Interval}.
     *
     * @param interval the {@link Interval} to create the InstantInterval for.
     */
    public InstantInterval(Interval interval) {
        this(interval.start(), interval.end());
    }

    /**
     * Constructs an {@link InstantInterval} for the given {@link YearMonth}.
     *
     * @param yearMonth the {@link YearMonth} to create the InstantInterval for.
     */
    public InstantInterval(YearMonth yearMonth) {
        this(toStartOfDate(yearMonth.atDay(1)), toStartOfDate(yearMonth.atEndOfMonth()));
    }

    /**
     * Constructs an {@link InstantInterval} for the given {@link Year}.
     *
     * @param year the {@link Year} to create the InstantInterval for.
     */
    public InstantInterval(Year year) {
        this(toStartOfDate(year.atDay(1)), toStartOfDate(DateUtils.getLastDayOfYear(year)));
    }

    /**
     * Returns this InstantInterval.
     *
     * @return this InstantInterval.
     */
    @Override
    public InstantInterval getInterval() {
        return this;
    }

    /**
     * Validates whether the start Instant is before or equal to the end Instant.
     *
     * @param start the start Instant
     * @param end   the end Instant
     * @return true if the start Instant is after the end Instant, false otherwise.
     */
    public static boolean isNotValid(@Nullable Instant start, @Nullable Instant end) {
        return applyIfBothNotNull(start, end, Instant::isAfter).orElse(false);
    }

    /**
     * Validates whether the start Instant is before or equal to the end Instant.
     *
     * @param start the start Instant.
     * @param end   the end Instant.
     * @return true if the start Instant is before or equal to the end Instant, false otherwise.
     */
    public static boolean isValid(@Nullable Instant start, @Nullable Instant end) {
        return !isNotValid(start, end);
    }

    /**
     * Constructs an {@link InstantInterval} using ISO 8601 formatted Instants (such as "2007-12-03T10:15:30.00Z").
     * The format is further specified in {@link DateTimeFormatter#ISO_INSTANT}.
     *
     * @param start start of the InstantInterval (inclusive).
     * @param end   end of the InstantInterval (inclusive).
     * @return an {@link InstantInterval} based on the given Instants.
     */
    public static InstantInterval forIsoInstants(@Nullable String start, @Nullable String end) {
        return new InstantInterval(parseInstant(start), parseInstant(end));
    }

    /**
     * Constructs an {@link InstantInterval} using ISO 8601 formatted dates (such as "2007-12-03").
     * The format is further specified in {@link DateTimeFormatter#ISO_LOCAL_DATE}.
     *
     * @param startDate the start date (inclusive).
     * @param endDate   the end date (inclusive).
     * @return an {@link InstantInterval} based on the given dates.
     */
    public static InstantInterval forIsoDates(@Nullable String startDate, @Nullable String endDate) {
        return new InstantInterval(parseLocalDate(startDate), parseLocalDate(endDate));
    }

    /**
     * Constructs an {@link InstantInterval} using ISO 8601 formatted dates (such as "2007-12-03").
     * The format is further specified in {@link DateTimeFormatter#ISO_LOCAL_DATE}.
     * The given accuracy and zoneOffset parameters are used to override the defaults from {@link TimeFactory}.
     *
     * @param startDate  the start date (inclusive).
     * @param endDate    the end date (inclusive).
     * @param accuracy   the accuracy to use when converting LocalDate to Instant.
     * @param zoneOffset the zoneOffset to use when converting LocalDate to Instant.
     * @return an {@link InstantInterval} based on the given dates.
     */
    public static InstantInterval forIsoDates(@Nullable String startDate, @Nullable String endDate, TimeUnit accuracy, ZoneOffset zoneOffset) {
        return new InstantInterval(parseLocalDate(startDate), parseLocalDate(endDate), accuracy, zoneOffset);
    }

    /**
     * Constructs an {@link InstantInterval} same as in {@link InstantInterval#forIsoDates(String, String, TimeUnit, ZoneOffset)},
     * but using milliseconds accuracy and UTC zoneOffset.
     *
     * @param startDate the start date (inclusive).
     * @param endDate   the end date (inclusive).
     * @return an {@link InstantInterval} based on the given dates.
     */
    public static InstantInterval forIsoDatesMilliUTC(@Nullable String startDate, @Nullable String endDate) {
        return forIsoDates(startDate, endDate, TimeUnit.MILLISECONDS, ZoneOffset.UTC);
    }

    /**
     * Constructs an {@link InstantInterval} same as in {@link InstantInterval#forIsoDates(String, String, TimeUnit, ZoneOffset)},
     * but using milliseconds accuracy and UTC zoneOffset, and with a null end date.
     *
     * @param startDate the start date (inclusive).
     * @return an open-ended {@link InstantInterval} based on the given start date.
     */
    public static InstantInterval fromIsoDateToNullMilliUTC(@Nullable String startDate) {
        return forIsoDates(startDate, null, TimeUnit.MILLISECONDS, ZoneOffset.UTC);
    }

    /**
     * Constructs an {@link InstantInterval} using a ISO 8601 formatted month ("YYYY-MM").
     *
     * @param month a string representing the month (e.g. "2025-07")
     * @return an {@link InstantInterval} based on the given month
     */
    public static InstantInterval forIsoMonth(@Nonnull String month) {
        return new InstantInterval(YearMonth.parse(month));
    }

    /**
     * Constructs an {@link InstantInterval} using a year.
     *
     * @param year an int representing the year (e.g. 2025)
     * @return an {@link InstantInterval} based on the given year
     */
    public static InstantInterval forIsoYear(int year) {
        return new InstantInterval(Year.of(year));
    }

    /**
     * Returns the start Instant, or the minimum Instant if the start is null.
     *
     * @return the start Instant or MIN_INSTANT
     */
    public Instant getStartNullAsMin() {
        return hasNullStart() ? TimeFactory.getMinInstant() : start;
    }

    /**
     * Returns the end Instant, or the maximum Instant if the end is null.
     *
     * @return the end Instant or MAX_INSTANT
     */
    public Instant getEndNullAsMax() {
        return hasNullEnd() ? TimeFactory.getMaxInstant() : end;
    }

    /**
     * Returns a sorted list of intervals, covering the same Instants as the input, but without any intervals overlapping.
     * <pre>
     * Input:
     * [-----------]    [-------------][------][----------------]    [---]
     *        [---------------]                    [---][---]
     * Output:
     * [-----][----][--][-----][------][------][--][---][---][--]    [---]
     * </pre>
     *
     * @param intervals collection of intervals to normalize
     * @return split list of intervals
     */
    public static List<InstantInterval> split(Collection<InstantInterval> intervals) {
        if (intervals.size() <= 1) {
            return new ArrayList<>(intervals);
        }

        // generate start instants from end instants, and vice versa, selecting only those that overlap with the given intervals, with start/end-specific sorting
        List<Instant> startInstants = getSortedDatesWithFunctionsPredicateAndComparator(intervals, InstantInterval::start, i -> addSingle(i.end()),
                TimeConstants.NULL_AS_MIN_INSTANT_COMPARATOR, InstantInterval::containsNullAsMin);
        List<Instant> endInstants = getSortedDatesWithFunctionsPredicateAndComparator(intervals, InstantInterval::end, i -> subtractSingle(i.start()),
                TimeConstants.NULL_AS_MAX_INSTANT_COMPARATOR, InstantInterval::containsNullAsMax);

        if (startInstants.size() != endInstants.size()) {
            throw new IllegalStateException(String.format("Unable to split collection of intervals: %s", intervals));
        }

        List<InstantInterval> splittedIntervals = new ArrayList<>();
        for (int i = 0; i < startInstants.size(); i++) {
            splittedIntervals.add(new InstantInterval(startInstants.get(i), endInstants.get(i)));
        }
        return splittedIntervals;
    }

    private static List<Instant> getSortedDatesWithFunctionsPredicateAndComparator(Collection<InstantInterval> intervals,
                                                                                   Function<InstantInterval, Instant> keepNull,
                                                                                   Function<InstantInterval, Instant> discardNull,
                                                                                   Comparator<Instant> comparator,
                                                                                   BiPredicate<InstantInterval, Instant> predicate) {
        return intervals.stream()
                .flatMap(interval -> Stream.concat(
                        Stream.of(keepNull.apply(interval)),
                        Stream.ofNullable(discardNull.apply(interval))))
                .filter(date -> isAnyMatchForPredicateOnDate(intervals, predicate, date))
                .distinct()
                .sorted(comparator)
                .toList();
    }

    private static boolean isAnyMatchForPredicateOnDate(Collection<InstantInterval> intervals, BiPredicate<InstantInterval, Instant> predicate, Instant instant) {
        return intervals.stream()
                .anyMatch(interval -> predicate.test(interval, instant));
    }

    private boolean hasNullStart() {
        return start == null;
    }

    private boolean hasNullEnd() {
        return end == null;
    }

    /**
     * Checks if the given instant is contained within this InstantInterval, treating null as MIN_INSTANT.
     *
     * @param instant the instant to check
     * @return true if the instant is contained, false otherwise
     */
    public boolean containsNullAsMin(@Nullable Instant instant) {
        Instant nonNullInstant = instant == null ? TimeFactory.getMinInstant() : instant;
        return (hasNullStart() || !nonNullInstant.isBefore(start)) && (hasNullEnd() || !nonNullInstant.isAfter(end));
    }

    /**
     * Checks if the given instant is contained within this InstantInterval, treating null as MAX_INSTANT.
     *
     * @param instant the instant to check
     * @return true if the instant is contained, false otherwise
     */
    public boolean containsNullAsMax(@Nullable Instant instant) {
        Instant nonNullInstant = instant == null ? TimeFactory.getMaxInstant() : instant;
        return (hasNullStart() || !nonNullInstant.isBefore(start)) && (hasNullEnd() || !nonNullInstant.isAfter(end));
    }

    /**
     * Checks if the given instant is contained within this InstantInterval.
     *
     * @param instant the instant to check.
     * @return true if the instant is contained, false otherwise.
     */
    public boolean contains(@Nonnull Instant instant) {
        return (hasNullStart() || !instant.isBefore(start)) && (hasNullEnd() || !instant.isAfter(end));
    }

    /**
     * Checks if the given {@link LocalDate} is fully contained within this {@link InstantInterval}.
     *
     * @param date a given {@link LocalDate}.
     * @return true if this {@link InstantInterval} fully contains the given {@link LocalDate}, otherwise false.
     */
    public boolean contains(@Nonnull LocalDate date) {
        return contains(new InstantInterval(date));
    }

    /**
     * Checks if the given {@link YearMonth} is fully contained within this {@link InstantInterval}.
     *
     * @param yearMonth a given {@link YearMonth}.
     * @return true if this {@link InstantInterval} fully contains the given {@link YearMonth}, otherwise false.
     */
    public boolean contains(@Nonnull YearMonth yearMonth) {
        return contains(new InstantInterval(yearMonth));
    }

    /**
     * Checks if the given {@link Year} is fully contained within this {@link InstantInterval}.
     *
     * @param year a given {@link Year}.
     * @return true if this {@link InstantInterval} fully contains the given {@link Year}, otherwise false.
     */
    public boolean contains(@Nonnull Year year) {
        return contains(new InstantInterval(year));
    }

    /**
     * Checks if the given {@link InstantInterval} is fully contained within this {@link InstantInterval}.
     *
     * @param interval a given {@link InstantInterval}.
     * @return true if this {@link InstantInterval} fully contains the given {@link InstantInterval}, otherwise false.
     */
    public boolean contains(@Nonnull InstantInterval interval) {
        return (hasNullStart() || (interval.hasNullStart() || !InstantUtils.isBeforeNullAsMin(interval.start(), start())))
                && (hasNullEnd() || (interval.hasNullEnd() || !InstantUtils.isAfterNullAsMax(interval.end(), end())));
    }

    /**
     * Checks if this {@link InstantInterval} overlaps the given interval.
     *
     * @param interval a given {@link InstantInterval}
     * @return true if this {@link InstantInterval} overlaps the given interval, otherwise false
     */
    public boolean overlaps(@Nonnull InstantInterval interval) {
        return (hasNullStart() || !InstantUtils.isAfterNullAsMax(start(), interval.end()))
                && (hasNullEnd() || !InstantUtils.isBeforeNullAsMin(end(), interval.start()));
    }

    /**
     * Returns true if this {@link InstantInterval} overlaps with any of the given intervals, otherwise false.
     *
     * @param intervals a collection of {@link InstantInterval}
     * @return true if this {@link InstantInterval} overlaps with any of the given intervals, otherwise false
     */
    public boolean overlaps(Collection<InstantInterval> intervals) {
        return intervals.stream()
                .anyMatch(this::overlaps);
    }

    /**
     * Returns true if this {@link InstantInterval} overlaps with the given yearMonth, otherwise false.
     *
     * @param yearMonth a {@link YearMonth}
     * @return true if this {@link InstantInterval} overlaps with the given yearMonth, otherwise false
     */
    public boolean overlaps(@Nonnull YearMonth yearMonth) {
        return overlaps(new InstantInterval(yearMonth));
    }

    /**
     * Returns true if this {@link InstantInterval} overlaps with the given year, otherwise false.
     *
     * @param year a {@link Year}
     * @return true if this {@link InstantInterval} overlaps with the given year, otherwise false.
     */
    public boolean overlaps(@Nonnull Year year) {
        return overlaps(new InstantInterval(year));
    }

    /**
     * Returns true if any of the given intervals overlap with each other, otherwise false
     *
     * @param intervals a list of intervals
     * @return true if any of the given intervals overlap with each other
     */
    public static boolean anyOverlaps(Collection<InstantInterval> intervals) {
        return intervals.stream()
                .anyMatch(interval -> interval.overlaps(interval.relativeComplement(intervals)));
    }

    /**
     * Returns the list of the given intervals, excluding any elements equal to this InstantInterval
     *
     * @param intervals a list of intervals
     * @return the result of removing any intervals equal to this InstantInterval
     */
    public List<InstantInterval> relativeComplement(Collection<InstantInterval> intervals) {
        return intervals.stream()
                .filter(Predicate.not(this::equals))
                .toList();
    }

    /**
     * Checks if this {@link InstantInterval} partially overlaps the given interval, meaning that they overlap but neither contains the other.
     *
     * @param interval a given {@link InstantInterval}
     * @return true if this {@link InstantInterval} overlaps the given interval and neither interval contains the other, otherwise false
     */
    public boolean partiallyOverlaps(@Nonnull InstantInterval interval) {
        return this.overlaps(interval) && !this.contains(interval) && !interval.contains(this);
    }

    /**
     * Returns the overlap between this interval and the given interval, otherwise an empty Optional if there is no overlap
     * <pre>
     * This interval:
     * |--------------------------------------|
     * Input:
     *        |---------------|
     * Output:
     *        |---------------|
     * </pre>
     *
     * @param interval a given {@link InstantInterval}
     * @return the overlap between this interval and the given interval, otherwise an empty Optional if there is no overlap
     */
    public Optional<InstantInterval> getOverlap(@Nonnull InstantInterval interval) {
        if (overlaps(interval)) {
            return Optional.of(new InstantInterval(
                    InstantUtils.maxNullAsMin(this.start(), interval.start()),
                    InstantUtils.minNullAsMax(this.end(), interval.end())
            ));
        }
        return Optional.empty();
    }

    /**
     * Returns a new version of this InstantInterval, where the end date is bounded according to the given {@code upperBound}.
     * Equivalent to calling {@link InstantInterval#getOverlap(InstantInterval)} with an InstantInterval starting from null and ending in {@code upperBound}.
     *
     * @param upperBound an InstantInterval defining the bounds to apply to this InstantInterval
     * @return a new version of this InstantInterval, bounded according to given {@code upperBound}, or an empty Optional if the bounding results in an invalid InstantInterval
     */
    public Optional<InstantInterval> boundEnd(@Nullable Instant upperBound) {
        return getOverlap(new InstantInterval(null, upperBound));
    }

    /**
     * Returns a new version of this InstantInterval, where the start date is bounded according to the given {@code lowerBound}
     * Equivalent to calling {@link InstantInterval#getOverlap(InstantInterval)} with an InstantInterval starting from {@code lowerBound} and ending in null.
     *
     * @param lowerBound a date defining the lower bound to apply to this InstantInterval
     * @return a new version of this InstantInterval, bounded according to given {@code lowerBound}, or an empty Optional if the bounding results in an invalid InstantInterval
     */
    public Optional<InstantInterval> boundStart(@Nullable Instant lowerBound) {
        return getOverlap(new InstantInterval(lowerBound, null));
    }

    /**
     * Returns a new version of this InstantInterval, where the start date is bounded according to {@link InstantInterval#boundStart(Instant)}.
     * If the bounding results in an invalid InstantInterval, the original InstantInterval is returned.
     *
     * @param lowerBound a date defining the lower bound to apply to this InstantInterval.
     * @return a new version of this InstantInterval, bounded according to given {@code lowerBound}, or the original InstantInterval if the bounding results in an invalid InstantInterval.
     */
    public InstantInterval boundStartDateIfValid(@Nullable Instant lowerBound) {
        return boundStart(lowerBound)
                .orElse(this);
    }

    /**
     * Returns a normalized (see {@link InstantInterval#normalize(Collection)}) list of overlaps between the given intervals and this InstantInterval
     * <pre>
     * This InstantInterval:
     * |--------------------------------|
     * Input:
     *        |---------|           |------|
     *             |-------||--|
     * Output:
     *        |----------------|    |---|
     * </pre>
     *
     * @param intervals intervals to determine overlaps for
     * @return a normalized list of overlaps between the given intervals and this InstantInterval
     */
    public List<InstantInterval> getOverlaps(Collection<InstantInterval> intervals) {
        return getOverlaps(intervals, true);
    }

    /**
     * Vararg variant of {@link InstantInterval#getOverlaps(Collection)}
     *
     * @param intervals intervals to determine overlaps for
     * @return a normalized list of overlaps between the given intervals and this InstantInterval
     */
    public List<InstantInterval> getOverlaps(InstantInterval... intervals) {
        return getOverlaps(List.of(intervals));
    }

    /**
     * Returns a normalized (see {@link InstantInterval#normalize(Collection)}) list of overlaps between the given intervals and this InstantInterval
     * Whether to merge adjacent overlaps is subject to the value of {@code mergeAdjacentIntervals}
     * <pre>
     * This InstantInterval:
     * |--------------------------------|
     * Input:
     *        |---------|           |------|
     *             |-------||--|
     * Output:
     *        |----------------|    |---|      (if mergeAdjacentIntervals = true)
     * or
     *        |------------||--|    |---|      (if mergeAdjacentIntervals = false)
     * </pre>
     *
     * @param intervals              intervals to determine overlaps for
     * @param mergeAdjacentIntervals whether to merge adjacent overlaps
     * @return a normalized list of overlaps between the given intervals and this InstantInterval
     */
    public List<InstantInterval> getOverlaps(Collection<InstantInterval> intervals, boolean mergeAdjacentIntervals) {
        List<InstantInterval> overlaps = intervals.stream()
                .map(this::getOverlap)
                .flatMap(Optional::stream)
                .toList();
        return normalize(overlaps, mergeAdjacentIntervals);
    }

    /**
     * Returns a normalized list of the portions of {@code intervals} that do not overlap with this InstantInterval
     * <pre>
     * This InstantInterval:
     *                |---------------|
     * Input:
     *        |---------|         |------|
     *             |---------|
     * Output:
     *        |------|                 |-|
     * </pre>
     *
     * @param intervals list of intervals
     * @return the portions of {@code intervals} that do not overlap with this InstantInterval
     */
    public List<InstantInterval> getNotOverlaps(Collection<InstantInterval> intervals) {
        List<InstantInterval> overlaps = this.getOverlaps(intervals);
        List<InstantInterval> notOverlaps = intervals.stream()
                .map(interval -> interval.subtract(overlaps))
                .flatMap(Collection::stream)
                .toList();
        return normalize(notOverlaps);
    }

    /**
     * Vararg variant of {@link InstantInterval#getNotOverlaps(Collection)}
     *
     * @param intervals given intervals
     * @return the portions of {@code intervals} that do not overlap with this InstantInterval
     */
    public List<InstantInterval> getNotOverlaps(InstantInterval... intervals) {
        return getNotOverlaps(List.of(intervals));
    }

    /**
     * Returns a sorted list of intervals, covering the same days as the input, but without any intervals overlapping.
     * intervals that start just after the previous one ends will also be merged.
     * <pre>
     * Input:
     * [-----------]    [-------------][------][----------------]    [---]
     *        [---------------]                    [---][---]
     * Output:
     * [--------------------------------------------------------]    [---]
     * </pre>
     *
     * @param intervals collection of intervals to normalize
     * @return normalized list of intervals
     */
    public static List<InstantInterval> normalize(Collection<InstantInterval> intervals) {
        return normalize(intervals, true);
    }

    /**
     * Returns a sorted list of intervals, covering the same days as the input, but without any intervals overlapping.
     * <pre>
     * Input:
     * [-----------]    [-------------][------][----------------]    [---]
     *        [---------------]                    [---][---]
     * Output:
     * [--------------------------------------------------------]    [---]   (if mergeAdjacentIntervals = true)
     * or
     * [------------------------------][------][----------------]    [---]   (if mergeAdjacentIntervals = false)
     * </pre>
     *
     * @param intervals              collection of intervals to normalize
     * @param mergeAdjacentIntervals whether to merge adjacent intervals
     * @return normalized list of intervals
     */
    public static List<InstantInterval> normalize(Collection<InstantInterval> intervals, boolean mergeAdjacentIntervals) {
        if (intervals.size() <= 1) {
            return new ArrayList<>(intervals);
        }

        List<InstantInterval> sortedIntervals = sort(intervals);
        List<InstantInterval> result = new ArrayList<>();
        InstantInterval current = sortedIntervals.getFirst();

        for (InstantInterval interval : sortedIntervals) {
            if (shouldMergeCurrentInstantIntervalWithNextInstantInterval(current, interval, mergeAdjacentIntervals)) {
                current = new InstantInterval(current.start(), InstantUtils.maxNullAsMax(current.end(), interval.end()));
            } else {
                result.add(current);
                current = interval;
            }
        }
        result.add(current);

        return result.stream()
                .distinct()
                .toList();
    }

    private static boolean shouldMergeCurrentInstantIntervalWithNextInstantInterval(InstantInterval current, InstantInterval next, boolean mergeAdjacentIntervals) {
        Instant adjustedEnd = mergeAdjacentIntervals ? addSingle(current.end()) : current.end();
        return !InstantUtils.isAfterNullAsMax(next.start(), adjustedEnd);
    }

    private static List<InstantInterval> sort(Collection<InstantInterval> intervals) {
        return intervals.stream()
                .sorted()
                .toList();
    }

    /**
     * Checks if the given {@link InstantInterval} is touching this interval.
     *
     * @param interval a given {@link InstantInterval}
     * @return true if this interval ends on the day before the given interval starts or starts on the day after the given interval ends, otherwise false.
     */
    public boolean adjacent(@Nonnull InstantInterval interval) {
        return isAllObjectsNotNullAndEquals(subtractSingle(start()), interval.end())
                || isAllObjectsNotNullAndEquals(end(), subtractSingle(interval.start()));
    }

    /**
     * Returns the remainder of this interval, after subtracting the overlap with the given interval.
     * <pre>
     * This:
     * |---------------------------|
     * Input:
     *        |----|
     * Output:
     * |-----|      |--------------|
     * </pre>
     *
     * @param interval a given interval to subtract with
     * @return list of Intervals after subtracting the given {@code interval} from this {@link InstantInterval}
     */
    public List<InstantInterval> subtract(InstantInterval interval) {
        return this.subtract(List.of(interval));
    }

    /**
     * Returns the remainder of this InstantInterval, after subtracting the overlap with the given intervals.
     * <pre>
     * This:
     * |---------------------------|
     * Input:
     *        |----|    |---|
     * Output:
     * |-----|      |--|     |-----|
     * </pre>
     *
     * @param intervals intervals to subtract with
     * @return list of intervals after subtracting the given {@code intervals} from this {@link InstantInterval}
     */
    public List<InstantInterval> subtract(Collection<InstantInterval> intervals) {
        @Nullable Instant nextPossibleStart = this.start();
        boolean endCrossed = false;
        List<InstantInterval> resultIntervals = new ArrayList<>();
        for (InstantInterval overlap : this.getOverlaps(intervals)) {
            if (InstantUtils.isBeforeNullAsMin(nextPossibleStart, overlap.start())) {
                resultIntervals.add(new InstantInterval(
                        nextPossibleStart,
                        InstantUtils.minNullAsMax(this.end(), subtractSingle(overlap.start())))
                );
            }
            if (InstantUtils.isAfterOrEqual(overlap.end(), this.end())) {
                endCrossed = true;
                break;
            }
            if (InstantUtils.isBeforeOrEqual(nextPossibleStart, overlap.end())) {
                nextPossibleStart = addSingle(overlap.end());
            }
        }
        if (!endCrossed) {
            resultIntervals.add(new InstantInterval(nextPossibleStart, end()));
        }

        return resultIntervals;
    }

    @Override
    public int compareTo(@Nonnull InstantInterval o) {
        int startComparisonResult = TimeConstants.NULL_AS_MIN_INSTANT_COMPARATOR.compare(start(), o.start());
        if (startComparisonResult == 0) {
            return TimeConstants.NULL_AS_MAX_INSTANT_COMPARATOR.compare(end(), o.end());
        } else {
            return startComparisonResult;
        }
    }
}