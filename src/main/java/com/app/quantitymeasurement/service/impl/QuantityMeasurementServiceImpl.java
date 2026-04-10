package com.app.quantitymeasurement.service.impl;

import com.app.quantitymeasurement.unit.IMeasurable;
import com.app.quantitymeasurement.unit.LengthUnit;
import com.app.quantitymeasurement.unit.Quantity;
import com.app.quantitymeasurement.unit.TemperatureUnit;
import com.app.quantitymeasurement.unit.VolumeUnit;
import com.app.quantitymeasurement.unit.WeightUnit;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UC16 Service Implementation.
 *
 * Responsibilities:
 * - Accept QuantityDTO input
 * - Resolve IMeasurable units via resolveUnit()
 * - Delegate operations to Quantity<U> (UC14 logic - unchanged)
 * - Save results to repository
 * - Return QuantityDTO output
 * - Wrap exceptions as QuantityMeasurementException
 */
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    private final IQuantityMeasurementRepository repository;

    public QuantityMeasurementServiceImpl(IQuantityMeasurementRepository repository) {
        this.repository = repository;
        logger.info("QuantityMeasurementServiceImpl initialized with repository: {}",
                repository.getClass().getSimpleName());
    }

    // -- COMPARE ------------------------------------------

    @Override
    public boolean compare(QuantityDTO a, QuantityDTO b) {
        try {
            logger.info("Comparing: {} vs {}", a, b);
            validateSameType(a, b);
            Quantity<?> q1 = toQuantity(a);
            Quantity<?> q2 = toQuantity(b);
            boolean result = q1.equals(q2);
            repository.save(new QuantityMeasurementEntity(
                    a.getValue(), a.getUnit(),
                    b.getValue(), b.getUnit(),
                    result));
            logger.info("Compare result: {}", result);
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Compare failed: " + e.getMessage(), e);
        }
    }

    // -- CONVERT ------------------------------------------

    @Override
    public QuantityDTO convert(QuantityDTO input, String targetUnitName) {
        try {
            logger.info("Converting: {} to {}", input, targetUnitName);
            Quantity<?> q = toQuantity(input);
            IMeasurable targetUnit = resolveUnit(targetUnitName, input.getType());

            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity<IMeasurable>) q).convertTo((IMeasurable) targetUnit);

            QuantityDTO dto = toDTO(result, input.getType());
            repository.save(new QuantityMeasurementEntity(
                    input.getValue(), input.getUnit(),
                    "CONVERT",
                    dto.getValue(), dto.getUnit()));
            logger.info("Convert result: {}", dto);
            return dto;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Convert failed: " + e.getMessage(), e);
        }
    }

    // -- ADD ----------------------------------------------

    @Override
    public QuantityDTO add(QuantityDTO a, QuantityDTO b) {
        try {
            logger.info("Adding: {} + {}", a, b);
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
            logger.info("Add result: {}", dto);
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

    // -- SUBTRACT -----------------------------------------

    @Override
    public QuantityDTO subtract(QuantityDTO a, QuantityDTO b) {
        try {
            logger.info("Subtracting: {} - {}", a, b);
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
            logger.info("Subtract result: {}", dto);
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

    // -- DIVIDE -------------------------------------------

    @Override
    public double divide(QuantityDTO a, QuantityDTO b) {
        try {
            logger.info("Dividing: {} / {}", a, b);
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
            logger.info("Divide result: {}", result);
            return result;
        } catch (QuantityMeasurementException e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw e;
        } catch (Exception e) {
            repository.save(new QuantityMeasurementEntity(e.getMessage()));
            throw new QuantityMeasurementException("Divide failed: " + e.getMessage(), e);
        }
    }

    // -- HELPERS ------------------------------------------

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
            case "LENGTH" -> LengthUnit.valueOf(unitName.toUpperCase());
            case "WEIGHT" -> WeightUnit.valueOf(unitName.toUpperCase());
            case "VOLUME" -> VolumeUnit.valueOf(unitName.toUpperCase());
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
