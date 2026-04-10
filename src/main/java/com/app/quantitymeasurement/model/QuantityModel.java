package com.app.quantitymeasurement.model;

import com.app.quantitymeasurement.unit.IMeasurable;

/**
 * UC15 Internal model used within the service layer.
 * Generic type U guarantees type-safe unit operations.
 */
public class QuantityModel<U extends IMeasurable> {

    private final double value;
    private final U unit;

    public QuantityModel(double value, U unit) {
        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return "QuantityModel{value=" + value + ", unit=" + unit.getUnitName() + "}";
    }
}
