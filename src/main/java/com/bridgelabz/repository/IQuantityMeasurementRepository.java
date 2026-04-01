package com.bridgelabz.repository;

import com.bridgelabz.entity.QuantityMeasurementEntity;
import java.util.List;

/**
 * UC15 Repository interface - Interface Segregation Principle.
 * Allows swapping in-memory vs database implementations.
 */
public interface IQuantityMeasurementRepository {

    void save(QuantityMeasurementEntity entity);

    List<QuantityMeasurementEntity> findAll();

    void clear();
}
