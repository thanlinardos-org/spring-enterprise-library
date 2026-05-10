package com.thanlinardos.spring_enterprise_library.time.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class DateTimeUtilsExtendedTest {


    @Test
    void shouldCoverParsingBoundariesComparisonsAndArithmetic() {
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0), DateTimeUtils.parseDateTime("2025-01-01T10:00:00"));
        assertNull(DateTimeUtils.parseDateTime("", LocalDateTime::parse));

        LocalDate date = LocalDate.of(2025, 1, 1);
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), DateTimeUtils.toStartOfDate(date));
        assertEquals(LocalDateTime.of(2025, 1, 1, 23, 59, 59, 999_000_000), DateTimeUtils.toEndOfDate(date));
        assertEquals(LocalDateTime.of(2025, 1, 1, 23, 59, 59), DateTimeUtils.toEndOfDate(date, TimeUnit.SECONDS));

        LocalDateTime dt1 = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime dt2 = LocalDateTime.of(2025, 1, 1, 11, 0);
        assertEquals(LocalDate.of(2025, 1, 1), DateTimeUtils.toDate(dt1));

        assertEquals(dt2, DateTimeUtils.maxNullAsMax(dt1, dt2));
        assertEquals(dt1, DateTimeUtils.minNullAsMin(dt1, dt2));
        assertEquals(dt2, DateTimeUtils.maxNullAsMin(dt1, dt2));
        assertEquals(dt1, DateTimeUtils.minNullAsMax(dt1, dt2));

        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0, 0, 1_000_000), DateTimeUtils.addDefault(dt1, 1));
        assertEquals(LocalDateTime.of(2025, 1, 1, 10, 0, 0, 1_000_000), DateTimeUtils.addSingle(dt1));
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 59, 59, 999_000_000), DateTimeUtils.subtractDefault(dt1, 1));
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 59, 59, 999_000_000), DateTimeUtils.subtractSingle(dt1));
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 0), DateTimeUtils.subtractDefault(dt1, 1, TimeUnit.HOURS));
        assertEquals(LocalDateTime.of(2025, 1, 1, 9, 59, 59), DateTimeUtils.subtractSingle(dt1, TimeUnit.SECONDS));

        assertTrue(DateTimeUtils.isBeforeOrEqual(dt1, dt1));
        assertTrue(DateTimeUtils.isAfterOrEqual(dt2, dt1));
        assertTrue(DateTimeUtils.isBeforeNullAsMin(null, dt1));
        assertTrue(DateTimeUtils.isAfterNullAsMax(null, dt1));
    }
}

