package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing the different types of REST operations, including CRUD.
 */
public enum OperationType {

    CREATE("CREATE"),
    ADD("ADD"),
    VIEW("VIEW"),
    UPDATE("UPDATE"),
    DELETE("DELETE");

    private final String value;

    OperationType(String value) {
        this.value = value;
    }

    /**
     * Gets the string value of the operation type.
     *
     * @return the string value of the operation type
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    /**
     * Creates an OperationType from a string value.
     *
     * @param value the string value
     * @return the corresponding OperationType
     * @throws IllegalArgumentException if the value does not correspond to any OperationType
     */
    @JsonCreator
    public static OperationType fromValue(String value) {
        for (OperationType b : OperationType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
