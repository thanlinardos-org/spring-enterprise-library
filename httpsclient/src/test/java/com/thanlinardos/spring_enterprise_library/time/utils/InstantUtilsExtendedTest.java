package com.thanlinardos.spring_enterprise_library.time.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class InstantUtilsExtendedTest {


    @Test
    void shouldCoverParsingBoundariesComparisonsAndArithmetic() {
        Instant instant = Instant.parse("2025-01-01T10:00:00Z");
        assertEquals(instant, InstantUtils.parseInstant("2025-01-01T10:00:00Z"));
        assertNull(InstantUtils.parseInstant("", Instant::parse));

        LocalDate date = LocalDate.of(2025, 1, 1);
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), InstantUtils.toStartOfDate(date));
        assertEquals(Instant.parse("2025-01-01T00:00:00Z"), InstantUtils.toStartOfDate(date, ZoneOffset.UTC));
        assertEquals(Instant.parse("2025-01-01T23:59:59.999Z"), InstantUtils.toEndOfLocalDate(date));
        assertEquals(Instant.parse("2025-01-01T23:59:59Z"), InstantUtils.toEndOfLocalDate(date, TimeUnit.SECONDS, ZoneOffset.UTC));

        assertEquals(date, InstantUtils.toLocalDate(instant));

        Instant later = instant.plusSeconds(60);
        assertEquals(later, InstantUtils.maxNullAsMax(instant, later));
        assertEquals(instant, InstantUtils.minNullAsMin(instant, later));
        assertEquals(later, InstantUtils.maxNullAsMin(instant, later));
        assertEquals(instant, InstantUtils.minNullAsMax(instant, later));

        assertEquals(instant.plusMillis(1), InstantUtils.addDefault(instant, 1));
        assertEquals(instant.plusMillis(1), InstantUtils.addSingle(instant));
        assertEquals(instant.minusMillis(1), InstantUtils.subtractDefault(instant, 1));
        assertEquals(instant.minusMillis(1), InstantUtils.subtractSingle(instant));
        assertEquals(instant.minusSeconds(1), InstantUtils.subtractDefault(instant, 1, TimeUnit.SECONDS));
        assertEquals(instant.minusSeconds(1), InstantUtils.subtractSingle(instant, TimeUnit.SECONDS));

        assertTrue(InstantUtils.isBeforeOrEqual(instant, instant));
        assertTrue(InstantUtils.isAfterOrEqual(later, instant));
        assertTrue(InstantUtils.isBeforeNullAsMin(null, instant));
        assertTrue(InstantUtils.isAfterNullAsMax(null, instant));
    }
}

