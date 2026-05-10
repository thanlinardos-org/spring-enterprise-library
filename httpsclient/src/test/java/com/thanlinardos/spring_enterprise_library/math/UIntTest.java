package com.thanlinardos.spring_enterprise_library.math;

import com.thanlinardos.spring_enterprise_library.annotations.CoreTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.LongStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@CoreTest
class UIntTest {

    public static Stream<Arguments> rangeUIntFactory() {
        return LongStream.range(2, 100)
                .mapToObj(Arguments::of);
    }

    private static final long SAMPLE_VALUE = 42L;
    private static final long LARGE_VALUE = UInt.MAX_VALUE; // 4294967295

    @Test
    void intValue_smallValue_returnsCastInt() {
        UInt uInt = new UInt(SAMPLE_VALUE);
        assertEquals((int) SAMPLE_VALUE, uInt.intValue());
    }

    @Test
    void intValue_maxUIntValue_truncatesToInt() {
        // MAX_VALUE (4294967295) overflows int — should equal -1 via cast
        UInt uInt = new UInt(LARGE_VALUE);
        assertEquals((int) LARGE_VALUE, uInt.intValue());
    }

    @Test
    void longValue_returnsExactValue() {
        UInt uInt = new UInt(SAMPLE_VALUE);
        assertEquals(SAMPLE_VALUE, uInt.longValue());
    }

    @Test
    void floatValue_returnsFloatRepresentation() {
        UInt uInt = new UInt(SAMPLE_VALUE);
        assertEquals((float) SAMPLE_VALUE, uInt.floatValue(), 0.001f);
    }

    @Test
    void doubleValue_returnsDoubleRepresentation() {
        UInt uInt = new UInt(SAMPLE_VALUE);
        assertEquals((double) SAMPLE_VALUE, uInt.doubleValue(), 0.001);
    }

    @Test
    void toString_formatsWithUSuffix() {
        UInt uInt = new UInt(SAMPLE_VALUE);
        assertEquals("42U", uInt.toString());
    }

    @Test
    void toString_zero_formatsWithUSuffix() {
        UInt uInt = new UInt(UInt.MIN_VALUE);
        assertEquals("0U", uInt.toString());
    }

    @Test
    void equals_sameValue_returnsTrue() {
        UInt a = new UInt(SAMPLE_VALUE);
        UInt b = new UInt(SAMPLE_VALUE);
        assertEquals(a, b);
    }

    @Test
    void equals_differentValues_returnsFalse() {
        UInt a = new UInt(1L);
        UInt b = new UInt(2L);
        assertNotEquals(a, b);
    }

    @Test
    void hashCode_equalObjects_sameHashCode() {
        UInt a = new UInt(SAMPLE_VALUE);
        UInt b = new UInt(SAMPLE_VALUE);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0, 1, -1, -100, -4294967295L, UInt.MIN_VALUE, UInt.MAX_VALUE, 131435L})
    @MethodSource("rangeUIntFactory")
    void testUInt_zero(Long value) {
        if (value == null) {
            assertThrows(NullPointerException.class, () -> getUInt(null));
        } else if (value < UInt.MIN_VALUE || value > UInt.MAX_VALUE) {
            assertThrows(IllegalArgumentException.class, () -> getUInt(value));
        } else {
            UInt uInt = getUInt(value);
            assertEquals(value, uInt.longValue());
        }
    }

    private UInt getUInt(Long value) {
        return new UInt(value);
    }
}