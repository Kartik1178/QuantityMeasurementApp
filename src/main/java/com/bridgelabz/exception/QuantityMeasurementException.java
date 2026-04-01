package com.bridgelabz.exception;

/**
 * UC15 Custom unchecked exception for quantity measurement errors.
 */
public class QuantityMeasurementException extends RuntimeException {

    public QuantityMeasurementException(String message) {
        super(message);
    }

    public QuantityMeasurementException(String message, Throwable cause) {
        super(message, cause);
    }
}
