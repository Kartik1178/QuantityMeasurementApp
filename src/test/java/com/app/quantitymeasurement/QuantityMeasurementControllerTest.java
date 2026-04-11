package com.app.quantitymeasurement;

import com.app.quantitymeasurement.config.SecurityConfig;
import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.dto.QuantityInputDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.exception.GlobalExceptionHandler;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UC17 REST Controller Unit Tests using Spring MockMvc.
 * Tests the controller layer in isolation with mocked service.
 */
@WebMvcTest(QuantityMeasurementController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IQuantityMeasurementService service;

    @Autowired
    private ObjectMapper objectMapper;

    // ============ Helper Methods ============

    private QuantityInputDTO createCompareInput() {
        return new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(12.0, "INCH", "LengthUnit")
        );
    }

    private QuantityMeasurementDTO createCompareResponse(boolean result) {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(1.0);
        dto.setThisUnit("FEET");
        dto.setThisMeasurementType("LengthUnit");
        dto.setThatValue(12.0);
        dto.setThatUnit("INCH");
        dto.setThatMeasurementType("LengthUnit");
        dto.setOperation("compare");
        dto.setResultString(String.valueOf(result));
        return dto;
    }

    private QuantityMeasurementDTO createAddResponse() {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setThisValue(1.0);
        dto.setThisUnit("FEET");
        dto.setThisMeasurementType("LengthUnit");
        dto.setThatValue(12.0);
        dto.setThatUnit("INCH");
        dto.setThatMeasurementType("LengthUnit");
        dto.setOperation("add");
        dto.setResultValue(2.0);
        dto.setResultUnit("FEET");
        dto.setResultMeasurementType("LengthUnit");
        return dto;
    }

    // ============ Compare Tests ============

    @Test
    @Order(1)
    void testCompareQuantities_Success() throws Exception {
        QuantityInputDTO input = createCompareInput();
        QuantityMeasurementDTO response = createCompareResponse(true);
        when(service.compareQuantities(any(QuantityInputDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quantities/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("compare"))
                .andExpect(jsonPath("$.resultString").value("true"))
                .andExpect(jsonPath("$.thisUnit").value("FEET"))
                .andExpect(jsonPath("$.thatUnit").value("INCH"));
    }

    @Test
    @Order(2)
    void testCompareQuantities_NotEqual() throws Exception {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(10.0, "INCH", "LengthUnit")
        );
        QuantityMeasurementDTO response = createCompareResponse(false);
        response.setThatValue(10.0);
        response.setResultString("false");
        when(service.compareQuantities(any(QuantityInputDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quantities/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultString").value("false"));
    }

    // ============ Convert Tests ============

    @Test
    @Order(3)
    void testConvertQuantity_Success() throws Exception {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(0.0, "INCH", "LengthUnit")
        );
        QuantityMeasurementDTO response = new QuantityMeasurementDTO();
        response.setThisValue(1.0);
        response.setThisUnit("FEET");
        response.setThisMeasurementType("LengthUnit");
        response.setOperation("convert");
        response.setResultValue(12.0);
        when(service.convertQuantity(any(QuantityInputDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quantities/convert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("convert"))
                .andExpect(jsonPath("$.resultValue").value(12.0));
    }

    // ============ Add Tests ============

    @Test
    @Order(4)
    void testAddQuantities_Success() throws Exception {
        QuantityInputDTO input = createCompareInput();
        QuantityMeasurementDTO response = createAddResponse();
        when(service.addQuantities(any(QuantityInputDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quantities/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.resultValue").value(2.0))
                .andExpect(jsonPath("$.resultUnit").value("FEET"));
    }

    // ============ Subtract Tests ============

    @Test
    @Order(5)
    void testSubtractQuantities_Success() throws Exception {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(10.0, "FEET", "LengthUnit"),
                new QuantityDTO(6.0, "INCH", "LengthUnit")
        );
        QuantityMeasurementDTO response = new QuantityMeasurementDTO();
        response.setOperation("subtract");
        response.setResultValue(9.5);
        response.setResultUnit("FEET");
        when(service.subtractQuantities(any(QuantityInputDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quantities/subtract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("subtract"))
                .andExpect(jsonPath("$.resultValue").value(9.5));
    }

    // ============ Divide Tests ============

    @Test
    @Order(6)
    void testDivideQuantities_Success() throws Exception {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(10.0, "FEET", "LengthUnit"),
                new QuantityDTO(2.0, "FEET", "LengthUnit")
        );
        QuantityMeasurementDTO response = new QuantityMeasurementDTO();
        response.setOperation("divide");
        response.setResultValue(5.0);
        when(service.divideQuantities(any(QuantityInputDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/quantities/divide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("divide"))
                .andExpect(jsonPath("$.resultValue").value(5.0));
    }

    // ============ Error Handling Tests ============

    @Test
    @Order(7)
    void testCompareQuantities_CrossCategory_Returns400() throws Exception {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(1.0, "KILOGRAM", "WeightUnit")
        );
        when(service.compareQuantities(any(QuantityInputDTO.class)))
                .thenThrow(new QuantityMeasurementException("Cannot compare different measurement types"));

        mockMvc.perform(post("/api/v1/quantities/compare")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Quantity Measurement Error"));
    }

    @Test
    @Order(8)
    void testDivideByZero_Returns400() throws Exception {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(0.0, "INCH", "LengthUnit")
        );
        when(service.divideQuantities(any(QuantityInputDTO.class)))
                .thenThrow(new QuantityMeasurementException("Divide by zero"));

        mockMvc.perform(post("/api/v1/quantities/divide")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Divide by zero"));
    }

    // ============ History Tests ============

    @Test
    @Order(9)
    void testGetHistoryByOperation_Success() throws Exception {
        QuantityMeasurementDTO dto = createCompareResponse(true);
        List<QuantityMeasurementDTO> history = Arrays.asList(dto);
        when(service.getHistoryByOperation("COMPARE")).thenReturn(history);

        mockMvc.perform(get("/api/v1/quantities/history/operation/COMPARE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].operation").value("compare"));
    }

    @Test
    @Order(10)
    void testGetHistoryByMeasurementType_Success() throws Exception {
        QuantityMeasurementDTO dto = createCompareResponse(true);
        List<QuantityMeasurementDTO> history = Arrays.asList(dto);
        when(service.getHistoryByMeasurementType("LengthUnit")).thenReturn(history);

        mockMvc.perform(get("/api/v1/quantities/history/type/LengthUnit"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].thisMeasurementType").value("LengthUnit"));
    }

    @Test
    @Order(11)
    void testGetCountByOperation_Success() throws Exception {
        when(service.getCountByOperationSuccess("COMPARE")).thenReturn(5L);

        mockMvc.perform(get("/api/v1/quantities/count/COMPARE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationType").value("COMPARE"))
                .andExpect(jsonPath("$.count").value(5));
    }

    @Test
    @Order(12)
    void testGetErrorHistory_Success() throws Exception {
        QuantityMeasurementDTO dto = new QuantityMeasurementDTO();
        dto.setError(true);
        dto.setErrorMessage("Test error");
        when(service.getErrorHistory()).thenReturn(Arrays.asList(dto));

        mockMvc.perform(get("/api/v1/quantities/history/errored"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].error").value(true))
                .andExpect(jsonPath("$[0].errorMessage").value("Test error"));
    }

    @Test
    @Order(13)
    void testClearHistory_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/quantities/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Measurement history cleared successfully"));
    }
}
