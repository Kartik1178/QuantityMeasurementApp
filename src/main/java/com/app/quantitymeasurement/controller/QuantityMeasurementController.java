package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.dto.QuantityInputDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * UC17 REST Controller for Quantity Measurement operations.
 * Exposes all core quantity operations and history management via HTTP endpoints.
 */
@RestController
@RequestMapping("/api/v1/quantities")
@Tag(name = "Quantity Measurements", description = "REST API for quantity measurement operations")
public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);

    @Autowired
    private IQuantityMeasurementService service;

    // ===================== Core Operations =====================

    @PostMapping("/compare")
    @Operation(summary = "Compare two quantities for equality")
    public ResponseEntity<QuantityMeasurementDTO> compareQuantities(
            @Valid @RequestBody QuantityInputDTO input) {
        logger.info("REST: Compare request - {}", input);
        QuantityMeasurementDTO response = service.compareQuantities(input);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/convert")
    @Operation(summary = "Convert a quantity from one unit to another")
    public ResponseEntity<QuantityMeasurementDTO> convertQuantity(
            @Valid @RequestBody QuantityInputDTO input) {
        logger.info("REST: Convert request - {}", input);
        QuantityMeasurementDTO response = service.convertQuantity(input);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    @Operation(summary = "Add two quantities")
    public ResponseEntity<QuantityMeasurementDTO> addQuantities(
            @Valid @RequestBody QuantityInputDTO input) {
        logger.info("REST: Add request - {}", input);
        QuantityMeasurementDTO response = service.addQuantities(input);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/subtract")
    @Operation(summary = "Subtract two quantities")
    public ResponseEntity<QuantityMeasurementDTO> subtractQuantities(
            @Valid @RequestBody QuantityInputDTO input) {
        logger.info("REST: Subtract request - {}", input);
        QuantityMeasurementDTO response = service.subtractQuantities(input);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/divide")
    @Operation(summary = "Divide two quantities (returns dimensionless ratio)")
    public ResponseEntity<QuantityMeasurementDTO> divideQuantities(
            @Valid @RequestBody QuantityInputDTO input) {
        logger.info("REST: Divide request - {}", input);
        QuantityMeasurementDTO response = service.divideQuantities(input);
        return ResponseEntity.ok(response);
    }

    // ===================== History / Query Endpoints =====================

    @GetMapping("/history")
    @Operation(summary = "Get all measurement history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getAllHistory() {
        logger.info("REST: Get all history");
        List<QuantityMeasurementDTO> history = service.getAllMeasurements();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/operation/{type}")
    @Operation(summary = "Get measurement history by operation type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByOperation(
            @PathVariable String type) {
        logger.info("REST: Get history by operation type: {}", type);
        List<QuantityMeasurementDTO> history = service.getHistoryByOperation(type);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/type/{type}")
    @Operation(summary = "Get measurement history by measurement type")
    public ResponseEntity<List<QuantityMeasurementDTO>> getHistoryByMeasurementType(
            @PathVariable String type) {
        logger.info("REST: Get history by measurement type: {}", type);
        List<QuantityMeasurementDTO> history = service.getHistoryByMeasurementType(type);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/history/errored")
    @Operation(summary = "Get error history")
    public ResponseEntity<List<QuantityMeasurementDTO>> getErrorHistory() {
        logger.info("REST: Get error history");
        List<QuantityMeasurementDTO> history = service.getErrorHistory();
        return ResponseEntity.ok(history);
    }

    @GetMapping("/count/{operationType}")
    @Operation(summary = "Get successful operation count by operation type")
    public ResponseEntity<Map<String, Object>> getCountByOperation(
            @PathVariable String operationType) {
        logger.info("REST: Get count by operation: {}", operationType);
        long count = service.getCountByOperationSuccess(operationType);
        return ResponseEntity.ok(Map.of("operationType", operationType.toUpperCase(), "count", count));
    }

    @DeleteMapping("/history")
    @Operation(summary = "Clear all measurement history")
    public ResponseEntity<Map<String, String>> clearHistory() {
        logger.info("REST: Clear history");
        service.clearHistory();
        return ResponseEntity.ok(Map.of("message", "Measurement history cleared successfully"));
    }
}