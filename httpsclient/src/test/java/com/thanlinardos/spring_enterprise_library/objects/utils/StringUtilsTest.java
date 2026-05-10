package com.thanlinardos.spring_enterprise_library.objects.utils;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.error.errorcodes.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@CoreTest
class StringUtilsTest {

    // formatMessageWithArgs

    private static Stream<Arguments> formatMessageParams() {
        return Stream.of(
                // No placeholder → unchanged
                Arguments.of("Hello world", new Object[]{"arg"}, "Hello world"),
                // No args → unchanged
                Arguments.of("Hello {0}", new Object[]{}, "Hello {0}"),
                // Null args array → unchanged
                Arguments.of("Hello {0}", null, "Hello {0}"),
                // One arg substituted
                Arguments.of("Hello {0}", new Object[]{"World"}, "Hello World"),
                // Multiple args
                Arguments.of("{0} + {1} = {2}", new Object[]{1, 2, 3}, "1 + 2 = 3"),
                // Placeholder present but no args (empty) → unchanged
                Arguments.of("{0}", new Object[]{}, "{0}")
        );
    }

    @ParameterizedTest
    @MethodSource("formatMessageParams")
    void formatMessageWithArgs_variousInputs_returnsExpectedString(String message, Object[] args, String expected) {
        String result = StringUtils.formatMessageWithArgs(message, args);
        assertEquals(expected, result);
    }

    // formatErrorMessageWithArgs

    @Test
    void formatErrorMessageWithArgs_noPlaceholders_includesCodeAndName() {
        String result = StringUtils.formatErrorMessageWithArgs(
                ErrorCode.ILLEGAL_ARGUMENT, "Something went wrong", new Object[]{});

        // MessageFormat formats ints with locale grouping (e.g. 10000 → "10,000"),
        // so check for the name and message rather than the raw numeric code.
        assertTrue(result.contains("ILLEGAL_ARGUMENT"), "Should contain name");
        assertTrue(result.contains("Something went wrong"), "Should contain message");
        // The code is present (possibly with grouping separator)
        assertTrue(result.contains("10") && result.contains("000"), "Should contain code digits");
    }

    @Test
    void formatErrorMessageWithArgs_withPlaceholders_interpolatesArgs() {
        String result = StringUtils.formatErrorMessageWithArgs(
                ErrorCode.NONE_FOUND, "Entity {0} not found", new Object[]{"User"});

        assertTrue(result.contains("NONE_FOUND"));
        assertTrue(result.contains("Entity User not found"));
    }

    @Test
    void formatErrorMessageWithArgs_formatMatchesExpected() {
        // MessageFormat uses locale-dependent number formatting (e.g. 10010 → "10,010").
        // Build the expected string the same way so the test is locale-independent.
        String formattedCode = java.text.MessageFormat.format("{0}", ErrorCode.VALIDATION_ERROR.getCode());
        String expected = "[" + formattedCode + "-VALIDATION_ERROR] invalid input";

        String result = StringUtils.formatErrorMessageWithArgs(
                ErrorCode.VALIDATION_ERROR, "invalid input", new Object[]{});

        assertEquals(expected, result);
    }
}


