package com.app.quantitymeasurement.exception;

/**
 * UC16 Custom exception for database-related errors.
 * Wraps SQL exceptions and connection pool errors.
 */
public class DatabaseException extends RuntimeException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
