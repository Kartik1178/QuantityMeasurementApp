package com.bridgelabz;

/**
 * Temperature units
 * Base unit = CELSIUS
 */
public enum TemperatureUnit implements IMeasurable {

    CELSIUS,
    FAHRENHEIT;

    // Temperature does NOT support arithmetic
    private final SupportsArithmetic supportsArithmetic = () -> false;

    @Override
    public String getUnitName() {
        return name();
    }

    @Override
    public double getConversionFactor() {
        return 1.0;
    }

    /**
     * Convert temperature to base unit (Celsius)
     */
    @Override
    public double convertToBaseUnit(double value) {

        if (this == CELSIUS)
            return value;

        // Fahrenheit → Celsius
        return (value - 32) * 5 / 9;
    }

    /**
     * Convert temperature from base unit (Celsius)
     */
    @Override
    public double convertFromBaseUnit(double baseValue) {

        if (this == CELSIUS)
            return baseValue;

        // Celsius → Fahrenheit
        return baseValue * 9 / 5 + 32;
    }

    @Override
    public boolean supportsArithmetic() {
        return supportsArithmetic.isSupported();
    }

    /**
     * Throw exception when arithmetic is attempted
     */
    @Override
    public void validateOperationSupport(String operation) {

        if (!supportsArithmetic.isSupported()) {

            throw new UnsupportedOperationException(
                    this.name() + " does not support " +
                            operation + " operations.");
        }
    }
}