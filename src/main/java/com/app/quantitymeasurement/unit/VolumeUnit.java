package com.app.quantitymeasurement.unit;

/** UC11 VolumeUnit. Base unit = LITRE */
public enum VolumeUnit implements IMeasurable {

    LITRE(1.0),
    MILLILITRE(0.001),
    GALLON(3.78541);

    private final double conversionFactor;

    VolumeUnit(double conversionFactor) {
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
        return "VOLUME";
    }

    @Override
    public IMeasurable fromUnitName(String n) {
        return VolumeUnit.valueOf(n.toUpperCase());
    }
}
