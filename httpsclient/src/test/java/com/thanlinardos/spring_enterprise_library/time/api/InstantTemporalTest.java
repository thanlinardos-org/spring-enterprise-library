package com.thanlinardos.spring_enterprise_library.time.api;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.model.InstantInterval;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class InstantTemporalTest {


    @Test
    void defaultMethods_shouldDelegateToInstantIntervalBehavior() {
        Instant start = Instant.parse("2025-01-10T00:00:00Z");
        Instant end = Instant.parse("2025-01-20T00:00:00Z");

        InstantTemporal temporal = new TestInstantTemporal(new InstantInterval(start, end));
        InstantTemporal inside = new TestInstantTemporal(new InstantInterval(
                Instant.parse("2025-01-12T00:00:00Z"),
                Instant.parse("2025-01-13T00:00:00Z")));
        InstantTemporal outside = new TestInstantTemporal(new InstantInterval(
                Instant.parse("2025-02-01T00:00:00Z"),
                Instant.parse("2025-02-02T00:00:00Z")));

        assertTrue(temporal.isInRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
        assertTrue(temporal.isInRange(Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-31T00:00:00Z")));
        assertTrue(inside.isContainedIn(temporal));
        assertTrue(inside.isContainedIn(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31)));
        assertTrue(inside.isContainedIn(Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-31T00:00:00Z")));
        assertTrue(temporal.containsInterval(inside));
        assertTrue(temporal.containsDate(LocalDate.of(2025, 1, 12)));
        assertTrue(temporal.containsDateTime(Instant.parse("2025-01-15T00:00:00Z")));
        assertFalse(temporal.containsMonth(YearMonth.of(2025, 1)));
        assertTrue(temporal.equalsInterval(new TestInstantTemporal(new InstantInterval(start, end))));

        assertFalse(temporal.overlapsInterval(outside));
        assertTrue(temporal.overlapsMonth(YearMonth.of(2025, 1)));
        assertTrue(temporal.overlapsYear(Year.of(2025)));
        assertTrue(temporal.overlapsYear(LocalDate.of(2025, 6, 1)));
        assertTrue(temporal.overlapsInterval(LocalDate.of(2025, 1, 5), LocalDate.of(2025, 1, 11)));
        assertTrue(temporal.overlapsInterval(Instant.parse("2025-01-05T00:00:00Z"), Instant.parse("2025-01-11T00:00:00Z")));

        assertTrue(temporal.startsAfter(LocalDate.of(2025, 1, 1)));
        assertTrue(temporal.endsAfter(LocalDate.of(2025, 1, 15)));
        assertTrue(temporal.endsAfterOrOn(LocalDate.of(2025, 1, 20)));
        assertTrue(temporal.startsBefore(LocalDate.of(2025, 1, 11)));
        assertTrue(temporal.startsBeforeOrOn(LocalDate.of(2025, 1, 10)));
        assertTrue(temporal.endsAfter(Instant.parse("2025-01-19T00:00:00Z")));
        assertTrue(temporal.endsAfterOrOn(end));
        assertTrue(temporal.startsBefore(Instant.parse("2025-01-10T00:00:01Z")));
        assertTrue(temporal.startsBeforeOrOn(start));
    }

    private record TestInstantTemporal(InstantInterval interval) implements InstantTemporal {
        @Override
        public InstantInterval getInterval() {
            return interval;
        }
    }
}


