package com.app.quantitymeasurement;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.impl.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.impl.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.unit.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC15 Test Suite - covers all layer interactions and backward compatibility.
 * Refactored to com.app.quantitymeasurement package.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementAppUC15Test {

    private static final double EPS = 1e-4;

    private IQuantityMeasurementRepository repository;
    private IQuantityMeasurementService service;
    private QuantityMeasurementController controller;

    @BeforeEach
    void setUp() {
        repository = QuantityMeasurementCacheRepository.getInstance();
        repository.clear();
        service = new QuantityMeasurementServiceImpl(repository);
        controller = new QuantityMeasurementController(service);
    }

    // -- ENTITY LAYER -----------------------------------------

    @Test
    @Order(1)
    void testEntity_BinaryOperation() {
        var e = new com.app.quantitymeasurement.entity.QuantityMeasurementEntity(
                1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET");
        assertFalse(e.isError());
        assertEquals("ADD", e.getOperationType());
        assertEquals(2.0, e.getResultValue(), EPS);
    }

    @Test
    @Order(2)
    void testEntity_ErrorConstruction() {
        var e = new com.app.quantitymeasurement.entity.QuantityMeasurementEntity("something went wrong");
        assertTrue(e.isError());
        assertEquals("something went wrong", e.getErrorMessage());
    }

    // -- SERVICE: COMPARE ------------------------------------

    @Test
    @Order(3)
    void testService_Compare_FeetAndInch_Equal() {
        assertTrue(service.compare(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH")));
    }

    @Test
    @Order(4)
    void testService_Compare_KgAndGram_Equal() {
        assertTrue(service.compare(
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"),
                new QuantityDTO(1000.0, "GRAM", "WEIGHT")));
    }

    @Test
    @Order(5)
    void testService_Compare_CelsiusAndFahrenheit_Equal() {
        assertTrue(service.compare(
                new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE"),
                new QuantityDTO(32.0, "FAHRENHEIT", "TEMPERATURE")));
    }

    @Test
    @Order(6)
    void testService_Compare_CrossCategory_Throws() {
        assertThrows(QuantityMeasurementException.class, () -> service.compare(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT")));
    }

    // -- SERVICE: CONVERT ------------------------------------

    @Test
    @Order(7)
    void testService_Convert_FeetToInch() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, "FEET", "LENGTH"), "INCH");
        assertEquals(12.0, result.getValue(), EPS);
        assertEquals("INCH", result.getUnit());
    }

    @Test
    @Order(8)
    void testService_Convert_KgToGram() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"), "GRAM");
        assertEquals(1000.0, result.getValue(), EPS);
    }

    @Test
    @Order(9)
    void testService_Convert_CelsiusToFahrenheit() {
        QuantityDTO result = service.convert(
                new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE"), "FAHRENHEIT");
        assertEquals(212.0, result.getValue(), EPS);
    }

    // -- SERVICE: ADD ----------------------------------------

    @Test
    @Order(10)
    void testService_Add_FeetPlusInch() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH"));
        assertEquals(2.0, result.getValue(), EPS);
        assertEquals("FEET", result.getUnit());
    }

    @Test
    @Order(11)
    void testService_Add_KgPlusGram() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"),
                new QuantityDTO(1000.0, "GRAM", "WEIGHT"));
        assertEquals(2.0, result.getValue(), EPS);
    }

    @Test
    @Order(12)
    void testService_Add_VolumeLitrePlusMillilitre() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0, "LITRE", "VOLUME"),
                new QuantityDTO(1000.0, "MILLILITRE", "VOLUME"));
        assertEquals(2.0, result.getValue(), EPS);
    }

    @Test
    @Order(13)
    void testService_Add_Temperature_Throws() {
        assertThrows(QuantityMeasurementException.class, () -> service.add(
                new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE"),
                new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE")));
    }

    @Test
    @Order(14)
    void testService_Add_CrossCategory_Throws() {
        assertThrows(QuantityMeasurementException.class, () -> service.add(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT")));
    }

    // -- SERVICE: SUBTRACT -----------------------------------

    @Test
    @Order(15)
    void testService_Subtract_FeetMinusInch() {
        QuantityDTO result = service.subtract(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(6.0, "INCH", "LENGTH"));
        assertEquals(9.5, result.getValue(), EPS);
    }

    // -- SERVICE: DIVIDE -------------------------------------

    @Test
    @Order(16)
    void testService_Divide_FeetByFeet() {
        double result = service.divide(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(2.0, "FEET", "LENGTH"));
        assertEquals(5.0, result, EPS);
    }

    @Test
    @Order(17)
    void testService_Divide_ByZero_Throws() {
        assertThrows(Exception.class, () -> service.divide(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(0.0, "FEET", "LENGTH")));
    }

    // -- REPOSITORY ------------------------------------------

    @Test
    @Order(18)
    void testRepository_SaveAndFindAll() {
        service.add(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH"));
        assertEquals(1, repository.findAll().size());
    }

    // -- CONTROLLER ------------------------------------------

    @Test
    @Order(19)
    void testController_NullService_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuantityMeasurementController(null));
    }

    @Test
    @Order(20)
    void testController_PerformAddition_NoException() {
        assertDoesNotThrow(() -> controller.performAddition(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH")));
    }

    @Test
    @Order(21)
    void testController_PerformAddition_ErrorHandled() {
        assertDoesNotThrow(() -> controller.performAddition(
                new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE"),
                new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE")));
    }

    // -- LAYER SEPARATION ------------------------------------

    @Test
    @Order(22)
    void testLayerSeparation_ServiceWithoutController() {
        assertTrue(service.compare(
                new QuantityDTO(1.0, "LITRE", "VOLUME"),
                new QuantityDTO(1000.0, "MILLILITRE", "VOLUME")));
    }

    // -- BACKWARD COMPATIBILITY (UC1-UC14) -------------------

    @Test
    @Order(23)
    void testBackwardCompat_LengthEquality() {
        Quantity<LengthUnit> a = new Quantity<>(1.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(12.0, LengthUnit.INCH);
        assertTrue(a.equals(b));
    }

    @Test
    @Order(24)
    void testBackwardCompat_WeightConversion() {
        Quantity<WeightUnit> kg = new Quantity<>(1.0, WeightUnit.KILOGRAM);
        assertEquals(1000.0, kg.convertTo(WeightUnit.GRAM).getValue(), EPS);
    }

    @Test
    @Order(25)
    void testBackwardCompat_TemperatureComparison() {
        Quantity<TemperatureUnit> c = new Quantity<>(0.0, TemperatureUnit.CELSIUS);
        Quantity<TemperatureUnit> f = new Quantity<>(32.0, TemperatureUnit.FAHRENHEIT);
        assertTrue(c.equals(f));
    }

    @Test
    @Order(26)
    void testBackwardCompat_Division() {
        Quantity<LengthUnit> a = new Quantity<>(10.0, LengthUnit.FEET);
        Quantity<LengthUnit> b = new Quantity<>(2.0, LengthUnit.FEET);
        assertEquals(5.0, a.divide(b), EPS);
    }
}
