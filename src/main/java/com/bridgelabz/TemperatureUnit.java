package com.bridgelabz;

/** UC14 TemperatureUnit. Base unit = CELSIUS */
public enum TemperatureUnit implements IMeasurable {

    CELSIUS,
    FAHRENHEIT;

    private final SupportsArithmetic arithmeticSupport = () -> false;

    @Override public String getUnitName()               { return name(); }
    @Override public double getConversionFactor()       { return 1.0; }

    @Override
    public double convertToBaseUnit(double value) {
        if (this == CELSIUS) return value;
        return (value - 32) * 5.0 / 9.0;
    }

    @Override
    public double convertFromBaseUnit(double baseValue) {
        if (this == CELSIUS) return baseValue;
        return baseValue * 9.0 / 5.0 + 32;
    }

    @Override public boolean supportsArithmetic() { return arithmeticSupport.isSupported(); }

    @Override
    public void validateOperationSupport(String operation) {
        if (!arithmeticSupport.isSupported())
            throw new UnsupportedOperationException(name() + " does not support " + operation + " operations.");
    }

    @Override public String getMeasurementType()            { return "TEMPERATURE"; }
    @Override public IMeasurable fromUnitName(String n)     { return TemperatureUnit.valueOf(n.toUpperCase()); }
}
