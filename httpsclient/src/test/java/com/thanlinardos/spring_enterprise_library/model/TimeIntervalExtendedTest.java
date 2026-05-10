package com.thanlinardos.spring_enterprise_library.model;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.model.TimeInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class TimeIntervalExtendedTest {

    @Test
    void shouldCoverValidationFactoriesAndNullBoundaries() {
        assertTrue(TimeInterval.isNotValid(LocalDateTime.of(2025, 1, 2, 0, 0), LocalDateTime.of(2025, 1, 1, 0, 0)));
        assertTrue(TimeInterval.isValid(LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 2, 0, 0)));
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), TimeInterval.forIsoMonth("2025-01").start());
        assertEquals(LocalDateTime.of(2025, 12, 31, 0, 0), TimeInterval.forIsoYear(2025).end());
        assertEquals(TimeFactory.getMinDateTime(), new TimeInterval(null, LocalDateTime.of(2025, 1, 20, 0, 0)).getStartNullAsMin());
        assertEquals(TimeFactory.getMaxDateTime(), new TimeInterval(LocalDateTime.of(2025, 1, 10, 0, 0), null).getEndNullAsMax());
    }

    @Test
    void shouldCoverContainmentOverlapAndRelativeOperations() {
        TimeInterval interval = baseInterval();
        TimeInterval inside = insideInterval();
        TimeInterval overlap = overlapInterval();
        TimeInterval disjoint = disjointInterval();

        assertTrue(interval.containsNullAsMin(LocalDateTime.of(2025, 1, 11, 0, 0)));
        assertFalse(interval.containsNullAsMax(LocalDateTime.of(2025, 1, 9, 0, 0)));
        assertTrue(interval.contains(LocalDateTime.of(2025, 1, 11, 0, 0)));
        assertFalse(interval.contains(java.time.YearMonth.of(2025, 1)));
        assertTrue(interval.contains(inside));
        assertTrue(interval.overlaps(overlap));
        assertFalse(interval.overlaps(disjoint));
        assertTrue(TimeInterval.anyOverlaps(List.of(interval, overlap)));
        assertEquals(List.of(overlap), interval.relativeComplement(List.of(interval, overlap)));
        assertTrue(interval.partiallyOverlaps(overlap));
    }

    @Test
    void shouldCoverOverlapAndBoundsApis() {
        TimeInterval interval = baseInterval();
        TimeInterval overlap = overlapInterval();

        Optional<TimeInterval> overlapResult = interval.getOverlap(overlap);
        assertTrue(overlapResult.isPresent());
        assertEquals(new TimeInterval(LocalDateTime.of(2025, 1, 15, 0, 0), LocalDateTime.of(2025, 1, 20, 0, 0)), overlapResult.get());
        assertEquals(Optional.of(new TimeInterval(LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 1, 18, 0, 0))), interval.boundEnd(LocalDateTime.of(2025, 1, 18, 0, 0)));
        assertEquals(Optional.of(new TimeInterval(LocalDateTime.of(2025, 1, 12, 0, 0), LocalDateTime.of(2025, 1, 20, 0, 0))), interval.boundStart(LocalDateTime.of(2025, 1, 12, 0, 0)));
        assertEquals(baseInterval(), interval.boundStartDateIfValid(LocalDateTime.of(2024, 1, 1, 0, 0)));
    }

    @Test
    void shouldCoverCollectionBasedOverlapAndSubtractApis() {
        TimeInterval interval = baseInterval();
        TimeInterval inside = insideInterval();
        TimeInterval overlap = overlapInterval();

        assertFalse(interval.getOverlaps(List.of(inside, overlap)).isEmpty());
        assertFalse(interval.getOverlaps(List.of(inside, overlap), false).isEmpty());
        assertFalse(interval.getOverlaps(inside, overlap).isEmpty());
        assertFalse(interval.getNotOverlaps(List.of(inside, overlap)).isEmpty());
        assertFalse(interval.getNotOverlaps(inside, overlap).isEmpty());
        assertFalse(interval.subtract(inside).isEmpty());
        assertFalse(interval.subtract(List.of(inside, overlap)).isEmpty());
    }

    @Test
    void shouldCoverAdjacencyAndCompareTo() {
        assertFalse(new TimeInterval(LocalDateTime.of(2025, 1, 21, 0, 0), LocalDateTime.of(2025, 1, 25, 0, 0)).adjacent(
                new TimeInterval(LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 1, 20, 0, 0))));
        assertTrue(new TimeInterval(null, LocalDateTime.of(2025, 1, 1, 0, 0)).compareTo(
                new TimeInterval(LocalDateTime.of(2025, 1, 1, 0, 0), LocalDateTime.of(2025, 1, 2, 0, 0))) < 0);
    }

    private static TimeInterval baseInterval() {
        return new TimeInterval(LocalDateTime.of(2025, 1, 10, 0, 0), LocalDateTime.of(2025, 1, 20, 0, 0));
    }

    private static TimeInterval insideInterval() {
        return new TimeInterval(LocalDateTime.of(2025, 1, 12, 0, 0), LocalDateTime.of(2025, 1, 13, 0, 0));
    }

    private static TimeInterval overlapInterval() {
        return new TimeInterval(LocalDateTime.of(2025, 1, 15, 0, 0), LocalDateTime.of(2025, 1, 25, 0, 0));
    }

    private static TimeInterval disjointInterval() {
        return new TimeInterval(LocalDateTime.of(2025, 2, 1, 0, 0), LocalDateTime.of(2025, 2, 2, 0, 0));
    }
}

