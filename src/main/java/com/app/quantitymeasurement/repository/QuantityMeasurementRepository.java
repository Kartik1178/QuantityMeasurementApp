package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UC17 Spring Data JPA Repository for QuantityMeasurementEntity.
 * Extends JpaRepository to provide built-in CRUD operations.
 * Defines custom query methods using Spring Data naming conventions
 * and @Query annotations for complex queries.
 */
@Repository
public interface QuantityMeasurementRepository extends JpaRepository<QuantityMeasurementEntity, Long> {

    /**
     * Find all measurements by operation type (e.g., COMPARE, ADD, CONVERT).
     */
    List<QuantityMeasurementEntity> findByOperationType(String operationType);

    /**
     * Find all measurements by measurement type (e.g., LENGTH, WEIGHT).
     */
    List<QuantityMeasurementEntity> findByThisMeasurementType(String measurementType);

    /**
     * Find all measurements created after a given date.
     */
    List<QuantityMeasurementEntity> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Find all successful operations by operation type using custom JPQL query.
     */
    @Query("SELECT q FROM QuantityMeasurementEntity q WHERE q.operationType = :operation AND q.isError = false")
    List<QuantityMeasurementEntity> findSuccessfulByOperation(@Param("operation") String operation);

    /**
     * Count successful operations by operation type.
     */
    long countByOperationTypeAndIsErrorFalse(String operationType);

    /**
     * Find all error records.
     */
    List<QuantityMeasurementEntity> findByIsErrorTrue();
}
