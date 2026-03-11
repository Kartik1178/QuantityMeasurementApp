package com.bridgelabz;

/**
 * UC10: Interface for all measurable units.
 * Provides contract for conversion operations.
 */
public interface IMeasurable {

    double getConversionFactor();

    double convertToBaseUnit(double value);

    double convertFromBaseUnit(double baseValue);

    String getUnitName();
}