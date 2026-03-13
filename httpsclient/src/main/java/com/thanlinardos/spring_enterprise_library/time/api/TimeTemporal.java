package com.thanlinardos.spring_enterprise_library.time.api;

import com.thanlinardos.spring_enterprise_library.time.model.TimeInterval;
import com.thanlinardos.spring_enterprise_library.time.utils.DateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;

/**
 * Interface representing a temporal entity with a time interval.
 */
public interface TimeTemporal {

    /**
     * Gets the time interval of the temporal entity.
     *
     * @return the date interval.
     */
    TimeInterval getInterval();

    /**
     * Checks if the temporal entity is within the specified date range.
     *
     * @param from the start date of the range.
     * @param to   the end date of the range.
     * @return true if the temporal entity is within the range, false otherwise.
     */
    default boolean isInRange(LocalDate from, LocalDate to) {
        return new TimeInterval(from, to).contains(getInterval());
    }

    /**
     * Checks if the temporal entity is within the specified date time range.
     *
     * @param from the start date time of the range.
     * @param to   the end date time of the range.
     * @return true if the temporal entity is within the range, false otherwise.
     */
    default boolean isInRange(LocalDateTime from, LocalDateTime to) {
        return new TimeInterval(from, to).contains(getInterval());
    }

    /**
     * Checks if the temporal entity is contained within another temporal entity's interval.
     *
     * @param interval the other temporal entity.
     * @return true if this entity is contained within the other entity's interval, false otherwise.
     */
    default boolean isContainedIn(TimeTemporal interval) {
        return interval.getInterval().contains(getInterval());
    }

    /**
     * Checks if the temporal entity is contained within the specified date range.
     *
     * @param from the start date of the range.
     * @param to   the end date of the range.
     * @return true if the temporal entity is contained within the range, false otherwise.
     */
    default boolean isContainedIn(LocalDate from, LocalDate to) {
        return new TimeInterval(from, to).contains(getInterval());
    }

    /**
     * Checks if the temporal entity is contained within the specified date time range.
     *
     * @param from the start date time of the range.
     * @param to   the end date time of the range.
     * @return true if the temporal entity is contained within the range, false otherwise.
     */
    default boolean isContainedIn(LocalDateTime from, LocalDateTime to) {
        return new TimeInterval(from, to).contains(getInterval());
    }

    /**
     * Checks if the temporal entity contains another temporal entity's interval.
     *
     * @param interval the other temporal entity.
     * @return true if this entity contains the other entity's interval, false otherwise.
     */
    default boolean containsInterval(TimeTemporal interval) {
        return getInterval().contains(interval.getInterval());
    }

    /**
     * Checks if the temporal entity contains the specified date.
     *
     * @param date the date to check.
     * @return true if the temporal entity contains the date, false otherwise.
     */
    default boolean containsDate(LocalDate date) {
        return getInterval().contains(date);
    }

    /**
     * Checks if the temporal entity contains the specified dateTime.
     *
     * @param dateTime the {@link LocalDateTime} to check.
     * @return true if the temporal entity contains the dateTime, false otherwise.
     */
    default boolean containsDateTime(LocalDateTime dateTime) {
        return getInterval().contains(dateTime);
    }

    /**
     * Checks if the temporal entity contains the specified month.
     *
     * @param yearMonth the month to check.
     * @return true if the temporal entity contains the month, false otherwise.
     */
    default boolean containsMonth(YearMonth yearMonth) {
        return getInterval().contains(yearMonth);
    }

    /**
     * Checks if the interval of this temporal entity is equal to another temporal entity's interval.
     *
     * @param interval the other temporal entity.
     * @return true if the intervals are equal, false otherwise.
     */
    default boolean equalsInterval(TimeTemporal interval) {
        return getInterval().equals(interval.getInterval());
    }

    /**
     * Checks if the interval of this temporal entity overlaps with another temporal entity's interval.
     *
     * @param interval the other temporal entity.
     * @return true if the intervals overlap, false otherwise.
     */
    default boolean overlapsInterval(TimeTemporal interval) {
        return getInterval().overlaps(interval.getInterval());
    }

    /**
     * Checks if the interval of this temporal entity overlaps with the specified month.
     *
     * @param yearMonth the month to check.
     * @return true if the interval overlaps with the month, false otherwise.
     */
    default boolean overlapsMonth(YearMonth yearMonth) {
        return getInterval().overlaps(yearMonth);
    }

    /**
     * Checks if the interval of this temporal entity overlaps with the specified year.
     *
     * @param year the year to check.
     * @return true if the interval overlaps with the year, false otherwise.
     */
    default boolean overlapsYear(Year year) {
        return getInterval().overlaps(year);
    }

    /**
     * Checks if the interval of this temporal entity overlaps with the year of the specified date.
     *
     * @param yearDate the date whose year to check.
     * @return true if the interval overlaps with the year, false otherwise.
     */
    default boolean overlapsYear(LocalDate yearDate) {
        return getInterval().overlaps(Year.from(yearDate));
    }

    /**
     * Checks if the interval of this temporal entity overlaps with the specified date range.
     *
     * @param start the start date of the range.
     * @param end   the end date of the range.
     * @return true if the intervals overlap, false otherwise.
     */
    default boolean overlapsInterval(LocalDate start, LocalDate end) {
        return getInterval().overlaps(new TimeInterval(start, end));
    }

    /**
     * Checks if the interval of this temporal entity overlaps with the specified date time range.
     *
     * @param start the start date time of the range.
     * @param end   the end date time of the range.
     * @return true if the intervals overlap, false otherwise.
     */
    default boolean overlapsInterval(LocalDateTime start, LocalDateTime end) {
        return getInterval().overlaps(new TimeInterval(start, end));
    }

    /**
     * Checks if the interval of this temporal entity starts after the specified date.
     *
     * @param date the date to check.
     * @return true if the interval starts after the date, false otherwise.
     */
    default boolean startsAfter(LocalDate date) {
        return DateTimeUtils.isAfterNullAsMax(getInterval().start(), DateTimeUtils.toStartOfDate(date));
    }

    /**
     * Checks if the interval of this temporal entity ends after the specified date.
     *
     * @param date the date to check.
     * @return true if the interval ends after the date, false otherwise.
     */
    default boolean endsAfter(LocalDate date) {
        return DateTimeUtils.isAfterNullAsMax(getInterval().end(), DateTimeUtils.toStartOfDate(date));
    }

    /**
     * Checks if the interval of this temporal entity ends after or on the specified date.
     *
     * @param date the date to check.
     * @return true if the interval ends after or on the date, false otherwise.
     */
    default boolean endsAfterOrOn(LocalDate date) {
        return DateTimeUtils.isAfterOrEqual(getInterval().end(), DateTimeUtils.toStartOfDate(date));
    }

    /**
     * Checks if the interval of this temporal entity starts before the specified date.
     *
     * @param date the date to check.
     * @return true if the interval starts before the date, false otherwise.
     */
    default boolean startsBefore(LocalDate date) {
        return DateTimeUtils.isBeforeNullAsMin(getInterval().start(), DateTimeUtils.toStartOfDate(date));
    }

    /**
     * Checks if the interval of this temporal entity starts before or on the specified date.
     *
     * @param date the date to check.
     * @return true if the interval starts before or on the date, false otherwise.
     */
    default boolean startsBeforeOrOn(LocalDate date) {
        return DateTimeUtils.isBeforeOrEqual(getInterval().start(), DateTimeUtils.toStartOfDate(date));
    }

    /**
     * Checks if the interval of this temporal entity ends after the specified dateTime.
     *
     * @param dateTime the date to check.
     * @return true if the interval ends after the dateTime, false otherwise.
     */
    default boolean endsAfter(LocalDateTime dateTime) {
        return DateTimeUtils.isAfterNullAsMax(getInterval().end(), dateTime);
    }

    /**
     * Checks if the interval of this temporal entity ends after or on the specified dateTime.
     *
     * @param dateTime the dateTime to check.
     * @return true if the interval ends after or on the dateTime, false otherwise.
     */
    default boolean endsAfterOrOn(LocalDateTime dateTime) {
        return DateTimeUtils.isAfterOrEqual(getInterval().end(), dateTime);
    }

    /**
     * Checks if the interval of this temporal entity starts before the specified dateTime.
     *
     * @param dateTime the dateTime to check.
     * @return true if the interval starts before the dateTime, false otherwise.
     */
    default boolean startsBefore(LocalDateTime dateTime) {
        return DateTimeUtils.isBeforeNullAsMin(getInterval().start(), dateTime);
    }

    /**
     * Checks if the interval of this temporal entity starts before or on the specified dateTime.
     *
     * @param dateTime the dateTime to check.
     * @return true if the interval starts before or on the dateTime, false otherwise.
     */
    default boolean startsBeforeOrOn(LocalDateTime dateTime) {
        return DateTimeUtils.isBeforeOrEqual(getInterval().start(), dateTime);
    }
}
