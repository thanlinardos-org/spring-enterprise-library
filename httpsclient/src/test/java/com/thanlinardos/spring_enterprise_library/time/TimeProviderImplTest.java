package com.thanlinardos.spring_enterprise_library.time;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.model.InstantInterval;
import com.thanlinardos.spring_enterprise_library.time.model.TimeInterval;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class TimeProviderImplTest {

    private final TimeProviderImpl provider = new TimeProviderImpl(
            ZoneOffset.UTC,
            TimeUnit.MILLISECONDS,
            LocalDate.of(2999, 12, 31),
            LocalDate.of(1900, 1, 1),
            LocalDateTime.of(2999, 12, 31, 23, 59, 59),
            LocalDateTime.of(1900, 1, 1, 0, 0)
    );

    @Test
    void conversionAndMetadataMethods_shouldReturnExpectedValues() {
        assertEquals(ZoneOffset.UTC, provider.getDefaultZone());
        assertEquals(ChronoUnit.MILLIS, provider.getChronoAccuracy());

        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 1, 12, 0);
        long millis = provider.toMillis(dateTime);
        assertEquals(dateTime, provider.fromMillis(millis));

        assertNotNull(provider.getCurrentDateTime());
        assertNotNull(provider.getCurrentInstant());
        assertNotNull(provider.getCurrentDate());
        assertTrue(provider.getCurrentTimeMillis() > 0);
    }

    @Test
    void dayQuarterYearMethods_shouldComputeBoundaries() {
        LocalDate date = LocalDate.of(2025, 5, 15);

        assertEquals(LocalDateTime.of(2025, 5, 15, 0, 0), provider.getStartOfDay(date));
        assertEquals(LocalDateTime.of(2025, 5, 15, 23, 59, 59), provider.getEndOfDay(date));

        assertEquals(LocalDate.of(2025, 4, 1), provider.getFirstDayOfQuarter(date));
        assertEquals(LocalDate.of(2025, 6, 30), provider.getLastDayOfQuarter(date));

        assertEquals(LocalDate.of(2025, 1, 1), provider.getFirstDayOfYear(date));
        assertEquals(LocalDate.of(2025, 12, 31), provider.getLastDayOfYear(date));
    }

    @Test
    void fromNowMethods_shouldCreateForwardIntervals() {
        InstantInterval instantInterval = provider.instantFromNowPlusSeconds(5);
        TimeInterval timeInterval = provider.timeFromNowPlusSeconds(5);

        assertNotNull(instantInterval.start());
        assertNotNull(instantInterval.end());
        assertEquals(5, instantInterval.end().getEpochSecond() - instantInterval.start().getEpochSecond());

        assertNotNull(timeInterval.start());
        assertNotNull(timeInterval.end());
        assertEquals(5, ChronoUnit.SECONDS.between(timeInterval.start(), timeInterval.end()));
    }
}

