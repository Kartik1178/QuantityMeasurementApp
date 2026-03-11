package com.bridgelabz;

import java.util.Objects;

public class Quantity<U extends IMeasurable> {

    private static final double EPS = 1e-6;

    private final double value;
    private final U unit;

    public Quantity(double value, U unit) {
        if (!Double.isFinite(value))
            throw new IllegalArgumentException("Value must be finite");

        if (unit == null)
            throw new IllegalArgumentException("Unit must not be null");

        this.value = value;
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public U getUnit() {
        return unit;
    }

    public Quantity<U> convertTo(U targetUnit) {
        Objects.requireNonNull(targetUnit);

        double base = unit.convertToBaseUnit(value);
        double converted = targetUnit.convertFromBaseUnit(base);

        return new Quantity<>(converted, targetUnit);
    }

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {

        double a = unit.convertToBaseUnit(value);
        double b = other.unit.convertToBaseUnit(other.value);

        double sum = a + b;

        double result = targetUnit.convertFromBaseUnit(sum);

        return new Quantity<>(result, targetUnit);
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (!(obj instanceof Quantity<?> other)) return false;

        if (this.unit.getClass() != other.unit.getClass())
            return false;

        double thisBase = unit.convertToBaseUnit(value);
        double otherBase = other.unit.convertToBaseUnit(other.value);

        return Math.abs(thisBase - otherBase) <= EPS;
    }

    @Override
    public int hashCode() {
        double base = unit.convertToBaseUnit(value);
        long rounded = Math.round(base / EPS);
        return Long.hashCode(rounded);
    }

    @Override
    public String toString() {
        return "Quantity(" + value + ", " + unit + ")";
    }
}