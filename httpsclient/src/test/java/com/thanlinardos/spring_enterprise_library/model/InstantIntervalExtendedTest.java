package com.thanlinardos.spring_enterprise_library.model;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.TimeFactory;
import com.thanlinardos.spring_enterprise_library.time.model.InstantInterval;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CoreTest
class InstantIntervalExtendedTest {


    @Test
    void shouldCoverValidationFactoriesAndNullBoundaries() {
        assertTrue(InstantInterval.isNotValid(Instant.parse("2025-01-02T00:00:00Z"), Instant.parse("2025-01-01T00:00:00Z")));
        assertTrue(InstantInterval.isValid(Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-02T00:00:00Z")));
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), InstantInterval.forIsoMonth("2025-01").start());
        assertEquals(Instant.parse("2025-12-31T00:00:00Z"), InstantInterval.forIsoYear(2025).end());
        assertEquals(TimeFactory.getMinInstant(), new InstantInterval(null, Instant.parse("2025-01-20T00:00:00Z")).getStartNullAsMin());
        assertEquals(TimeFactory.getMaxInstant(), new InstantInterval(Instant.parse("2025-01-10T00:00:00Z"), null).getEndNullAsMax());
    }

    @Test
    void shouldCoverContainmentOverlapAndRelativeOperations() {
        InstantInterval interval = baseInterval();
        InstantInterval inside = insideInterval();
        InstantInterval overlap = overlapInterval();
        InstantInterval disjoint = disjointInterval();

        assertTrue(interval.containsNullAsMin(Instant.parse("2025-01-11T00:00:00Z")));
        assertFalse(interval.containsNullAsMax(Instant.parse("2025-01-09T00:00:00Z")));
        assertTrue(interval.contains(Instant.parse("2025-01-11T00:00:00Z")));
        assertFalse(interval.contains(java.time.YearMonth.of(2025, 1)));
        assertTrue(interval.contains(inside));
        assertTrue(interval.overlaps(overlap));
        assertFalse(interval.overlaps(disjoint));

        assertTrue(InstantInterval.anyOverlaps(List.of(interval, overlap)));
        assertEquals(List.of(overlap), interval.relativeComplement(List.of(interval, overlap)));
        assertTrue(interval.partiallyOverlaps(overlap));
    }

    @Test
    void shouldCoverOverlapAndBoundsApis() {
        InstantInterval interval = baseInterval();
        InstantInterval overlap = overlapInterval();

        Optional<InstantInterval> overlapResult = interval.getOverlap(overlap);
        assertTrue(overlapResult.isPresent());
        assertEquals(new InstantInterval(Instant.parse("2025-01-15T00:00:00Z"), Instant.parse("2025-01-20T00:00:00Z")), overlapResult.get());
        assertEquals(Optional.of(new InstantInterval(Instant.parse("2025-01-10T00:00:00Z"), Instant.parse("2025-01-18T00:00:00Z"))), interval.boundEnd(Instant.parse("2025-01-18T00:00:00Z")));
        assertEquals(Optional.of(new InstantInterval(Instant.parse("2025-01-12T00:00:00Z"), Instant.parse("2025-01-20T00:00:00Z"))), interval.boundStart(Instant.parse("2025-01-12T00:00:00Z")));
        assertEquals(baseInterval(), interval.boundStartDateIfValid(Instant.parse("2024-01-01T00:00:00Z")));
    }

    @Test
    void shouldCoverCollectionBasedOverlapAndSubtractApis() {
        InstantInterval interval = baseInterval();
        InstantInterval inside = insideInterval();
        InstantInterval overlap = overlapInterval();

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
        assertFalse(new InstantInterval(Instant.parse("2025-01-21T00:00:00Z"), Instant.parse("2025-01-25T00:00:00Z")).adjacent(
                new InstantInterval(Instant.parse("2025-01-10T00:00:00Z"), Instant.parse("2025-01-20T00:00:00Z"))));

        assertTrue(new InstantInterval(null, Instant.parse("2025-01-01T00:00:00Z")).compareTo(
                new InstantInterval(Instant.parse("2025-01-01T00:00:00Z"), Instant.parse("2025-01-02T00:00:00Z"))) < 0);
    }

    private static InstantInterval baseInterval() {
        return new InstantInterval(Instant.parse("2025-01-10T00:00:00Z"), Instant.parse("2025-01-20T00:00:00Z"));
    }

    private static InstantInterval insideInterval() {
        return new InstantInterval(Instant.parse("2025-01-12T00:00:00Z"), Instant.parse("2025-01-13T00:00:00Z"));
    }

    private static InstantInterval overlapInterval() {
        return new InstantInterval(Instant.parse("2025-01-15T00:00:00Z"), Instant.parse("2025-01-25T00:00:00Z"));
    }

    private static InstantInterval disjointInterval() {
        return new InstantInterval(Instant.parse("2025-02-01T00:00:00Z"), Instant.parse("2025-02-02T00:00:00Z"));
    }
}

