package com.thanlinardos.spring_enterprise_library.error.errorcodes;

import com.thanlinardos.spring_enterprise_library.error.exceptions.CoreException;
import lombok.Getter;

import javax.net.ssl.SSLContext;
import java.util.logging.Level;

/**
 * Enumeration of error codes used in the application.
 * Each error code has a name, numeric code, and severity level.
 */
@Getter
public enum ErrorCode {

    /**
     * Error code indicating an illegal argument was provided.
     */
    ILLEGAL_ARGUMENT("ILLEGAL_ARGUMENT", 10000, Level.SEVERE),
    /**
     * Error code indicating that no results were found when at least one was expected.
     */
    NONE_FOUND("NONE_FOUND", 10001, Level.SEVERE),
    /**
     * Error code indicating that more than one result was found when only one was expected.
     */
    MORE_THAN_ONE_FOUND("MORE_THAN_ONE_FOUND", 10002, Level.SEVERE),
    /**
     * Error code indicating that a list was expected to be non-empty but was empty.
     */
    EMPTY_LIST("EMPTY_LIST", 10003, Level.SEVERE),
    /**
     * Error code indicating that a list was expected to be empty but was not.
     */
    NON_EMPTY_LIST("NON_EMPTY_LIST", 10004, Level.SEVERE),
    /**
     * Error code indicating that two lists were expected to be of the same size but were not.
     */
    LIST_NOT_SAME_SIZE("LIST_NOT_SAME_SIZE", 10005, Level.SEVERE),
    /**
     * Error code indicating an unexpected error occurred.
     */
    UNEXPECTED_ERROR("UNEXPECTED_ERROR", 10006, Level.SEVERE),
    /** Error code indicating an error with creating an {@link SSLContext}. */
    CREATE_SSL_CONTEXT_ERROR("CREATE_SSL_CONTEXT_ERROR", 10007, Level.SEVERE),
    CONFIG_PROPERTY_NOT_FOUND("CONFIG_PROPERTY_NOT_FOUND", 10008, Level.SEVERE),
    INVALID_EVENT_CLASS_INSTANCE("INVALID_EVENT_CLASS_INSTANCE", 10009, Level.SEVERE);


    private final String name;
    private final int code;
    private final Level level;

    /**
     * Constructor for ErrorCode enum.
     *
     * @param name  the name of the error code
     * @param code  the numeric code of the error
     * @param level the severity level of the error
     */
    ErrorCode(String name, int code, Level level) {
        this.name = name;
        this.code = code;
        this.level = level;
    }

    /**
     * Creates a CoreException with this error code and the given message.
     *
     * @param message the error message
     * @return a CoreException instance
     */
    public CoreException createCoreException(String message) {
        return new CoreException(this, message);
    }

    /**
     * Creates a CoreException with this error code, the given message, a cause and arguments for message formatting.
     *
     * @param message the error message
     * @param e       the cause of the exception
     * @param args    optional arguments for message formatting.
     * @return a CoreException instance
     */
    public CoreException createCoreException(String message, Throwable e, Object[] args) {
        return new CoreException(this, message, e, args);
    }

    /**
     * Creates a CoreException with this error code, the given message and arguments for message formatting.
     *
     * @param message the error message
     * @param args    optional arguments for message formatting.
     * @return a CoreException instance
     */
    public CoreException createCoreException(String message, Object[] args) {
        return new CoreException(this, message, args);
    }

    /**
     * Creates a CoreException with this error code, the given message and cause.
     *
     * @param message the error message
     * @param e       the cause of the exception
     * @return a CoreException instance
     */
    public CoreException createCoreException(String message, Throwable e) {
        return new CoreException(this, message, e);
    }
}
