package com.thanlinardos.spring_enterprise_library.model;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.model.Interval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class IntervalExtendedTest {


    @Test
    void shouldCoverValidationFactoriesAndNullBoundaries() {
        assertTrue(Interval.isNotValid(LocalDate.of(2025, 1, 2), LocalDate.of(2025, 1, 1)));
        assertTrue(Interval.isValid(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2)));
        assertEquals(LocalDate.of(2025, 1, 1), Interval.forIsoMonth("2025-01").start());
        assertEquals(LocalDate.of(2025, 12, 31), Interval.forIsoYear(2025).end());
        assertEquals(TimeFactory.getMinDate(), new Interval(null, LocalDate.of(2025, 1, 20)).getStartNullAsMin());
        assertEquals(TimeFactory.getMaxDate(), new Interval(LocalDate.of(2025, 1, 10), null).getEndNullAsMax());
    }

    @Test
    void shouldCoverContainmentOverlapAndRelativeOperations() {
        Interval interval = baseInterval();
        Interval inside = insideInterval();
        Interval overlap = overlapInterval();
        Interval disjoint = disjointInterval();

        assertTrue(interval.containsNullAsMin(LocalDate.of(2025, 1, 11)));
        assertFalse(interval.containsNullAsMax(LocalDate.of(2025, 1, 9)));
        assertTrue(interval.contains(LocalDate.of(2025, 1, 11)));
        assertFalse(interval.contains(java.time.YearMonth.of(2025, 1)));
        assertTrue(interval.contains(inside));
        assertTrue(interval.overlaps(overlap));
        assertFalse(interval.overlaps(disjoint));

        assertTrue(Interval.anyOverlaps(List.of(interval, overlap)));
        assertEquals(List.of(overlap), interval.relativeComplement(List.of(interval, overlap)));
        assertTrue(interval.partiallyOverlaps(overlap));
    }

    @Test
    void shouldCoverOverlapAndBoundsApis() {
        Interval interval = baseInterval();
        Interval overlap = overlapInterval();

        Optional<Interval> overlapResult = interval.getOverlap(overlap);
        assertTrue(overlapResult.isPresent());
        assertEquals(new Interval(LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 20)), overlapResult.get());
        assertEquals(Optional.of(new Interval(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 18))), interval.boundEndDate(LocalDate.of(2025, 1, 18)));
        assertEquals(Optional.of(new Interval(LocalDate.of(2025, 1, 12), LocalDate.of(2025, 1, 20))), interval.boundStartDate(LocalDate.of(2025, 1, 12)));
        assertEquals(baseInterval(), interval.boundStartDateIfValid(LocalDate.of(2024, 1, 1)));
    }

    @Test
    void shouldCoverCollectionBasedOverlapAndSubtractApis() {
        Interval interval = baseInterval();
        Interval inside = insideInterval();
        Interval overlap = overlapInterval();

        List<Interval> overlaps = interval.getOverlaps(List.of(inside, overlap));
        assertFalse(overlaps.isEmpty());
        assertFalse(interval.getOverlaps(List.of(inside, overlap), false).isEmpty());
        assertFalse(interval.getOverlaps(inside, overlap).isEmpty());
        assertFalse(interval.getNotOverlaps(List.of(inside, overlap)).isEmpty());
        assertFalse(interval.getNotOverlaps(inside, overlap).isEmpty());
        assertFalse(interval.subtract(inside).isEmpty());
        assertFalse(interval.subtract(List.of(inside, overlap)).isEmpty());
    }

    @Test
    void shouldCoverAdjacencyAndCompareTo() {
        assertFalse(new Interval(LocalDate.of(2025, 1, 21), LocalDate.of(2025, 1, 25)).adjacent(
                new Interval(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20))));

        assertTrue(new Interval(null, LocalDate.of(2025, 1, 1)).compareTo(new Interval(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2))) < 0);
    }

    private static Interval baseInterval() {
        return new Interval(LocalDate.of(2025, 1, 10), LocalDate.of(2025, 1, 20));
    }

    private static Interval insideInterval() {
        return new Interval(LocalDate.of(2025, 1, 12), LocalDate.of(2025, 1, 13));
    }

    private static Interval overlapInterval() {
        return new Interval(LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 25));
    }

    private static Interval disjointInterval() {
        return new Interval(LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 2));
    }
}



