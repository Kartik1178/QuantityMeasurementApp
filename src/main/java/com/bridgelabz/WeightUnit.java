package com.bridgelabz;

/** UC10 WeightUnit. Base unit = KILOGRAM */
public enum WeightUnit implements IMeasurable {

    KILOGRAM(1.0),
    GRAM(0.001),
    POUND(0.453592);

    private final double conversionFactor;

    WeightUnit(double conversionFactor) { this.conversionFactor = conversionFactor; }

    @Override public double getConversionFactor()           { return conversionFactor; }
    @Override public double convertToBaseUnit(double v)     { if (!Double.isFinite(v)) throw new IllegalArgumentException("Value must be finite"); return v * conversionFactor; }
    @Override public double convertFromBaseUnit(double v)   { if (!Double.isFinite(v)) throw new IllegalArgumentException("Value must be finite"); return v / conversionFactor; }
    @Override public String getUnitName()                   { return name(); }
    @Override public String getMeasurementType()            { return "WEIGHT"; }
    @Override public IMeasurable fromUnitName(String n)     { return WeightUnit.valueOf(n.toUpperCase()); }
}
