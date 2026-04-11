package com.app.quantitymeasurement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UC17 JPA Entity for persisting quantity measurement operations.
 * Mapped to the 'quantity_measurements' table with JPA annotations.
 * Uses Lombok to reduce boilerplate code.
 */
@Entity
@Table(name = "quantity_measurements", indexes = {
        @Index(name = "idx_operation_type", columnList = "operationType"),
        @Index(name = "idx_measurement_type", columnList = "thisMeasurementType"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_is_error", columnList = "isError")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "this_value")
    private double thisValue;

    @Column(name = "this_unit")
    private String thisUnit;

    @Column(name = "this_measurement_type")
    private String thisMeasurementType;

    @Column(name = "that_value")
    private double thatValue;

    @Column(name = "that_unit")
    private String thatUnit;

    @Column(name = "that_measurement_type")
    private String thatMeasurementType;

    @Column(name = "operation_type", nullable = false)
    private String operationType;

    @Column(name = "result_string")
    private String resultString;

    @Column(name = "result_value")
    private double resultValue;

    @Column(name = "result_unit")
    private String resultUnit;

    @Column(name = "result_measurement_type")
    private String resultMeasurementType;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "is_error")
    private boolean isError;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Lifecycle callback to set timestamps on persist.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Lifecycle callback to update timestamp on update.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
