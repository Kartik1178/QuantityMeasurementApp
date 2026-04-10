package com.app.quantitymeasurement;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.impl.QuantityMeasurementServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UC16 Service Layer Unit Tests using Mockito.
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementServiceTest {

    @Mock
    private IQuantityMeasurementRepository mockRepository;

    private IQuantityMeasurementService service;

    @BeforeEach
    void setUp() {
        service = new QuantityMeasurementServiceImpl(mockRepository);
    }

    @Test
    @Order(1)
    void testCompare_Equal() {
        boolean result = service.compare(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH"));
        assertTrue(result);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    @Order(2)
    void testCompare_NotEqual() {
        boolean result = service.compare(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(10.0, "INCH", "LENGTH"));
        assertFalse(result);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    @Order(3)
    void testCompare_CrossCategory_Throws() {
        assertThrows(QuantityMeasurementException.class, () -> service.compare(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT")));
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    @Order(4)
    void testConvert_FeetToInch() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, "FEET", "LENGTH"), "INCH");
        assertEquals(12.0, result.getValue(), 1e-4);
        assertEquals("INCH", result.getUnit());
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    @Order(5)
    void testAdd_FeetPlusInch() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH"));
        assertEquals(2.0, result.getValue(), 1e-4);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    @Order(6)
    void testAdd_Temperature_Throws() {
        assertThrows(QuantityMeasurementException.class, () -> service.add(
                new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE"),
                new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE")));
    }

    @Test
    @Order(7)
    void testSubtract_FeetMinusInch() {
        QuantityDTO result = service.subtract(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(6.0, "INCH", "LENGTH"));
        assertEquals(9.5, result.getValue(), 1e-4);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    @Order(8)
    void testDivide_FeetByFeet() {
        double result = service.divide(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(2.0, "FEET", "LENGTH"));
        assertEquals(5.0, result, 1e-4);
        verify(mockRepository, times(1)).save(any(QuantityMeasurementEntity.class));
    }

    @Test
    @Order(9)
    void testDivide_ByZero_Throws() {
        assertThrows(QuantityMeasurementException.class, () -> service.divide(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(0.0, "FEET", "LENGTH")));
    }

    @Test
    @Order(10)
    void testConvert_NullType_Throws() {
        assertThrows(QuantityMeasurementException.class,
                () -> service.convert(new QuantityDTO(1.0, "FEET", null), "INCH"));
    }
}
