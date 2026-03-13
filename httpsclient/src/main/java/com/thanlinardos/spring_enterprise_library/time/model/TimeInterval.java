package com.thanlinardos.spring_enterprise_library.time.model;

import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.api.TimeTemporal;
import com.thanlinardos.spring_enterprise_library.time.constants.TimeConstants;
import com.thanlinardos.spring_enterprise_library.time.utils.DateUtils;
import com.thanlinardos.spring_enterprise_library.time.utils.DateTimeUtils;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
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

import static com.thanlinardos.spring_enterprise_library.time.utils.DateTimeUtils.addSingle;
import static com.thanlinardos.spring_enterprise_library.time.utils.DateTimeUtils.parseDateTime;
import static com.thanlinardos.spring_enterprise_library.time.utils.DateTimeUtils.subtractSingle;
import static com.thanlinardos.spring_enterprise_library.time.utils.DateTimeUtils.toEndOfDate;
import static com.thanlinardos.spring_enterprise_library.time.utils.DateTimeUtils.toStartOfDate;
import static com.thanlinardos.spring_enterprise_library.time.utils.DateUtils.parseLocalDate;
import static com.thanlinardos.spring_enterprise_library.objects.utils.ObjectUtils.isAllObjectsNotNullAndEquals;

/**
 * Represents a time interval with a start and end date.
 * Provides utility methods for creating, manipulating, and analyzing Intervals.
 *
 * @param start the start of the interval.
 * @param end   the end of the interval.
 */
public record TimeInterval(@Nullable LocalDateTime start,
                           @Nullable LocalDateTime end) implements Comparable<TimeInterval>, TimeTemporal {

    /**
     * Constructs an interval with the given start and end dates.
     *
     * @param start the start date of the interval (nullable)
     * @param end   the end date of the interval (nullable)
     * @throws IllegalArgumentException if the start date is after the end date
     */
    public TimeInterval {
        if (isNotValid(start, end)) {
            throw ErrorCode.ILLEGAL_ARGUMENT.createCoreException("Found start date {0} to be after end date {1} when constructing Interval.", new Object[]{start, end});
        }
    }

    /**
     * Constructs an {@link TimeInterval} for the given {@link LocalDate}, where the interval will span the whole given day.
     *
     * @param date the {@link LocalDate} to create the interval for.
     */
    public TimeInterval(LocalDate date) {
        this(toStartOfDate(date), toEndOfDate(date));
    }

    /**
     * Constructs an {@link TimeInterval} for the given start and end {@link LocalDate}s.
     *
     * @param startDate the start {@link LocalDate} to create the interval for.
     * @param endDate   the end {@link LocalDate} to create the interval for.
     */
    public TimeInterval(LocalDate startDate, LocalDate endDate) {
        this(toStartOfDate(startDate), toEndOfDate(endDate));
    }

    /**
     * Constructs an {@link TimeInterval} for the given start and end {@link LocalDate}s
     * and with the given accuracy.
     *
     * @param startDate the start {@link LocalDate} to create the interval for.
     * @param endDate   the end {@link LocalDate} to create the interval for.
     * @param accuracy  the {@link TimeUnit} accuracy to represent the date-time interval with.
     */
    public TimeInterval(LocalDate startDate, LocalDate endDate, TimeUnit accuracy) {
        this(toStartOfDate(startDate), toEndOfDate(endDate, accuracy));
    }

    /**
     * Constructs a {@link TimeInterval} for the given {@link Interval}.
     *
     * @param interval the {@link Interval} to create the interval for.
     */
    public TimeInterval(Interval interval) {
        this(interval.start(), interval.end());
    }

    /**
     * Constructs an {@link TimeInterval} for the given {@link YearMonth}.
     *
     * @param yearMonth the {@link YearMonth} to create the interval for.
     */
    public TimeInterval(YearMonth yearMonth) {
        this(toStartOfDate(yearMonth.atDay(1)), toStartOfDate(yearMonth.atEndOfMonth()));
    }

    /**
     * Constructs an {@link TimeInterval} for the given {@link Year}.
     *
     * @param year the {@link Year} to create the interval for.
     */
    public TimeInterval(Year year) {
        this(toStartOfDate(year.atDay(1)), toStartOfDate(DateUtils.getLastDayOfYear(year)));
    }

    /**
     * Returns this interval.
     *
     * @return this interval.
     */
    public TimeInterval getInterval() {
        return this;
    }

    /**
     * Validates whether the start dateTime is before or equal to the end dateTime.
     *
     * @param start the start dateTime
     * @param end   the end dateTime
     * @return true if the start dateTime is after the end dateTime, false otherwise.
     */
    public static boolean isNotValid(@Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        return start != null && end != null && start.isAfter(end);
    }

    /**
     * Validates whether the start LocalDateTime is before or equal to the end LocalDateTime.
     *
     * @param start the start LocalDateTime.
     * @param end   the end LocalDateTime.
     * @return true if the start LocalDateTime is before or equal to the end LocalDateTime, false otherwise.
     */
    public static boolean isValid(@Nullable LocalDateTime start, @Nullable LocalDateTime end) {
        return !isNotValid(start, end);
    }

    /**
     * Constructs an {@link TimeInterval} using ISO 8601 formatted date (such as "2007-12-03"),
     * starting from the given date time and with open end.
     *
     * @param startDate the start date time (inclusive).
     * @return a {@link TimeInterval} based on the given date and open end.
     */
    public static TimeInterval forIsoDateToNull(String startDate) {
        return forIsoDates(startDate, null);
    }

    /**
     * Constructs an open-ended {@link TimeInterval} same as in {@link TimeInterval#forIsoDateToNull(String)},
     * but with overridden millisecond accuracy.
     *
     * @param startDate the start date time (inclusive).
     * @return a {@link TimeInterval} based on the given date and open end.
     */
    public static TimeInterval forIsoDateMilliToNull(String startDate) {
        return forIsoDates(startDate, null, TimeUnit.MILLISECONDS);
    }

    /**
     * Constructs an {@link TimeInterval} using ISO 8601 formatted date times (such as "2007-12-03T10:15:30.00").
     * The format is further specified in {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}.
     *
     * @param start start of the interval (inclusive).
     * @param end   end of the interval (inclusive).
     * @return an {@link TimeInterval} based on the given dateTimes.
     */
    public static TimeInterval forIsoDateTimes(@Nullable String start, @Nullable String end) {
        return new TimeInterval(parseDateTime(start), parseDateTime(end));
    }

    /**
     * Constructs an {@link TimeInterval} using ISO 8601 formatted dates (such as "2007-12-03").
     *
     * @param startDate the start date (inclusive).
     * @param endDate   the end date (inclusive).
     * @return an {@link TimeInterval} based on the given dates.
     */
    public static TimeInterval forIsoDates(@Nullable String startDate, @Nullable String endDate) {
        return new TimeInterval(parseLocalDate(startDate), parseLocalDate(endDate));
    }

    /**
     * Constructs an {@link TimeInterval} using ISO 8601 formatted dates (such as "2007-12-03"),
     * with the given accuracy to override the default with.
     *
     * @param startDate the start date (inclusive).
     * @param endDate   the end date (inclusive).
     * @param accuracy  the {@link TimeUnit} accuracy to represent the date-time interval with.
     * @return an {@link TimeInterval} based on the given dates.
     */
    public static TimeInterval forIsoDates(@Nullable String startDate, @Nullable String endDate, TimeUnit accuracy) {
        return new TimeInterval(parseLocalDate(startDate), parseLocalDate(endDate), accuracy);
    }

    /**
     * Constructs an {@link TimeInterval} same as in {@link TimeInterval#forIsoDates(String, String, TimeUnit)},
     * but with overridden millisecond accuracy.
     *
     * @param startDate the start date (inclusive).
     * @param endDate   the end date (inclusive).
     * @return an {@link TimeInterval} based on the given dates.
     */
    public static TimeInterval forIsoDatesMilli(@Nullable String startDate, @Nullable String endDate) {
        return forIsoDates(startDate, endDate, TimeUnit.MILLISECONDS);
    }

    /**
     * Constructs an {@link TimeInterval} using a ISO 8601 formatted month ("YYYY-MM").
     *
     * @param month a string representing the month (e.g. "2025-07")
     * @return an {@link TimeInterval} based on the given month
     */
    public static TimeInterval forIsoMonth(@Nonnull String month) {
        return new TimeInterval(YearMonth.parse(month));
    }

    /**
     * Constructs an {@link TimeInterval} using a year.
     *
     * @param year an int representing the year (e.g. 2025)
     * @return an {@link TimeInterval} based on the given year
     */
    public static TimeInterval forIsoYear(int year) {
        return new TimeInterval(Year.of(year));
    }

    /**
     * Returns the start LocalDateTime, or the minimum LocalDateTime if the start is null.
     *
     * @return the start LocalDateTime or {@link TimeFactory#getMinDateTime()}
     */
    public LocalDateTime getStartNullAsMin() {
        return hasNullStart() ? TimeFactory.getMinDateTime() : start;
    }

    /**
     * Returns the end LocalDateTime, or the maximum LocalDateTime if the end is null.
     *
     * @return the end LocalDateTime or {@link TimeFactory#getMaxDateTime()}
     */
    public LocalDateTime getEndNullAsMax() {
        return hasNullEnd() ? TimeFactory.getMaxDateTime() : end;
    }

    /**
     * Returns a sorted list of intervals, covering the same LocalDateTimes as the input, but without any intervals overlapping.
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
    public static List<TimeInterval> split(Collection<TimeInterval> intervals) {
        if (intervals.size() <= 1) {
            return new ArrayList<>(intervals);
        }

        // generate start dateTimes from end dateTimes, and vice versa, selecting only those that overlap with the given intervals, with start/end-specific sorting
        List<LocalDateTime> startDateTimes = getSortedDatesWithFunctionsPredicateAndComparator(intervals, TimeInterval::start, i -> addSingle(i.end()),
                TimeConstants.NULL_AS_MIN_DATE_TIME_COMPARATOR, TimeInterval::containsNullAsMin);
        List<LocalDateTime> endDateTimes = getSortedDatesWithFunctionsPredicateAndComparator(intervals, TimeInterval::end, i -> subtractSingle(i.start()),
                TimeConstants.NULL_AS_MAX_DATE_TIME_COMPARATOR, TimeInterval::containsNullAsMax);

        if (startDateTimes.size() != endDateTimes.size()) {
            throw new IllegalStateException(String.format("Unable to split collection of intervals: %s", intervals));
        }

        List<TimeInterval> splittedIntervals = new ArrayList<>();
        for (int i = 0; i < startDateTimes.size(); i++) {
            splittedIntervals.add(new TimeInterval(startDateTimes.get(i), endDateTimes.get(i)));
        }
        return splittedIntervals;
    }

    private static List<LocalDateTime> getSortedDatesWithFunctionsPredicateAndComparator(Collection<TimeInterval> intervals,
                                                                                         Function<TimeInterval, LocalDateTime> keepNull,
                                                                                         Function<TimeInterval, LocalDateTime> discardNull,
                                                                                         Comparator<LocalDateTime> comparator,
                                                                                         BiPredicate<TimeInterval, LocalDateTime> predicate) {
        return intervals.stream()
                .flatMap(interval -> Stream.concat(
                        Stream.of(keepNull.apply(interval)),
                        Stream.ofNullable(discardNull.apply(interval))))
                .filter(date -> isAnyMatchForPredicateOnDate(intervals, predicate, date))
                .distinct()
                .sorted(comparator)
                .toList();
    }

    private static boolean isAnyMatchForPredicateOnDate(Collection<TimeInterval> intervals, BiPredicate<TimeInterval, LocalDateTime> predicate, LocalDateTime dateTime) {
        return intervals.stream()
                .anyMatch(interval -> predicate.test(interval, dateTime));
    }

    private boolean hasNullStart() {
        return start == null;
    }

    private boolean hasNullEnd() {
        return end == null;
    }

    /**
     * Checks if the given dateTime is contained within this interval, treating null as {@link TimeFactory#getMinDateTime()}.
     *
     * @param dateTime the dateTime to check
     * @return true if the dateTime is contained, false otherwise
     */
    public boolean containsNullAsMin(@Nullable LocalDateTime dateTime) {
        LocalDateTime nonNullDateTime = dateTime == null ? TimeFactory.getMinDateTime() : dateTime;
        return (hasNullStart() || !nonNullDateTime.isBefore(start)) && (hasNullEnd() || !nonNullDateTime.isAfter(end));
    }

    /**
     * Checks if the given dateTime is contained within this interval, treating null as {@link TimeFactory#getMaxDateTime()}.
     *
     * @param dateTime the dateTime to check
     * @return true if the dateTime is contained, false otherwise
     */
    public boolean containsNullAsMax(@Nullable LocalDateTime dateTime) {
        LocalDateTime nonNullDateTime = dateTime == null ? TimeFactory.getMaxDateTime() : dateTime;
        return (hasNullStart() || !nonNullDateTime.isBefore(start)) && (hasNullEnd() || !nonNullDateTime.isAfter(end));
    }

    /**
     * Checks if the given dateTime is contained within this interval.
     *
     * @param dateTime the dateTime to check.
     * @return true if the dateTime is contained, false otherwise.
     */
    public boolean contains(@Nonnull LocalDateTime dateTime) {
        return (hasNullStart() || !dateTime.isBefore(start)) && (hasNullEnd() || !dateTime.isAfter(end));
    }

    /**
     * Checks if the given date is contained within this interval, by checking the start of the date.
     *
     * @param date the date to check.
     * @return true if the date is contained, false otherwise.
     */
    public boolean contains(@Nonnull LocalDate date) {
        LocalDateTime startOfDay = DateTimeUtils.toStartOfDate(date);
        return (hasNullStart() || !startOfDay.isBefore(start)) && (hasNullEnd() || !startOfDay.isAfter(end));
    }

    /**
     * Checks if the given {@link YearMonth} is fully contained within this interval.
     *
     * @param yearMonth a given {@link YearMonth}.
     * @return true if this {@link TimeInterval} fully contains the given {@link YearMonth}, otherwise false.
     */
    public boolean contains(@Nonnull YearMonth yearMonth) {
        return contains(new TimeInterval(yearMonth));
    }

    /**
     * Checks if the given {@link TimeInterval} is fully contained within this interval.
     *
     * @param interval a given {@link TimeInterval}.
     * @return true if this {@link TimeInterval} fully contains the given {@link TimeInterval}, otherwise false.
     */
    public boolean contains(@Nonnull TimeInterval interval) {
        return (hasNullStart() || (interval.hasNullStart() || !DateTimeUtils.isBeforeNullAsMin(interval.start(), start())))
                && (hasNullEnd() || (interval.hasNullEnd() || !DateTimeUtils.isAfterNullAsMax(interval.end(), end())));
    }

    /**
     * Checks if this {@link TimeInterval} overlaps the given interval.
     *
     * @param interval a given {@link TimeInterval}
     * @return true if this {@link TimeInterval} overlaps the given interval, otherwise false
     */
    public boolean overlaps(@Nonnull TimeInterval interval) {
        return (hasNullStart() || !DateTimeUtils.isAfterNullAsMax(start(), interval.end()))
                && (hasNullEnd() || !DateTimeUtils.isBeforeNullAsMin(end(), interval.start()));
    }

    /**
     * Checks if this {@link TimeInterval} overlaps with any of the given intervals.
     *
     * @param intervals a collection of {@link TimeInterval}
     * @return true if this {@link TimeInterval} overlaps with any of the given intervals, otherwise false
     */
    public boolean overlaps(Collection<TimeInterval> intervals) {
        return intervals.stream()
                .anyMatch(this::overlaps);
    }

    /**
     * Returns true if this {@link TimeInterval} overlaps with the given yearMonth, otherwise false.
     *
     * @param yearMonth a {@link YearMonth}
     * @return true if this {@link TimeInterval} overlaps with the given yearMonth, otherwise false
     */
    public boolean overlaps(@Nonnull YearMonth yearMonth) {
        return overlaps(new TimeInterval(yearMonth));
    }

    /**
     * Returns true if this {@link TimeInterval} overlaps with the given year, otherwise false.
     *
     * @param year a {@link Year}
     * @return true if this {@link TimeInterval} overlaps with the given year, otherwise false
     */
    public boolean overlaps(@Nonnull Year year) {
        return overlaps(new TimeInterval(year));
    }

    /**
     * Returns true if any of the given intervals overlap with each other, otherwise false.
     *
     * @param intervals a list of intervals
     * @return true if any of the given intervals overlap with each other
     */
    public static boolean anyOverlaps(Collection<TimeInterval> intervals) {
        return intervals.stream()
                .anyMatch(interval -> interval.overlaps(interval.relativeComplement(intervals)));
    }

    /**
     * Returns the list of the given intervals, excluding any elements equal to this interval.
     *
     * @param intervals a list of intervals
     * @return the result of removing any intervals equal to this interval
     */
    public List<TimeInterval> relativeComplement(Collection<TimeInterval> intervals) {
        return intervals.stream()
                .filter(Predicate.not(this::equals))
                .toList();
    }

    /**
     * Checks if this {@link TimeInterval} partially overlaps the given interval, meaning that they overlap but neither contains the other.
     *
     * @param interval a given {@link TimeInterval}
     * @return true if this {@link TimeInterval} overlaps the given interval and neither interval contains the other, otherwise false
     */
    public boolean partiallyOverlaps(@Nonnull TimeInterval interval) {
        return this.overlaps(interval) && !this.contains(interval) && !interval.contains(this);
    }

    /**
     * Returns the overlap between this interval and the given interval, otherwise an empty Optional if there is no overlap.
     * <pre>
     * This interval:
     * |--------------------------------------|
     * Input:
     *        |---------------|
     * Output:
     *        |---------------|
     * </pre>
     *
     * @param interval a given {@link TimeInterval}
     * @return the overlap between this interval and the given interval, otherwise an empty Optional if there is no overlap.
     */
    public Optional<TimeInterval> getOverlap(@Nonnull TimeInterval interval) {
        if (overlaps(interval)) {
            return Optional.of(new TimeInterval(
                    DateTimeUtils.maxNullAsMin(this.start(), interval.start()),
                    DateTimeUtils.minNullAsMax(this.end(), interval.end())
            ));
        }
        return Optional.empty();
    }

    /**
     * Returns a new version of this interval, where the end date is bounded according to the given {@code upperBound}.
     * Equivalent to calling {@link TimeInterval#getOverlap(TimeInterval)} with an interval starting from null and ending in {@code upperBound}.
     *
     * @param upperBound an interval defining the bounds to apply to this interval
     * @return a new version of this interval, bounded according to given {@code upperBound}, or an empty Optional if the bounding results in an invalid interval
     */
    public Optional<TimeInterval> boundEnd(@Nullable LocalDateTime upperBound) {
        return getOverlap(new TimeInterval(null, upperBound));
    }

    /**
     * Returns a new version of this interval, where the start date is bounded according to the given {@code lowerBound}.
     * Equivalent to calling {@link TimeInterval#getOverlap(TimeInterval)} with an interval starting from {@code lowerBound} and ending in null.
     *
     * @param lowerBound a date defining the lower bound to apply to this interval
     * @return a new version of this interval, bounded according to given {@code lowerBound}, or an empty Optional if the bounding results in an invalid interval
     */
    public Optional<TimeInterval> boundStart(@Nullable LocalDateTime lowerBound) {
        return getOverlap(new TimeInterval(lowerBound, null));
    }

    /**
     * Returns a new version of this interval, where the start date is bounded according to {@link TimeInterval#boundStart(LocalDateTime)}.
     * If the bounding results in an invalid interval, the original interval is returned.
     *
     * @param lowerBound a date defining the lower bound to apply to this interval.
     * @return a new version of this interval, bounded according to given {@code lowerBound}, or the original interval if the bounding results in an invalid interval.
     */
    public TimeInterval boundStartDateIfValid(@Nullable LocalDateTime lowerBound) {
        return boundStart(lowerBound)
                .orElse(this);
    }

    /**
     * Returns a normalized (see {@link TimeInterval#normalize(Collection)}) list of overlaps between the given intervals and this interval.
     * <pre>
     * This interval:
     * |--------------------------------|
     * Input:
     *        |---------|           |------|
     *             |-------||--|
     * Output:
     *        |----------------|    |---|
     * </pre>
     *
     * @param intervals intervals to determine overlaps for
     * @return a normalized list of overlaps between the given intervals and this interval
     */
    public List<TimeInterval> getOverlaps(Collection<TimeInterval> intervals) {
        return getOverlaps(intervals, true);
    }

    /**
     * Vararg variant of {@link TimeInterval#getOverlaps(Collection)}.
     *
     * @param intervals intervals to determine overlaps for
     * @return a normalized list of overlaps between the given intervals and this interval
     */
    public List<TimeInterval> getOverlaps(TimeInterval... intervals) {
        return getOverlaps(List.of(intervals));
    }

    /**
     * Returns a normalized (see {@link TimeInterval#normalize(Collection)}) list of overlaps between the given intervals and this interval.
     * Whether to merge adjacent overlaps is subject to the value of {@code mergeAdjacentIntervals}
     * <pre>
     * This interval:
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
     * @return a normalized list of overlaps between the given intervals and this interval
     */
    public List<TimeInterval> getOverlaps(Collection<TimeInterval> intervals, boolean mergeAdjacentIntervals) {
        List<TimeInterval> overlaps = intervals.stream()
                .map(this::getOverlap)
                .flatMap(Optional::stream)
                .toList();
        return normalize(overlaps, mergeAdjacentIntervals);
    }

    /**
     * Returns a normalized list of the portions of {@code intervals} that do not overlap with this interval.
     * <pre>
     * This interval:
     *                |---------------|
     * Input:
     *        |---------|         |------|
     *             |---------|
     * Output:
     *        |------|                 |-|
     * </pre>
     *
     * @param intervals list of intervals
     * @return the portions of {@code intervals} that do not overlap with this interval
     */
    public List<TimeInterval> getNotOverlaps(Collection<TimeInterval> intervals) {
        List<TimeInterval> overlaps = this.getOverlaps(intervals);
        List<TimeInterval> notOverlaps = intervals.stream()
                .map(interval -> interval.subtract(overlaps))
                .flatMap(Collection::stream)
                .toList();
        return normalize(notOverlaps);
    }

    /**
     * Vararg variant of {@link TimeInterval#getNotOverlaps(Collection)}.
     *
     * @param intervals given intervals
     * @return the portions of {@code intervals} that do not overlap with this interval
     */
    public List<TimeInterval> getNotOverlaps(TimeInterval... intervals) {
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
    public static List<TimeInterval> normalize(Collection<TimeInterval> intervals) {
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
    public static List<TimeInterval> normalize(Collection<TimeInterval> intervals, boolean mergeAdjacentIntervals) {
        if (intervals.size() <= 1) {
            return new ArrayList<>(intervals);
        }

        List<TimeInterval> sortedIntervals = sort(intervals);
        List<TimeInterval> result = new ArrayList<>();
        TimeInterval current = sortedIntervals.getFirst();

        for (TimeInterval interval : sortedIntervals) {
            if (shouldMergeCurrentIntervalWithNextInterval(current, interval, mergeAdjacentIntervals)) {
                current = new TimeInterval(current.start(), DateTimeUtils.maxNullAsMax(current.end(), interval.end()));
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

    private static boolean shouldMergeCurrentIntervalWithNextInterval(TimeInterval current, TimeInterval next, boolean mergeAdjacentIntervals) {
        LocalDateTime adjustedEnd = mergeAdjacentIntervals ? addSingle(current.end()) : current.end();
        return !DateTimeUtils.isAfterNullAsMax(next.start(), adjustedEnd);
    }

    private static List<TimeInterval> sort(Collection<TimeInterval> intervals) {
        return intervals.stream()
                .sorted()
                .toList();
    }

    /**
     * Checks if the given {@link TimeInterval} is touching this interval.
     *
     * @param interval a given {@link TimeInterval}
     * @return true if this interval ends on the day before the given interval starts or starts on the day after the given interval ends, otherwise false.
     */
    public boolean adjacent(@Nonnull TimeInterval interval) {
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
     * @return list of Intervals after subtracting the given {@code interval} from this {@link TimeInterval}
     */
    public List<TimeInterval> subtract(TimeInterval interval) {
        return this.subtract(List.of(interval));
    }

    /**
     * Returns the remainder of this interval, after subtracting the overlap with the given intervals.
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
     * @return list of intervals after subtracting the given {@code intervals} from this {@link TimeInterval}
     */
    public List<TimeInterval> subtract(Collection<TimeInterval> intervals) {
        @Nullable LocalDateTime nextPossibleStart = this.start();
        boolean endCrossed = false;
        List<TimeInterval> resultIntervals = new ArrayList<>();
        for (TimeInterval overlap : this.getOverlaps(intervals)) {
            if (DateTimeUtils.isBeforeNullAsMin(nextPossibleStart, overlap.start())) {
                resultIntervals.add(new TimeInterval(
                        nextPossibleStart,
                        DateTimeUtils.minNullAsMax(this.end(), subtractSingle(overlap.start())))
                );
            }
            if (DateTimeUtils.isAfterOrEqual(overlap.end(), this.end())) {
                endCrossed = true;
                break;
            }
            if (DateTimeUtils.isBeforeOrEqual(nextPossibleStart, overlap.end())) {
                nextPossibleStart = addSingle(overlap.end());
            }
        }
        if (!endCrossed) {
            resultIntervals.add(new TimeInterval(nextPossibleStart, end()));
        }

        return resultIntervals;
    }

    @Override
    public int compareTo(@Nonnull TimeInterval o) {
        int startComparisonResult = TimeConstants.NULL_AS_MIN_DATE_TIME_COMPARATOR.compare(start(), o.start());
        if (startComparisonResult == 0) {
            return TimeConstants.NULL_AS_MAX_DATE_TIME_COMPARATOR.compare(end(), o.end());
        } else {
            return startComparisonResult;
        }
    }
}
