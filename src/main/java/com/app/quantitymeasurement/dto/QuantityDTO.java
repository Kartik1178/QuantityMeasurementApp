package com.app.quantitymeasurement.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UC17 Data Transfer Object for quantity input.
 * Carries value + unit name + measurement type between layers.
 * Includes validation annotations for REST API input validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityDTO {

    @NotNull(message = "Value must not be null")
    private Double value;

    @NotEmpty(message = "Unit must not be empty")
    private String unit;

    @NotEmpty(message = "Measurement type must not be empty")
    private String measurementType;

    /**
     * Convenience constructor using primitive double for backward compatibility.
     */
    public QuantityDTO(double value, String unit, String measurementType) {
        this.value = value;
        this.unit = unit;
        this.measurementType = measurementType;
    }
}
