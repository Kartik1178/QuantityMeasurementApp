package com.app.quantitymeasurement.unit;

/** UC10 LengthUnit. Base unit = FEET */
public enum LengthUnit implements IMeasurable {

    FEET(1.0),
    INCH(1.0 / 12.0),
    YARDS(3.0),
    CENTIMETERS(1.0 / 30.48);

    private final double conversionFactor;

    LengthUnit(double conversionFactor) {
        this.conversionFactor = conversionFactor;
    }

    @Override
    public double getConversionFactor() {
        return conversionFactor;
    }

    @Override
    public double convertToBaseUnit(double v) {
        if (!Double.isFinite(v))
            throw new IllegalArgumentException("Value must be finite");
        return v * conversionFactor;
    }

    @Override
    public double convertFromBaseUnit(double v) {
        if (!Double.isFinite(v))
            throw new IllegalArgumentException("Value must be finite");
        return v / conversionFactor;
    }

    @Override
    public String getUnitName() {
        return name();
    }

    @Override
    public String getMeasurementType() {
        return "LENGTH";
    }

    @Override
    public IMeasurable fromUnitName(String n) {
        return LengthUnit.valueOf(n.toUpperCase());
    }
}
