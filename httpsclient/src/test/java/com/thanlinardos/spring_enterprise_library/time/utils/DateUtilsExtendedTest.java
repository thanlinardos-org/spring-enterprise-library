package com.thanlinardos.spring_enterprise_library.time.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class DateUtilsExtendedTest {


    @Test
    void shouldCoverDateParsingComparisonsAndTransformations() {
        assertEquals(LocalDate.of(2024, 12, 31), DateUtils.getLastDayOfYear(Year.of(2024)));

        long epochMilli = 1735689600000L;
        assertEquals(LocalDateTime.of(2025, 1, 1, 0, 0), DateUtils.getLocalDateTimeFromEpochMilli(epochMilli));
        assertEquals(LocalDate.of(2025, 1, 1), DateUtils.getLocalDateFromEpochMilli(epochMilli));
        assertEquals(ZoneOffset.UTC, DateUtils.epochMilliToZonedDateTime(epochMilli).getOffset());

        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 1, 12, 30);
        assertEquals(1735734600000L, DateUtils.getEpochMilliFromLocalDateTime(dateTime));

        LocalDate d1 = LocalDate.of(2025, 1, 1);
        LocalDate d2 = LocalDate.of(2025, 1, 2);
        assertEquals(d2, DateUtils.maxNullAsMax(d1, d2));
        assertEquals(d1, DateUtils.minNullAsMin(d1, d2));
        assertEquals(d2, DateUtils.maxNullAsMin(d1, d2));
        assertEquals(d1, DateUtils.minNullAsMax(d1, d2));

        assertNull(DateUtils.addDays(null, 1));
        assertEquals(LocalDate.of(2025, 1, 3), DateUtils.addDays(d2, 1));
        assertEquals(LocalDate.of(2025, 1, 3), DateUtils.addDay(d1.plusDays(1)));
        assertEquals(LocalDate.of(2025, 1, 1), DateUtils.subtractDay(d2));

        assertEquals(LocalDate.of(2025, 5, 1), DateUtils.parseLocalDate("2025-05-01"));
        assertNull(DateUtils.parseDate("", LocalDate::parse));
        assertEquals(LocalDate.of(2025, 6, 1), DateUtils.parseDate("x", s -> LocalDate.of(2025, 6, 1)));
    }
}


