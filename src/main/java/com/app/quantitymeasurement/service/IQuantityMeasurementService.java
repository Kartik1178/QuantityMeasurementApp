package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.dto.QuantityDTO;

/**
 * UC15 Service interface.
 * All operations accept and return QuantityDTO objects.
 */
public interface IQuantityMeasurementService {

    /** Compare two quantities for equality (cross-unit same category). */
    boolean compare(QuantityDTO a, QuantityDTO b);

    /** Convert a quantity to the specified target unit. */
    QuantityDTO convert(QuantityDTO input, String targetUnit);

    /** Add two quantities; result expressed in first operand's unit. */
    QuantityDTO add(QuantityDTO a, QuantityDTO b);

    /** Subtract b from a; result expressed in first operand's unit. */
    QuantityDTO subtract(QuantityDTO a, QuantityDTO b);

    /** Divide a by b; returns dimensionless scalar. */
    double divide(QuantityDTO a, QuantityDTO b);
}
