package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import java.util.List;

/**
 * UC16 Repository interface - Interface Segregation Principle.
 * Allows swapping in-memory vs database implementations.
 * Extended with query/delete/count methods for UC16.
 */
public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> findAll();

    void clear();

    /** UC16: Get all measurements (alias for findAll for clarity) */
    List<QuantityMeasurementEntity> getAllMeasurements();

    /** UC16: Filter measurements by operation type (ADD, SUBTRACT, etc.) */
    List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType);

    /** UC16: Filter measurements by unit type (e.g. FEET, KILOGRAM) */
    List<QuantityMeasurementEntity> getMeasurementsByType(String unitType);

    /** UC16: Delete all measurements */
    void deleteAll();

    /** UC16: Get total count of measurements */
    int getTotalCount();
}
