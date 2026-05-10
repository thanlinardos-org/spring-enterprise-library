package com.thanlinardos.spring_enterprise_library.error;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.error.utils.ExceptionUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class ExceptionUtilsTest {

    @Test
    void getStackTrace_nullThrowable_returnsEmptyString() {
        String result = ExceptionUtils.getStackTrace(null);
        assertEquals("", result);
    }

    @Test
    void getStackTrace_simpleException_containsExceptionMessage() {
        RuntimeException ex = new RuntimeException("test error message");
        String result = ExceptionUtils.getStackTrace(ex);

        assertFalse(result.isEmpty(), "Expected non-empty stack trace");
        assertTrue(result.contains("test error message"),
                "Stack trace should contain the exception message");
        assertTrue(result.contains("RuntimeException"),
                "Stack trace should contain the exception class");
    }

    @Test
    void getStackTrace_exceptionWithCause_containsBothMessages() {
        Exception cause = new IllegalArgumentException("root cause");
        RuntimeException ex = new RuntimeException("wrapper message", cause);

        String result = ExceptionUtils.getStackTrace(ex);

        assertTrue(result.contains("wrapper message"));
        assertTrue(result.contains("root cause"));
        assertTrue(result.contains("IllegalArgumentException"));
    }

    @Test
    void getStackTrace_exceptionWithSuppressed_containsSuppressedInfo() {
        RuntimeException ex = new RuntimeException("main error");
        ex.addSuppressed(new IllegalStateException("suppressed error"));

        String result = ExceptionUtils.getStackTrace(ex);

        assertTrue(result.contains("main error"));
        // Suppressed exceptions appear in the stack trace output
        assertTrue(result.contains("suppressed error") || result.contains("Suppressed"));
    }

    @Test
    void getStackTrace_customException_containsClassName() {
        CoreTestException ex = new CoreTestException("custom message");
        String result = ExceptionUtils.getStackTrace(ex);

        assertTrue(result.contains("CoreTestException"));
        assertTrue(result.contains("custom message"));
    }

    // Tiny helper exception to verify arbitrary class names are printed
    private static class CoreTestException extends RuntimeException {
        CoreTestException(String message) {
            super(message);
        }
    }
}

