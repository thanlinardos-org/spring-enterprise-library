package com.thanlinardos.spring_enterprise_library.math;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import com.thanlinardos.spring_enterprise_library.math.utils.BackOffUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@CoreTest
class BackOffUtilsTest {

    private static Stream<Arguments> backoffParams() {
        return Stream.of(
                // retryCount=0 → 2^0 * step = 1 * step, capped at maxDelay
                Arguments.of(0, 2, 60, 2L),
                Arguments.of(0, 5, 60, 5L),
                // retryCount=1 → 2^1 * step
                Arguments.of(1, 2, 60, 4L),
                Arguments.of(1, 5, 60, 10L),
                // retryCount=2 → 2^2 * step
                Arguments.of(2, 2, 60, 8L),
                // retryCount=3 → 2^3 * step
                Arguments.of(3, 2, 60, 16L),
                // retryCount=4 → 2^4 * step = 32, not capped
                Arguments.of(4, 2, 60, 32L),
                // retryCount=5 → 2^5 * step = 64, capped at 60
                Arguments.of(5, 2, 60, 60L),
                // retryCount=10 → very large, always capped
                Arguments.of(10, 2, 60, 60L),
                // step=0 → always 0
                Arguments.of(5, 0, 60, 0L),
                // maxDelay=0 → always 0
                Arguments.of(0, 2, 0, 0L),
                // maxDelay equals computed value exactly
                Arguments.of(3, 2, 16, 16L)
        );
    }

    @ParameterizedTest
    @MethodSource("backoffParams")
    void getExponentialBackoffDelay_variousInputs_returnsExpectedDelay(
            int retryCount, int stepSize, int maxDelay, long expectedDelay) {
        long result = BackOffUtils.getExponentialBackoffDelay(retryCount, stepSize, maxDelay);
        Assertions.assertEquals(expectedDelay, result,
                String.format("retryCount=%d stepSize=%d maxDelay=%d", retryCount, stepSize, maxDelay));
    }

    @ParameterizedTest
    @MethodSource("backoffParams")
    void getExponentialBackoffDelay_neverExceedsMaxDelay(
            int retryCount, int stepSize, int maxDelay, long ignoredExpected) {
        long result = BackOffUtils.getExponentialBackoffDelay(retryCount, stepSize, maxDelay);
        Assertions.assertTrue(result <= maxDelay,
                String.format("result %d exceeds maxDelay %d", result, maxDelay));
    }
}

