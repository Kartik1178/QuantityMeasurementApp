package com.app.quantitymeasurement.unit;

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
 * UC15: added getMeasurementType() and fromUnitName() helpers
 * so the service layer can resolve units from QuantityDTO strings.
 */
public interface IMeasurable {

    /* --- MANDATORY ----------------------------------------- */

    String getUnitName();

    double getConversionFactor();

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double baseValue);

    /* --- ARITHMETIC SUPPORT -------------------------------- */

    SupportsArithmetic supportsArithmetic = () -> true;

    default boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    /** Validates whether the operation is supported (TemperatureUnit overrides). */
    default void validateOperationSupport(String operation) {
    }

    /* --- UC15 HELPERS -------------------------------------- */

    /**
     * Returns the category name used in QuantityDTO.type
     * e.g. "LENGTH", "WEIGHT", "VOLUME", "TEMPERATURE"
     */
    String getMeasurementType();

    /**
     * Resolves a unit instance from its name within the same enum.
     * Used to convert QuantityDTO -> IMeasurable.
     */
    IMeasurable fromUnitName(String unitName);
}
