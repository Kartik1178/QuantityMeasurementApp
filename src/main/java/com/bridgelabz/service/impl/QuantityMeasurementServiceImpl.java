package com.bridgelabz.service.impl;

import com.bridgelabz.IMeasurable;
import com.bridgelabz.LengthUnit;
import com.bridgelabz.Quantity;
import com.bridgelabz.TemperatureUnit;
import com.bridgelabz.VolumeUnit;
import com.bridgelabz.WeightUnit;
import com.bridgelabz.dto.QuantityDTO;
import com.bridgelabz.entity.QuantityMeasurementEntity;
import com.bridgelabz.exception.QuantityMeasurementException;
import com.bridgelabz.repository.IQuantityMeasurementRepository;
import com.bridgelabz.service.IQuantityMeasurementService;

/**
 * UC15 Service Implementation.
 *
 * Responsibilities:
 *   - Accept QuantityDTO input
 *   - Resolve IMeasurable units via resolveUnit()
 *   - Delegate operations to Quantity<U> (UC14 logic - unchanged)
 *   - Save results to repository
 *   - Return QuantityDTO output
 *   - Wrap exceptions as QuantityMeasurementException
 */
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private final IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = repository;
    }

    // â”€â”€ COMPARE â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public boolean compare(QuantityDTO a, QuantityDTO b) {
        try {
            validateSameType(a, b);
            Quantity<?> q1 = toQuantity(a);
            Quantity<?> q2 = toQuantity(b);
            boolean result = q1.equals(q2);
            repository.save(new QuantityMeasurementEntity(
                    a.getValue(), a.getUnit(),
                    b.getValue(), b.getUnit(),
                    result));
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Compare failed: " + e.getMessage(), e);
        }
    }

    // â”€â”€ CONVERT â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public QuantityDTO convert(QuantityDTO input, String targetUnitName) {
        try {
            Quantity<?> q = toQuantity(input);
            IMeasurable targetUnit = resolveUnit(targetUnitName, input.getType());

            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity<IMeasurable>) q).convertTo((IMeasurable) targetUnit);

            QuantityDTO dto = toDTO(result, input.getType());
            repository.save(new QuantityMeasurementEntity(
                    input.getValue(), input.getUnit(),
                    "CONVERT",
                    dto.getValue(), dto.getUnit()));
            return dto;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Convert failed: " + e.getMessage(), e);
        }
    }

    // â”€â”€ ADD â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public QuantityDTO add(QuantityDTO a, QuantityDTO b) {
        try {
            validateSameType(a, b);
            Quantity<?> q1 = toQuantity(a);
            Quantity<?> q2 = toQuantity(b);

            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity<IMeasurable>) q1).add((Quantity<IMeasurable>) q2);

            QuantityDTO dto = toDTO(result, a.getType());
            repository.save(new QuantityMeasurementEntity(
                    a.getValue(), a.getUnit(),
                    b.getValue(), b.getUnit(),
                    "ADD",
                    dto.getValue(), dto.getUnit()));
            return dto;
        } catch (UnsupportedOperationException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException(e.getMessage(), e);
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Add failed: " + e.getMessage(), e);
        }
    }

    // â”€â”€ SUBTRACT â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public QuantityDTO subtract(QuantityDTO a, QuantityDTO b) {
        try {
            validateSameType(a, b);
            Quantity<?> q1 = toQuantity(a);
            Quantity<?> q2 = toQuantity(b);

            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity<IMeasurable>) q1).subtract((Quantity<IMeasurable>) q2);

            QuantityDTO dto = toDTO(result, a.getType());
            repository.save(new QuantityMeasurementEntity(
                    a.getValue(), a.getUnit(),
                    b.getValue(), b.getUnit(),
                    "SUBTRACT",
                    dto.getValue(), dto.getUnit()));
            return dto;
        } catch (UnsupportedOperationException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException(e.getMessage(), e);
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Subtract failed: " + e.getMessage(), e);
        }
    }

    // â”€â”€ DIVIDE â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public double divide(QuantityDTO a, QuantityDTO b) {
        try {
            validateSameType(a, b);
            Quantity<?> q1 = toQuantity(a);
            Quantity<?> q2 = toQuantity(b);

            @SuppressWarnings("unchecked")
            double result = ((Quantity<IMeasurable>) q1).divide((Quantity<IMeasurable>) q2);

            repository.save(new QuantityMeasurementEntity(
                    a.getValue(), a.getUnit(),
                    b.getValue(), b.getUnit(),
                    "DIVIDE",
                    result, "SCALAR"));
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Divide failed: " + e.getMessage(), e);
        }
    }

    // â”€â”€ HELPERS â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private Quantity<IMeasurable> toQuantity(QuantityDTO dto) {
        IMeasurable unit = resolveUnit(dto.getUnit(), dto.getType());
        return new Quantity<>(dto.getValue(), unit);
    }

    private QuantityDTO toDTO(Quantity<?> q, String type) {
        return new QuantityDTO(q.getValue(), q.getUnit().getUnitName(), type);
    }

    private IMeasurable resolveUnit(String unitName, String type) {
        if (unitName == null || type == null)
            throw new QuantityMeasurementException("Unit and type must not be null");
        return switch (type.toUpperCase()) {
            case "LENGTH"      -> LengthUnit.valueOf(unitName.toUpperCase());
            case "WEIGHT"      -> WeightUnit.valueOf(unitName.toUpperCase());
            case "VOLUME"      -> VolumeUnit.valueOf(unitName.toUpperCase());
            case "TEMPERATURE" -> TemperatureUnit.valueOf(unitName.toUpperCase());
            default -> throw new QuantityMeasurementException("Unknown measurement type: " + type);
        };
    }

    private void validateSameType(QuantityDTO a, QuantityDTO b) {
        if (!a.getType().equalsIgnoreCase(b.getType()))
            throw new QuantityMeasurementException(
                    "Cross-category operation not allowed: " + a.getType() + " vs " + b.getType());
    }
}
