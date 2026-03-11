package com.bridgelabz;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuantityMeasurementAppTest {

    private static final double EPS = 1e-6;

    // -----------------------
    // LENGTH TESTS
    // -----------------------

    @Test
    void testLengthEquality_FeetAndInch() {

        Quantity<LengthUnit> a = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(12.0, LengthUnit.INCH);

        assertTrue(a.equals(b));
    }

    @Test
    void testLengthConversion_FeetToInch() {

        Quantity<LengthUnit> a = new Quantity<>(1.0, LengthUnit.FEET);

        Quantity<LengthUnit> result =
                a.convertTo(LengthUnit.INCH);

        assertEquals(12.0, result.getValue(), EPS);
    }

    @Test
    void testLengthAddition_FeetPlusInch() {

        Quantity<LengthUnit> a = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(12.0, LengthUnit.INCH);

        Quantity<LengthUnit> result = a.add(b);

        assertEquals(2.0, result.getValue(), EPS);
        assertEquals(LengthUnit.FEET, result.getUnit());
    }

    @Test
    void testLengthAddition_TargetUnit_Yards() {

        Quantity<LengthUnit> a = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(12.0, LengthUnit.INCH);

        Quantity<LengthUnit> result =
                a.add(b, LengthUnit.YARDS);

        assertEquals(2.0 / 3.0, result.getValue(), EPS);
        assertEquals(LengthUnit.YARDS, result.getUnit());
    }

    // -----------------------
    // WEIGHT TESTS
    // -----------------------

    @Test
    void testWeightEquality_KgAndGram() {

        Quantity<WeightUnit> a = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> b = new Quantity<>(1000.0, WeightUnit.GRAM);

        assertTrue(a.equals(b));
    }

    @Test
    void testWeightConversion_KgToGram() {

        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);

        Quantity<WeightUnit> result =
                kg.convertTo(WeightUnit.GRAM);

        assertEquals(1000.0, result.getValue(), EPS);
    }

    @Test
    void testWeightAddition_KgPlusGram() {

        Quantity<WeightUnit> a = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        Quantity<WeightUnit> b = new Quantity<>(1000.0, WeightUnit.GRAM);

        Quantity<WeightUnit> result = a.add(b);

        assertEquals(2.0, result.getValue(), EPS);
        assertEquals(WeightUnit.KILOGRAM, result.getUnit());
    }

    // -----------------------
    // VOLUME TESTS (UC11)
    // -----------------------

    @Test
    void testVolumeEquality_LitreAndMillilitre() {

        Quantity<VolumeUnit> a = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> b = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        assertTrue(a.equals(b));
    }

    @Test
    void testVolumeConversion_LitreToMillilitre() {

        Quantity<VolumeUnit> litre =
                new Quantity<>(1.0, VolumeUnit.LITRE);

        Quantity<VolumeUnit> result =
                litre.convertTo(VolumeUnit.MILLILITRE);

        assertEquals(1000.0, result.getValue(), EPS);
    }

    @Test
    void testVolumeConversion_GallonToLitre() {

        Quantity<VolumeUnit> gallon =
                new Quantity<>(1.0, VolumeUnit.GALLON);

        Quantity<VolumeUnit> result =
                gallon.convertTo(VolumeUnit.LITRE);

        assertEquals(3.78541, result.getValue(), 1e-4);
    }

    @Test
    void testVolumeAddition_LitrePlusMillilitre() {

        Quantity<VolumeUnit> a = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> b = new Quantity<>(1000.0, VolumeUnit.MILLILITRE);

        Quantity<VolumeUnit> result = a.add(b);

        assertEquals(2.0, result.getValue(), EPS);
        assertEquals(VolumeUnit.LITRE, result.getUnit());
    }

    @Test
    void testVolumeAddition_TargetUnit_Gallon() {

        Quantity<VolumeUnit> a = new Quantity<>(1.0, VolumeUnit.LITRE);
        Quantity<VolumeUnit> b = new Quantity<>(1.0, VolumeUnit.GALLON);

        Quantity<VolumeUnit> result =
                a.add(b, VolumeUnit.GALLON);

        assertTrue(result.getValue() > 1.0);
    }

    // -----------------------
    // CROSS CATEGORY TEST
    // -----------------------

    @Test
    void testVolumeVsLength_NotEqual() {

        Quantity<VolumeUnit> volume =
                new Quantity<>(1.0, VolumeUnit.LITRE);

        Quantity<LengthUnit> length =
                new Quantity<>(1.0, LengthUnit.FEET);

        assertFalse(volume.equals(length));
    }

    // -----------------------
    // VALIDATION TESTS
    // -----------------------

    @Test
    void testConstructor_NullUnit() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Quantity<>(1.0, null)
        );
    }

    @Test
    void testConstructor_InvalidValue() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Quantity<>(Double.NaN, LengthUnit.FEET)
        );
    }
    @Test
    void testSubtraction_FeetMinusInches() {

        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(6.0, LengthUnit.INCH);

        Quantity<LengthUnit> result = a.subtract(b);

        assertEquals(9.5, result.getValue(), 1e-6);
    }
    @Test
    void testDivision_FeetByFeet() {

        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(2.0, LengthUnit.FEET);

        double result = a.divide(b);

        assertEquals(5.0, result, 1e-6);
    }
    @Test
    void testDivision_ByZero() {

        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(0.0, LengthUnit.FEET);

        assertThrows(ArithmeticException.class, () -> a.divide(b));
    }

}