package com.thanlinardos.spring_enterprise_library.spring_cloud_security.constants;

import java.util.List;

/**
 * A utility class that holds common security-related constants.
 */
public class SecurityCommonConstants {

    private SecurityCommonConstants() {
    }

    /**
     * Prefix used for role names in the security context.
     */
    public static final String ROLE_PREFIX = "ROLE_";

    /**
     * List of public URLs that allow read operations (e.g., GET requests).
     */
    public static final List<String> PUBLIC_READ_URLS = List.of(
            "/assets/**",
            "/error",
            "/login/**",
            "/invalidSession",
            "/contacts",
            "/home",
            "/",
            "/notices",
            "/favicon.ico",
            "/h2-console/**",
            "/actuator/**"
    );

    /**
     * List of public URLs that allow write operations (e.g., POST requests).
     */
    public static final List<String> PUBLIC_WRITE_URLS = List.of(
            "/contact",
            "/saveMsg",
            "/customers",
            "/actuator/**"
    );

    /**
     * The maximum privilege level that can access the resource.
     */
    public static final Integer MAX_PRIVILEGE_LEVEL = Integer.MAX_VALUE;
}
