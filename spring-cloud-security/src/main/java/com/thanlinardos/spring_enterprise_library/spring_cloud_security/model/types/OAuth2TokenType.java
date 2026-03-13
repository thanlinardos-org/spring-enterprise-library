package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing different types of OAuth2 tokens.
 */
public enum OAuth2TokenType {

    JWT("JWT"),
    OPAQUE("OPAQUE");

    private final String value;

    /**
     * Constructor for OAuth2TokenType.
     *
     * @param value the string value of the token type
     */
    OAuth2TokenType(String value) {
        this.value = value;
    }

    /**
     * Returns the string value of the OAuth2TokenType.
     *
     * @return the string value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Creates an OAuth2TokenType enum from a string value.
     *
     * @param value the string value
     * @return the corresponding OAuth2TokenType enum
     * @throws IllegalArgumentException if the value does not correspond to any OAuth2TokenType
     */
    @JsonCreator
    public static OAuth2TokenType fromValue(String value) {
        for (OAuth2TokenType t : OAuth2TokenType.values()) {
            if (t.value.equals(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
