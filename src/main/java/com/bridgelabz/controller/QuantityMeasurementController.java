package com.bridgelabz.controller;

import com.bridgelabz.dto.QuantityDTO;
import com.bridgelabz.service.IQuantityMeasurementService;

/**
 * UC15 Controller Layer.
 *
 * Acts as facade - delegates ALL logic to the service layer.
 * Designed to be easily wrapped as a REST controller later
 * (Spring @RestController + @RequestMapping).
 *
 * Renamed from demonstrateXXX -> performXXX to signal
 * REST-readiness (future: map to POST /quantity/add etc.)
 */
public class QuantityMeasurementController {

    private final IQuantityMeasurementService service;

    public QuantityMeasurementController(IQuantityMeasurementService service) {
        if (service == null)
            throw new IllegalArgumentException("Service must not be null");
        this.service = service;
    }

    // â”€â”€ POST /quantity/compare â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public void performComparison(QuantityDTO a, QuantityDTO b) {
        try {
            boolean result = service.compare(a, b);
            System.out.println("[COMPARE] " + a + " == " + b + " ? " + result);
        } catch (Exception e) {
            System.out.println("[COMPARE ERROR] " + e.getMessage());
        }
    }

    // â”€â”€ POST /quantity/convert â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public void performConversion(QuantityDTO input, String targetUnit) {
        try {
            QuantityDTO result = service.convert(input, targetUnit);
            System.out.println("[CONVERT] " + input + " -> " + result);
        } catch (Exception e) {
            System.out.println("[CONVERT ERROR] " + e.getMessage());
        }
    }

    // â”€â”€ POST /quantity/add â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public void performAddition(QuantityDTO a, QuantityDTO b) {
        try {
            QuantityDTO result = service.add(a, b);
            System.out.println("[ADD] " + a + " + " + b + " = " + result);
        } catch (Exception e) {
            System.out.println("[ADD ERROR] " + e.getMessage());
        }
    }

    // â”€â”€ POST /quantity/subtract â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public void performSubtraction(QuantityDTO a, QuantityDTO b) {
        try {
            QuantityDTO result = service.subtract(a, b);
            System.out.println("[SUBTRACT] " + a + " - " + b + " = " + result);
        } catch (Exception e) {
            System.out.println("[SUBTRACT ERROR] " + e.getMessage());
        }
    }

    // â”€â”€ POST /quantity/divide â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public void performDivision(QuantityDTO a, QuantityDTO b) {
        try {
            double result = service.divide(a, b);
            System.out.println("[DIVIDE] " + a + " / " + b + " = " + result);
        } catch (Exception e) {
            System.out.println("[DIVIDE ERROR] " + e.getMessage());
        }
    }
}
