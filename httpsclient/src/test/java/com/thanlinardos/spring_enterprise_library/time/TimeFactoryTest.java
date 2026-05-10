package com.thanlinardos.spring_enterprise_library.time;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.time.api.TimeProvider;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class TimeFactoryTest {

    @Test
    void staticMethods_shouldExposeConfiguredProviderValues() {
        TimeProvider provider = new TimeProviderImpl(
                ZoneOffset.UTC,
                TimeUnit.MILLISECONDS,
                LocalDate.of(2999, 12, 31),
                LocalDate.of(1900, 1, 1),
                LocalDateTime.of(2999, 12, 31, 23, 59, 59),
                LocalDateTime.of(1900, 1, 1, 0, 0)
        );
        new TimeFactory(provider);

        assertEquals(TimeUnit.MILLISECONDS, TimeFactory.getAccuracy());
        assertEquals(LocalDate.of(2999, 12, 31), TimeFactory.getMaxDate());
        assertEquals(LocalDate.of(1900, 1, 1), TimeFactory.getMinDate());
        assertEquals(LocalDateTime.of(2999, 12, 31, 23, 59, 59), TimeFactory.getMaxDateTime());
        assertEquals(LocalDateTime.of(1900, 1, 1, 0, 0), TimeFactory.getMinDateTime());

        assertEquals(ZoneOffset.UTC, TimeFactory.getDefaultZone());
        assertEquals(ZoneOffset.UTC, TimeFactory.getDefaultZoneId());
        assertEquals(LocalDateTime.of(2999, 12, 31, 23, 59, 59).toInstant(ZoneOffset.UTC), TimeFactory.getMaxInstant());
        assertEquals(LocalDateTime.of(1900, 1, 1, 0, 0).toInstant(ZoneOffset.UTC), TimeFactory.getMinInstant());

        assertNotNull(TimeFactory.getDate());
        assertNotNull(TimeFactory.getDateTime());
        assertNotNull(TimeFactory.getInstant());
    }

    @Test
    void staticMethods_whenNotInitialized_shouldThrow() throws Exception {
        Field field = TimeFactory.class.getDeclaredField("timeProvider");
        field.setAccessible(true);
        Object previous = field.get(null);

        try {
            field.set(null, null);
            IllegalStateException exception = assertThrows(IllegalStateException.class, TimeFactory::getDate);
            assertTrue(exception.getMessage().contains("TimeFactory has not been initialized yet"));
        } finally {
            field.set(null, previous);
        }
    }
}

