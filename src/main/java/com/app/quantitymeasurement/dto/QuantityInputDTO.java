package com.app.quantitymeasurement.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UC17 Input DTO for REST API operations.
 * Encapsulates two quantity DTOs for binary operations (compare, add, subtract, divide).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantityInputDTO {

    @NotNull(message = "First quantity (thisQuantityDTO) must not be null")
    @Valid
    private QuantityDTO thisQuantityDTO;

    @NotNull(message = "Second quantity (thatQuantityDTO) must not be null")
    @Valid
    private QuantityDTO thatQuantityDTO;
}
