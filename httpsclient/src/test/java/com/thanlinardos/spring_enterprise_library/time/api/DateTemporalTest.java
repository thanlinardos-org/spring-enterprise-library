package com.thanlinardos.spring_enterprise_library.time.api;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.model.Interval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class DateTemporalTest {

    @Test
    void defaultMethods_shouldDelegateToIntervalBehavior() {
        DateTemporal temporal = new TestDateTemporal(new Interval(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20)));
        DateTemporal inside = new TestDateTemporal(new Interval(LocalDate.of(2025, 1, 12), LocalDate.of(2025, 1, 13)));
        DateTemporal outside = new TestDateTemporal(new Interval(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2)));

        assertTrue(temporal.isInRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
        assertTrue(inside.isContainedIn(temporal));
        assertTrue(inside.isContainedIn(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
        assertTrue(temporal.containsInterval(inside));
        assertTrue(temporal.containsDate(LocalDate.of(2025, 1, 15)));
        assertFalse(temporal.containsMonth(YearMonth.of(2025, 1)));
        assertTrue(temporal.equalsInterval(new TestDateTemporal(new Interval(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20)))));

        assertFalse(temporal.overlapsInterval(outside));
        assertTrue(temporal.overlapsMonth(YearMonth.of(2025, 1)));
        assertTrue(temporal.overlapsYear(Year.of(2025)));
        assertTrue(temporal.overlapsYear(LocalDate.of(2025, 6, 1)));
        assertTrue(temporal.overlapsInterval(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 11)));

        assertTrue(temporal.startsAfter(LocalDate.of(2025, 1, 1)));
        assertTrue(temporal.endsAfter(LocalDate.of(2025, 1, 19)));
        assertTrue(temporal.endsAfterOrOn(LocalDate.of(2025, 1, 20)));
        assertTrue(temporal.startsBefore(LocalDate.of(2025, 1, 11)));
        assertTrue(temporal.startsBeforeOrOn(LocalDate.of(2025, 1, 10)));
    }

    private record TestDateTemporal(Interval interval) implements DateTemporal {
        @Override
        public Interval getInterval() {
            return interval;
        }
    }
}


