package com.app.quantitymeasurement.controller;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UC16 Controller Layer.
 *
 * Acts as facade - delegates ALL logic to the service layer.
 * Uses SLF4J Logger instead of System.out.println.
 */
public class QuantityMeasurementController {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementController.class);

    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        if (service == null)
            throw new IllegalArgumentException("Service must not be null");
        this.service = service;
        logger.info("QuantityMeasurementController initialized");
    }

    // -- POST /quantity/compare -----------------------
    public void performComparison(QuantityDTO a, QuantityDTO b) {
        try {
            boolean result = service.compare(a, b);
            logger.info("[COMPARE] {} == {} ? {}", a, b, result);
        } catch (Exception e) {
            logger.error("[COMPARE ERROR] {}", e.getMessage());
        }
    }

    // -- POST /quantity/convert -----------------------
    public void performConversion(QuantityDTO input, String targetUnit) {
        try {
            QuantityDTO result = service.convert(input, targetUnit);
            logger.info("[CONVERT] {} -> {}", input, result);
        } catch (Exception e) {
            logger.error("[CONVERT ERROR] {}", e.getMessage());
        }
    }

    // -- POST /quantity/add ---------------------------
    public void performAddition(QuantityDTO a, QuantityDTO b) {
        try {
            QuantityDTO result = service.add(a, b);
            logger.info("[ADD] {} + {} = {}", a, b, result);
        } catch (Exception e) {
            logger.error("[ADD ERROR] {}", e.getMessage());
        }
    }

    // -- POST /quantity/subtract ----------------------
    public void performSubtraction(QuantityDTO a, QuantityDTO b) {
        try {
            QuantityDTO result = service.subtract(a, b);
            logger.info("[SUBTRACT] {} - {} = {}", a, b, result);
        } catch (Exception e) {
            logger.error("[SUBTRACT ERROR] {}", e.getMessage());
        }
    }

    // -- POST /quantity/divide ------------------------
    public void performDivision(QuantityDTO a, QuantityDTO b) {
        try {
            double result = service.divide(a, b);
            logger.info("[DIVIDE] {} / {} = {}", a, b, result);
        } catch (Exception e) {
            logger.error("[DIVIDE ERROR] {}", e.getMessage());
        }
    }
}
