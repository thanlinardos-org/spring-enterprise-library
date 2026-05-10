package com.thanlinardos.spring_enterprise_library.https.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class RestControllerUtilsTest {

    @Test
    void okWithBodyOrNotFound_whenBodyExists_shouldReturnOk() {
        ResponseEntity<String> response = RestControllerUtils.okWithBodyOrNotFound("payload");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("payload", response.getBody());
    }

    @Test
    void okWithBodyOrNotFound_whenBodyMissing_shouldReturnNotFound() {
        ResponseEntity<String> response = RestControllerUtils.okWithBodyOrNotFound(null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}

