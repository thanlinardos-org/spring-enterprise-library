package com.thanlinardos.spring_enterprise_library.time.constants;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;

/**
 * A utility class that provides constants and comparators for handling time-related operations.
 */
public class TimeConstants {

    private TimeConstants() {
    }

    /**
     * Comparators that handle null values by treating them as minimum or maximum values.
     * null as minimum date
     */
    public static final Comparator<LocalDate> NULL_AS_MIN_COMPARATOR = Comparator.nullsFirst(LocalDate::compareTo);
    /** null as maximum date. */
    public static final Comparator<LocalDate> NULL_AS_MAX_COMPARATOR = Comparator.nullsLast(LocalDate::compareTo);
    /** null as minimum date-time. */
    public static final Comparator<LocalDateTime> NULL_AS_MIN_DATE_TIME_COMPARATOR = Comparator.nullsFirst(LocalDateTime::compareTo);
    /** null as maximum date-time. */
    public static final Comparator<LocalDateTime> NULL_AS_MAX_DATE_TIME_COMPARATOR = Comparator.nullsLast(LocalDateTime::compareTo);
    /** null as minimum instant. */
    public static final Comparator<Instant> NULL_AS_MIN_INSTANT_COMPARATOR = Comparator.nullsFirst(Instant::compareTo);
    /** null as maximum instant. */
    public static final Comparator<Instant> NULL_AS_MAX_INSTANT_COMPARATOR = Comparator.nullsLast(Instant::compareTo);
}
