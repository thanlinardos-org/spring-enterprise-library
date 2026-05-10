package com.thanlinardos.spring_enterprise_library.parse.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@CoreTest
class ParserUtilTest {

    @Test
    void numberParsers_shouldHandleValidInvalidAndNullValues() {
        assertEquals(5, ParserUtil.safeParseInteger("5"));
        assertNull(ParserUtil.safeParseInteger("x"));
        assertNull(ParserUtil.safeParseInteger(null));

        assertEquals(1.5f, ParserUtil.safeParseFloat("1.5"));
        assertNull(ParserUtil.safeParseFloat("x"));

        assertEquals(2.5d, ParserUtil.safeParseDouble("2.5"));
        assertNull(ParserUtil.safeParseDouble("x"));

        assertEquals(123L, ParserUtil.safeParseLong("123"));
        assertNull(ParserUtil.safeParseLong("x"));
    }

    @Test
    void safeParseOffsetDateTime_shouldParseOrReturnNull() {
        OffsetDateTime parsed = ParserUtil.safeParseOffsetDateTime("2025-01-01T10:15:30+01:00");

        assertNotNull(parsed);
        assertNull(ParserUtil.safeParseOffsetDateTime("not-a-date"));
        assertNull(ParserUtil.safeParseOffsetDateTime(null));
    }

    @Test
    void safeParseUUID_fromString_shouldParseOrReturnNull() {
        UUID uuid = UUID.randomUUID();

        assertEquals(uuid, ParserUtil.safeParseUUID(uuid.toString()));
        assertNull(ParserUtil.safeParseUUID("not-a-uuid"));
        assertNull(ParserUtil.safeParseUUID((String) null));
    }

    @Test
    void safeParseBoolean_shouldRespectNullAndBooleanParsing() {
        assertTrue(ParserUtil.safeParseBoolean("true"));
        assertFalse(ParserUtil.safeParseBoolean("FALSE"));
        assertNull(ParserUtil.safeParseBoolean(null));
    }

    @Test
    void objectParsers_shouldHandleStringUuidOptionalAndLists() {
        UUID uuid = UUID.randomUUID();

        assertEquals("42", ParserUtil.safeParseString(42));
        assertNull(ParserUtil.safeParseString(null));

        assertEquals(uuid, ParserUtil.safeParseUUID(uuid));
        assertNull(ParserUtil.safeParseUUID(new Object()));
        assertNull(ParserUtil.safeParseUUID((Object) null));

        assertEquals(Optional.of(uuid), ParserUtil.safeParseOptionalUUID(uuid));
        assertEquals(Optional.empty(), ParserUtil.safeParseOptionalUUID("not-a-uuid"));
        assertEquals(Optional.empty(), ParserUtil.safeParseOptionalUUID(null));

        assertEquals(Arrays.asList("a", null, "7"), ParserUtil.safeParseListString(Arrays.asList("a", null, 7)));
    }

    @Test
    void getPathParameterFromLocationURI_shouldReturnLastSegment() {
        Response response = mock(Response.class);
        when(response.getLocation()).thenReturn(URI.create("https://api.local/items/abc-123"));

        assertEquals("abc-123", ParserUtil.getPathParameterFromLocationURI(response));
    }
}


