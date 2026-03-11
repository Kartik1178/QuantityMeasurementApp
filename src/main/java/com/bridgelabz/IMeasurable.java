package com.bridgelabz;

/**
 * Functional interface used to indicate whether a unit
 * supports arithmetic operations.
 */
@FunctionalInterface
interface SupportsArithmetic {
    boolean isSupported();
}

/**
 * Interface implemented by all measurable units.
 */
public interface IMeasurable {

    /* ------------------------
       MANDATORY METHODS
    ------------------------- */

    String getUnitName();

    double getConversionFactor();

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double baseValue);


    /* ------------------------
       OPTIONAL OPERATION SUPPORT
    ------------------------- */

    // By default all units support arithmetic
    SupportsArithmetic supportsArithmetic = () -> true;

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    /**
     * Validates whether the operation is supported.
     * TemperatureUnit will override this.
     */
    default void validateOperationSupport(String operation) {
        // default: allow
    }
}