package com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing different types of OAuth2 authentication servers.
 */
public enum OAuth2AuthServerType {

    KEYCLOAK("KEYCLOAK"),
    SPRING_OAUTH2_SERVER("SPRING_OAUTH2_SERVER");

    private final String value;

    /**
     * Constructor for OAuth2AuthServerType.
     *
     * @param value the string value of the authentication server type
     */
    OAuth2AuthServerType(String value) {
        this.value = value;
    }

    /**
     * Returns the string value of the OAuth2AuthServerType.
     *
     * @return the string value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Creates an OAuth2AuthServerType enum from a string value.
     *
     * @param value the string value
     * @return the corresponding OAuth2AuthServerType enum
     * @throws IllegalArgumentException if the value does not correspond to any OAuth2AuthServerType
     */
    @JsonCreator
    public static OAuth2AuthServerType fromValue(String value) {
        for (OAuth2AuthServerType t : OAuth2AuthServerType.values()) {
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
