package com.thanlinardos.spring_enterprise_library.math;

import lombok.EqualsAndHashCode;

/**
 * Class representing an unsigned integer (UInt) with a range from 0 to 4294967295.
 * This class extends the Number class and provides methods to retrieve the value
 * in different numeric formats.
 */
@EqualsAndHashCode(callSuper = false)
public class UInt extends Number {

    /**
     * The minimum and maximum values for an unsigned integer.
     */
    public static final long MIN_VALUE = 0L;
    /**
     * The maximum value for an unsigned integer (2^32 - 1).
     */
    public static final long MAX_VALUE = 4294967295L;

    /**
     * The value of the unsigned integer.
     */
    private final long value;

    /**
     * Constructs a UInt with the specified value.
     *
     * @param value the unsigned integer value (must be between 0 and 4294967295)
     * @throws IllegalArgumentException if the value is out of range
     */
    public UInt(long value) {
        if (!isValueValid(value)) {
            throw new IllegalArgumentException("Value is not a valid unsigned integer: " + value);
        }
        this.value = value;
    }

    private boolean isValueValid(long value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    /**
     * Returns the value of this UInt as an int.
     *
     * @return the int value (may truncate if the value exceeds Integer.MAX_VALUE).
     */
    @Override
    public int intValue() {
        return (int) value;
    }

    /**
     * Returns the value of this UInt as a long.
     *
     * @return the long value.
     */
    @Override
    public long longValue() {
        return value;
    }

    /**
     * Returns the value of this UInt as a float.
     *
     * @return the float value.
     */
    @Override
    public float floatValue() {
        return value;
    }

    /**
     * Returns the value of this UInt as a double.
     *
     * @return the double value.
     */
    @Override
    public double doubleValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value + "U";
    }
}
