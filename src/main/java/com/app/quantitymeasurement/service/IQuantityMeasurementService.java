package com.app.quantitymeasurement.service;

import com.app.quantitymeasurement.dto.QuantityInputDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;

import java.util.List;

/**
 * UC17 Service interface for quantity measurement operations.
 * All operations accept QuantityInputDTO and return QuantityMeasurementDTO.
 */
public interface IQuantityMeasurementService {

    /** Compare two quantities for equality (cross-unit same category). */
    QuantityMeasurementDTO compareQuantities(QuantityInputDTO input);

    /** Convert a quantity to the target unit specified in thatQuantityDTO. */
    QuantityMeasurementDTO convertQuantity(QuantityInputDTO input);

    /** Add two quantities; result expressed in first operand's unit. */
    QuantityMeasurementDTO addQuantities(QuantityInputDTO input);

    /** Subtract second from first; result expressed in first operand's unit. */
    QuantityMeasurementDTO subtractQuantities(QuantityInputDTO input);

    /** Divide first by second; returns dimensionless scalar result. */
    QuantityMeasurementDTO divideQuantities(QuantityInputDTO input);

    /** Get all measurement history. */
    List<QuantityMeasurementDTO> getAllMeasurements();

    /** Get history by operation type (e.g., COMPARE, ADD). */
    List<QuantityMeasurementDTO> getHistoryByOperation(String operationType);

    /** Get history by measurement type (e.g., LengthUnit, WeightUnit). */
    List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType);

    /** Get error history. */
    List<QuantityMeasurementDTO> getErrorHistory();

    /** Get count of successful operations by operation type. */
    long getCountByOperationSuccess(String operationType);

    /** Get total measurement count. */
    long getTotalCount();

    /** Clear all measurement history. */
    void clearHistory();
}
