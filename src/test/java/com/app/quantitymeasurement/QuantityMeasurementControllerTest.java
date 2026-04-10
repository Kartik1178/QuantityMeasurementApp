package com.app.quantitymeasurement;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UC16 Controller Layer Unit Tests using Mockito.
 */
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementControllerTest {

    @Mock
    private IQuantityMeasurementService mockService;

    private QuantityMeasurementController controller;

    @BeforeEach
    void setUp() {
        controller = new QuantityMeasurementController(mockService);
    }

    @Test
    @Order(1)
    void testNullService_Throws() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuantityMeasurementController(null));
    }

    @Test
    @Order(2)
    void testPerformComparison_Success() {
        QuantityDTO a = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO b = new QuantityDTO(12.0, "INCH", "LENGTH");
        when(mockService.compare(a, b)).thenReturn(true);

        assertDoesNotThrow(() -> controller.performComparison(a, b));
        verify(mockService, times(1)).compare(a, b);
    }

    @Test
    @Order(3)
    void testPerformComparison_Error() {
        QuantityDTO a = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO b = new QuantityDTO(1.0, "KILOGRAM", "WEIGHT");
        when(mockService.compare(a, b)).thenThrow(new RuntimeException("cross-category"));

        assertDoesNotThrow(() -> controller.performComparison(a, b));
        verify(mockService, times(1)).compare(a, b);
    }

    @Test
    @Order(4)
    void testPerformConversion_Success() {
        QuantityDTO input = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO result = new QuantityDTO(12.0, "INCH", "LENGTH");
        when(mockService.convert(input, "INCH")).thenReturn(result);

        assertDoesNotThrow(() -> controller.performConversion(input, "INCH"));
        verify(mockService, times(1)).convert(input, "INCH");
    }

    @Test
    @Order(5)
    void testPerformAddition_Success() {
        QuantityDTO a = new QuantityDTO(1.0, "FEET", "LENGTH");
        QuantityDTO b = new QuantityDTO(12.0, "INCH", "LENGTH");
        QuantityDTO result = new QuantityDTO(2.0, "FEET", "LENGTH");
        when(mockService.add(a, b)).thenReturn(result);

        assertDoesNotThrow(() -> controller.performAddition(a, b));
        verify(mockService, times(1)).add(a, b);
    }

    @Test
    @Order(6)
    void testPerformSubtraction_Success() {
        QuantityDTO a = new QuantityDTO(10.0, "FEET", "LENGTH");
        QuantityDTO b = new QuantityDTO(6.0, "INCH", "LENGTH");
        QuantityDTO result = new QuantityDTO(9.5, "FEET", "LENGTH");
        when(mockService.subtract(a, b)).thenReturn(result);

        assertDoesNotThrow(() -> controller.performSubtraction(a, b));
        verify(mockService, times(1)).subtract(a, b);
    }

    @Test
    @Order(7)
    void testPerformDivision_Success() {
        QuantityDTO a = new QuantityDTO(10.0, "FEET", "LENGTH");
        QuantityDTO b = new QuantityDTO(2.0, "FEET", "LENGTH");
        when(mockService.divide(a, b)).thenReturn(5.0);

        assertDoesNotThrow(() -> controller.performDivision(a, b));
        verify(mockService, times(1)).divide(a, b);
    }

    @Test
    @Order(8)
    void testPerformDivision_Error() {
        QuantityDTO a = new QuantityDTO(10.0, "FEET", "LENGTH");
        QuantityDTO b = new QuantityDTO(0.0, "FEET", "LENGTH");
        when(mockService.divide(a, b)).thenThrow(new RuntimeException("Division by zero"));

        assertDoesNotThrow(() -> controller.performDivision(a, b));
    }
}
