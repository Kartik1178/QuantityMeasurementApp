package com.bridgelabz;

/**
 * UC10 QuantityMeasurementApp
 *
 * Demonstrates the usage of the generic Quantity<U extends IMeasurable> class.
 * Handles equality, conversion, and addition operations across measurement types.
 */
public class QuantityMeasurementApp {

    /** Demonstrate equality of two quantities */
    public static <U extends IMeasurable> boolean demonstrateEquality(
            Quantity<U> q1,
            Quantity<U> q2
    ) {
        return q1.equals(q2);
    }

    /** Demonstrate conversion to another unit */
    public static <U extends IMeasurable> Quantity<U> demonstrateConversion(
            Quantity<U> quantity,
            U targetUnit
    ) {
        return quantity.convertTo(targetUnit);
    }

    /** Demonstrate addition returning result in first operand unit */
    public static <U extends IMeasurable> Quantity<U> demonstrateAddition(
            Quantity<U> q1,
            Quantity<U> q2
    ) {
        return q1.add(q2);
    }

    /** Demonstrate addition returning result in specified unit */
    public static <U extends IMeasurable> Quantity<U> demonstrateAddition(
            Quantity<U> q1,
            Quantity<U> q2,
            U targetUnit
    ) {
        return q1.add(q2, targetUnit);
    }
    // UC12 subtraction demo
    public static <U extends IMeasurable> Quantity<U> demonstrateSubtraction(
            Quantity<U> q1,
            Quantity<U> q2
    ) {
        return q1.subtract(q2);
    }

    // explicit target
    public static <U extends IMeasurable> Quantity<U> demonstrateSubtraction(
            Quantity<U> q1,
            Quantity<U> q2,
            U targetUnit
    ) {
        return q1.subtract(q2, targetUnit);
    }

    // division demo
    public static <U extends IMeasurable> double demonstrateDivision(
            Quantity<U> q1,
            Quantity<U> q2
    ) {
        return q1.divide(q2);
    }
    public static void main(String[] args) {

        // LENGTH OPERATIONS
        Quantity<LengthUnit> lengthFeet = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> lengthInches = new Quantity<>(12.0, LengthUnit.INCH);

        System.out.println("Length Equality:");
        System.out.println(demonstrateEquality(lengthFeet, lengthInches));

        System.out.println("\nLength Conversion:");
        Quantity<LengthUnit> convertedLength =
                demonstrateConversion(lengthFeet, LengthUnit.INCH);

        System.out.println(convertedLength);

        System.out.println("\nLength Addition:");
        Quantity<LengthUnit> lengthSum =
                demonstrateAddition(lengthFeet, lengthInches, LengthUnit.FEET);

        System.out.println(lengthSum);


        // WEIGHT OPERATIONS
        Quantity<WeightUnit> weightKg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> weightGram = new Quantity<>(1000.0, WeightUnit.GRAM);

        System.out.println("\nWeight Equality:");
        System.out.println(demonstrateEquality(weightKg, weightGram));

        System.out.println("\nWeight Conversion:");
        Quantity<WeightUnit> convertedWeight =
                demonstrateConversion(weightKg, WeightUnit.GRAM);

        System.out.println(convertedWeight);

        System.out.println("\nWeight Addition:");
        Quantity<WeightUnit> weightSum =
                demonstrateAddition(weightKg, weightGram, WeightUnit.KILOGRAM);

        System.out.println(weightSum);
        // VOLUME OPERATIONS (UC11)

        Quantity<VolumeUnit> volume1 = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> volume2 = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);
        Quantity<VolumeUnit> volume3 = new Quantity<>(1.0, VolumeUnit.GALLON);

        System.out.println("\nVolume Equality:");
        System.out.println(volume1.equals(volume2)); // true

        System.out.println("\nVolume Conversion:");
        System.out.println(volume1.convertTo(VolumeUnit.MILLILITRE)); // 1000 mL
        System.out.println(volume3.convertTo(VolumeUnit.LITRE)); // ~3.78541 L

        System.out.println("\nVolume Addition:");
        System.out.println(volume1.add(volume2)); // 2 L
        System.out.println(volume1.add(volume3, VolumeUnit.MILLILITRE));
// SUBTRACTION
        Quantity<LengthUnit> lengthA = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> lengthB = new Quantity<>(6.0, LengthUnit.INCH);

        System.out.println(lengthA.subtract(lengthB));
        System.out.println(lengthA.subtract(lengthB, LengthUnit.INCH));

// DIVISION
        Quantity<LengthUnit> lengthC = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> lengthD = new Quantity<>(2.0, LengthUnit.FEET);

        System.out.println(lengthC.divide(lengthD)); // 5.0


    }
}