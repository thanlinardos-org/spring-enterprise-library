package com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.certs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClientCertificateType {

    KEYCLOAK("KEYCLOAK");

    private final String value;

    ClientCertificateType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static ClientCertificateType fromValue(String value) {
        for (ClientCertificateType b : ClientCertificateType.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
