package com.bridgelabz;

import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

/**
 * Generic Quantity class supporting multiple measurement categories.
 * UC13 refactor centralizes arithmetic logic to enforce DRY principle.
 */
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

    // ----------------------------
    // EQUALITY
    // ----------------------------

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
        return "Quantity(" + value + ", " + unit.getUnitName() + ")";
    }

    // ----------------------------
    // CONVERSION
    // ----------------------------

    public Quantity<U> convertTo(U targetUnit) {

        Objects.requireNonNull(targetUnit);

        double base = unit.convertToBaseUnit(value);

        double converted = targetUnit.convertFromBaseUnit(base);

        return new Quantity<>(converted, targetUnit);
    }

    // ----------------------------
    // ADDITION
    // ----------------------------

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {

        validateArithmeticOperands(other, targetUnit, true);

        double resultBase =
                performBaseArithmetic(other, ArithmeticOperation.ADD);

        double converted =
                targetUnit.convertFromBaseUnit(resultBase);

        return new Quantity<>(roundToTwoDecimals(converted), targetUnit);
    }

    // ----------------------------
    // SUBTRACTION
    // ----------------------------

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {

        validateArithmeticOperands(other, targetUnit, true);

        double resultBase =
                performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);

        double converted =
                targetUnit.convertFromBaseUnit(resultBase);

        return new Quantity<>(roundToTwoDecimals(converted), targetUnit);
    }

    // ----------------------------
    // DIVISION
    // ----------------------------

    public double divide(Quantity<U> other) {

        validateArithmeticOperands(other, null, false);

        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

    // ----------------------------
    // UC13 CENTRALIZED VALIDATION
    // ----------------------------

    private void validateArithmeticOperands(
            Quantity<U> other,
            U targetUnit,
            boolean targetUnitRequired) {

        if (other == null)
            throw new IllegalArgumentException("Other quantity must not be null");

        if (this.unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Incompatible unit categories");

        if (!Double.isFinite(this.value) || !Double.isFinite(other.value))
            throw new IllegalArgumentException("Values must be finite");

        if (targetUnitRequired && targetUnit == null)
            throw new IllegalArgumentException("Target unit must not be null");
    }

    // ----------------------------
    // UC13 CENTRALIZED ARITHMETIC
    // ----------------------------

    private double performBaseArithmetic(
            Quantity<U> other,
            ArithmeticOperation operation) {

        double thisBase = unit.convertToBaseUnit(value);

        double otherBase = other.unit.convertToBaseUnit(other.value);

        return operation.compute(thisBase, otherBase);
    }

    // ----------------------------
    // ROUNDING HELPER
    // ----------------------------

    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    // ----------------------------
    // ARITHMETIC OPERATION ENUM
    // ----------------------------

    private enum ArithmeticOperation {

        ADD((a, b) -> a + b),

        SUBTRACT((a, b) -> a - b),

        DIVIDE((a, b) -> {
            if (b == 0)
                throw new ArithmeticException("Division by zero");
            return a / b;
        });

        private final DoubleBinaryOperator operator;

        ArithmeticOperation(DoubleBinaryOperator operator) {
            this.operator = operator;
        }

        public double compute(double a, double b) {
            return operator.applyAsDouble(a, b);
        }
    }
}