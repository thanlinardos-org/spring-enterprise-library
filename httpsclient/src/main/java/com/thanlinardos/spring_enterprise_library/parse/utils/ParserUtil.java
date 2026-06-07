package com.thanlinardos.spring_enterprise_library.parse.utils;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A utility class for safely parsing various data types from strings and objects.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParserUtil {

    /**
     * Safely parses a String to an Integer.
     *
     * @param str the String to parse.
     * @return the parsed Integer, or null if parsing fails.
     */
    public static Integer safeParseInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException | NullPointerException _) {
            return null;
        }
    }

    /**
     * Safely parses a String to a Float.
     *
     * @param str the String to parse.
     * @return the parsed Float, or null if parsing fails.
     */
    public static Float safeParseFloat(String str) {
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException | NullPointerException _) {
            return null;
        }
    }

    /**
     * Safely parses a String to a Double.
     *
     * @param str the String to parse.
     * @return the parsed Double, or null if parsing fails.
     */
    public static Double safeParseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException | NullPointerException _) {
            return null;
        }
    }

    /**
     * Safely parses a String to an OffsetDateTime.
     *
     * @param str the String to parse.
     * @return the parsed OffsetDateTime, or null if parsing fails.
     */
    public static OffsetDateTime safeParseOffsetDateTime(String str) {
        try {
            return OffsetDateTime.parse(str);
        } catch (DateTimeParseException | NullPointerException _) {
            return null;
        }
    }

    /**
     * Safely parses a String to a UUID.
     *
     * @param str the String to parse.
     * @return the parsed UUID, or null if parsing fails.
     */
    public static UUID safeParseUUID(String str) {
        try {
            return UUID.fromString(str);
        } catch (IllegalArgumentException | NullPointerException _) {
            return null;
        }
    }

    /**
     * Safely parses a String to a Boolean.
     *
     * @param value the String to parse.
     * @return the parsed Boolean, or null if the input is null.
     */
    public static Boolean safeParseBoolean(String value) {
        return Optional.ofNullable(value)
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    /**
     * Safely converts an Object to a String.
     *
     * @param o the Object to convert.
     * @return the String representation of the Object, or null if the Object is null.
     */
    public static String safeParseString(Object o) {
        if (o == null) {
            return null;
        }

        return o.toString();
    }

    /**
     * Safely converts an Object to a UUID.
     *
     * @param o the Object to convert.
     * @return the UUID representation of the Object, or null if the Object is null or cannot be parsed as a UUID.
     */
    public static UUID safeParseUUID(Object o) {
        if (o == null) {
            return null;
        }
        try {
            return UUID.fromString(o.toString());
        } catch (IllegalArgumentException _) {
            return null;
        }
    }

    /**
     * Safely converts an Object to an Optional UUID.
     *
     * @param o the Object to convert.
     * @return an Optional containing the UUID representation of the Object, or an empty Optional if the Object is null or cannot be parsed as a UUID.
     */
    public static Optional<UUID> safeParseOptionalUUID(Object o) {
        if (o == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(o.toString()));
        } catch (IllegalArgumentException _) {
            return Optional.empty();
        }
    }

    /**
     * Safely parses a List of Objects to a List of Strings.
     *
     * @param list the List of Objects to parse.
     * @return a List of Strings, with each Object converted to a String. If an Object is null, it will be represented as null in the resulting List.
     */
    public static List<String> safeParseListString(List<Object> list) {
        List<String> res = new ArrayList<>();
        for (Object o : list) {
            res.add(safeParseString(o));
        }
        return res;
    }

    /**
     * Safely parses a String to a Long.
     *
     * @param str the String to parse.
     * @return the parsed Long, or null if parsing fails.
     */
    public static Long safeParseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException _) {
            return null;
        }
    }

    /**
     * Extracts the last path parameter from the Location URI in a JAX-RS Response.
     *
     * @param response the JAX-RS Response containing the Location URI.
     * @return the last path parameter as a String, or null if the path is empty.
     */
    public static String getPathParameterFromLocationURI(Response response) {
        String[] segments = response.getLocation().getPath().split("/");
        if (segments.length == 0) {
            return null;
        }
        return segments[segments.length - 1];
    }
}
