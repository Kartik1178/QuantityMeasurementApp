package com.app.quantitymeasurement.unit;

import java.util.Objects;
import java.util.function.DoubleBinaryOperator;

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

    /*
     * ------------------------
     * EQUALITY
     * -------------------------
     */

    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof Quantity<?> other))
            return false;

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

    /*
     * ------------------------
     * CONVERSION
     * -------------------------
     */

    public Quantity<U> convertTo(U targetUnit) {

        Objects.requireNonNull(targetUnit);

        if (unit.getClass() != targetUnit.getClass())
            throw new IllegalArgumentException("Incompatible unit categories");

        double base = unit.convertToBaseUnit(value);

        double converted = targetUnit.convertFromBaseUnit(base);

        return new Quantity<>(converted, targetUnit);
    }

    /*
     * ------------------------
     * ADD
     * -------------------------
     */

    public Quantity<U> add(Quantity<U> other) {
        return add(other, this.unit);
    }

    public Quantity<U> add(Quantity<U> other, U targetUnit) {

        validateArithmeticOperands(other, targetUnit, true);

        double resultBase = performBaseArithmetic(other, ArithmeticOperation.ADD);

        double converted = targetUnit.convertFromBaseUnit(resultBase);

        return new Quantity<>(round(converted), targetUnit);
    }

    /*
     * ------------------------
     * SUBTRACT
     * -------------------------
     */

    public Quantity<U> subtract(Quantity<U> other) {
        return subtract(other, this.unit);
    }

    public Quantity<U> subtract(Quantity<U> other, U targetUnit) {

        validateArithmeticOperands(other, targetUnit, true);

        double resultBase = performBaseArithmetic(other, ArithmeticOperation.SUBTRACT);

        double converted = targetUnit.convertFromBaseUnit(resultBase);

        return new Quantity<>(round(converted), targetUnit);
    }

    /*
     * ------------------------
     * DIVIDE
     * -------------------------
     */

    public double divide(Quantity<U> other) {

        validateArithmeticOperands(other, null, false);

        return performBaseArithmetic(other, ArithmeticOperation.DIVIDE);
    }

    /*
     * ------------------------
     * VALIDATION
     * -------------------------
     */

    private void validateArithmeticOperands(
            Quantity<U> other,
            U targetUnit,
            boolean targetRequired) {

        if (other == null)
            throw new IllegalArgumentException("Other quantity must not be null");

        if (unit.getClass() != other.unit.getClass())
            throw new IllegalArgumentException("Incompatible unit categories");

        unit.validateOperationSupport("ARITHMETIC");

        if (targetRequired && targetUnit == null)
            throw new IllegalArgumentException("Target unit must not be null");
    }

    /*
     * ------------------------
     * ARITHMETIC CORE
     * -------------------------
     */

    private double performBaseArithmetic(
            Quantity<U> other,
            ArithmeticOperation operation) {

        unit.validateOperationSupport(operation.name());

        double a = unit.convertToBaseUnit(value);
        double b = other.unit.convertToBaseUnit(other.value);

        return operation.compute(a, b);
    }

    /*
     * ------------------------
     * ROUNDING
     * -------------------------
     */

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    /*
     * ------------------------
     * OPERATION ENUM
     * -------------------------
     */

    private enum ArithmeticOperation {

        ADD((a, b) -> a + b),

        SUBTRACT((a, b) -> a - b),

        DIVIDE((a, b) -> {

            if (b == 0)
                throw new ArithmeticException("Division by zero");

            return a / b;
        });

        private final DoubleBinaryOperator op;

        ArithmeticOperation(DoubleBinaryOperator op) {
            this.op = op;
        }

        public double compute(double a, double b) {
            return op.applyAsDouble(a, b);
        }
    }
}
