package com.thanlinardos.spring_enterprise_library.time.api;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.model.TimeInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class TimeTemporalTest {


    @Test
    void defaultMethods_shouldDelegateToTimeIntervalBehavior() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 20, 0, 0);

        TimeTemporal temporal = new TestTimeTemporal(new TimeInterval(start, end));
        TimeTemporal inside = new TestTimeTemporal(new TimeInterval(
                LocalDateTime.of(2025, 1, 12, 0, 0),
                LocalDateTime.of(2025, 1, 13, 0, 0)));
        TimeTemporal outside = new TestTimeTemporal(new TimeInterval(
                LocalDateTime.of(2025, 2, 1, 0, 0),
                LocalDateTime.of(2025, 2, 2, 0, 0)));

        assertTrue(temporal.isInRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
        assertTrue(temporal.isInRange(LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 31, 0, 0)));
        assertTrue(inside.isContainedIn(temporal));
        assertTrue(inside.isContainedIn(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
        assertTrue(inside.isContainedIn(LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 31, 0, 0)));
        assertTrue(temporal.containsInterval(inside));
        assertTrue(temporal.containsDate(LocalDate.of(2025, 1, 12)));
        assertTrue(temporal.containsDateTime(LocalDateTime.of(2025, 1, 15, 0, 0)));
        assertFalse(temporal.containsMonth(YearMonth.of(2025, 1)));
        assertTrue(temporal.equalsInterval(new TestTimeTemporal(new TimeInterval(start, end))));

        assertFalse(temporal.overlapsInterval(outside));
        assertTrue(temporal.overlapsMonth(YearMonth.of(2025, 1)));
        assertTrue(temporal.overlapsYear(Year.of(2025)));
        assertTrue(temporal.overlapsYear(LocalDate.of(2025, 6, 1)));
        assertTrue(temporal.overlapsInterval(LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 11)));
        assertTrue(temporal.overlapsInterval(LocalDateTime.of(2025, 1, 5, 0, 0), LocalDateTime.of(2025, 1, 11, 0, 0)));

        assertTrue(temporal.startsAfter(LocalDate.of(2025, 1, 1)));
        assertTrue(temporal.endsAfter(LocalDate.of(2025, 1, 15)));
        assertTrue(temporal.endsAfterOrOn(LocalDate.of(2025, 1, 20)));
        assertTrue(temporal.startsBefore(LocalDate.of(2025, 1, 11)));
        assertTrue(temporal.startsBeforeOrOn(LocalDate.of(2025, 1, 10)));
        assertTrue(temporal.endsAfter(LocalDateTime.of(2025, 1, 19, 0, 0)));
        assertTrue(temporal.endsAfterOrOn(end));
        assertTrue(temporal.startsBefore(LocalDateTime.of(2025, 1, 10, 0, 1)));
        assertTrue(temporal.startsBeforeOrOn(start));
    }

    private record TestTimeTemporal(TimeInterval interval) implements TimeTemporal {
        @Override
        public TimeInterval getInterval() {
            return interval;
        }
    }
}


