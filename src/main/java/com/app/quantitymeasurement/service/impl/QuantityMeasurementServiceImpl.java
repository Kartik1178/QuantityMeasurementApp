package com.app.quantitymeasurement.service.impl;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.dto.QuantityInputDTO;
import com.app.quantitymeasurement.dto.QuantityMeasurementDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.unit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UC17 Service Implementation using Spring @Service and Spring Data JPA.
 *
 * Responsibilities:
 * - Accept QuantityInputDTO from controllers
 * - Resolve IMeasurable units via resolveUnit()
 * - Delegate operations to Quantity<U> (core business logic - unchanged from UC14)
 * - Save results to JPA repository
 * - Return QuantityMeasurementDTO output
 * - Wrap exceptions as QuantityMeasurementException
 */
@Service
public class QuantityMeasurementServiceImpl implements IQuantityMeasurementService {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementServiceImpl.class);

    @Autowired
    private QuantityMeasurementRepository repository;

    // -- COMPARE ------------------------------------------

    @Override
    public QuantityMeasurementDTO compareQuantities(QuantityInputDTO input) {
        QuantityDTO thisDTO = input.getThisQuantityDTO();
        QuantityDTO thatDTO = input.getThatQuantityDTO();
        try {
            logger.info("Comparing: {} vs {}", thisDTO, thatDTO);
            validateSameType(thisDTO, thatDTO);
            Quantity<?> q1 = toQuantity(thisDTO);
            Quantity<?> q2 = toQuantity(thatDTO);
            boolean result = q1.equals(q2);

            QuantityMeasurementEntity entity = buildEntity(thisDTO, thatDTO, "COMPARE");
            entity.setResultString(String.valueOf(result));
            repository.save(entity);

            logger.info("Compare result: {}", result);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "COMPARE", e);
        } catch (Exception e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "COMPARE",
                    new QuantityMeasurementException("Compare failed: " + e.getMessage(), e));
        }
    }

    // -- CONVERT ------------------------------------------

    @Override
    public QuantityMeasurementDTO convertQuantity(QuantityInputDTO input) {
        QuantityDTO thisDTO = input.getThisQuantityDTO();
        QuantityDTO thatDTO = input.getThatQuantityDTO();
        try {
            logger.info("Converting: {} to {}", thisDTO, thatDTO.getUnit());
            Quantity<?> q = toQuantity(thisDTO);
            IMeasurable targetUnit = resolveUnit(thatDTO.getUnit(), thisDTO.getMeasurementType());

            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity<IMeasurable>) q).convertTo((IMeasurable) targetUnit);

            QuantityMeasurementEntity entity = buildEntity(thisDTO, thatDTO, "CONVERT");
            entity.setResultValue(result.getValue());
            repository.save(entity);

            logger.info("Convert result: {} {}", result.getValue(), result.getUnit().getUnitName());
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "CONVERT", e);
        } catch (Exception e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "CONVERT",
                    new QuantityMeasurementException("Convert failed: " + e.getMessage(), e));
        }
    }

    // -- ADD ----------------------------------------------

    @Override
    public QuantityMeasurementDTO addQuantities(QuantityInputDTO input) {
        QuantityDTO thisDTO = input.getThisQuantityDTO();
        QuantityDTO thatDTO = input.getThatQuantityDTO();
        try {
            logger.info("Adding: {} + {}", thisDTO, thatDTO);
            validateSameType(thisDTO, thatDTO);
            Quantity<?> q1 = toQuantity(thisDTO);
            Quantity<?> q2 = toQuantity(thatDTO);

            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity<IMeasurable>) q1).add((Quantity<IMeasurable>) q2);

            QuantityMeasurementEntity entity = buildEntity(thisDTO, thatDTO, "ADD");
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(thisDTO.getMeasurementType());
            repository.save(entity);

            logger.info("Add result: {} {}", result.getValue(), result.getUnit().getUnitName());
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (UnsupportedOperationException e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "ADD",
                    new QuantityMeasurementException(e.getMessage(), e));
        } catch (QuantityMeasurementException e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "ADD", e);
        } catch (Exception e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "ADD",
                    new QuantityMeasurementException("add Error: " + e.getMessage(), e));
        }
    }

    // -- SUBTRACT -----------------------------------------

    @Override
    public QuantityMeasurementDTO subtractQuantities(QuantityInputDTO input) {
        QuantityDTO thisDTO = input.getThisQuantityDTO();
        QuantityDTO thatDTO = input.getThatQuantityDTO();
        try {
            logger.info("Subtracting: {} - {}", thisDTO, thatDTO);
            validateSameType(thisDTO, thatDTO);
            Quantity<?> q1 = toQuantity(thisDTO);
            Quantity<?> q2 = toQuantity(thatDTO);

            @SuppressWarnings("unchecked")
            Quantity<?> result = ((Quantity<IMeasurable>) q1).subtract((Quantity<IMeasurable>) q2);

            QuantityMeasurementEntity entity = buildEntity(thisDTO, thatDTO, "SUBTRACT");
            entity.setResultValue(result.getValue());
            entity.setResultUnit(result.getUnit().getUnitName());
            entity.setResultMeasurementType(thisDTO.getMeasurementType());
            repository.save(entity);

            logger.info("Subtract result: {} {}", result.getValue(), result.getUnit().getUnitName());
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (UnsupportedOperationException e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "SUBTRACT",
                    new QuantityMeasurementException(e.getMessage(), e));
        } catch (QuantityMeasurementException e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "SUBTRACT", e);
        } catch (Exception e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "SUBTRACT",
                    new QuantityMeasurementException("Subtract failed: " + e.getMessage(), e));
        }
    }

    // -- DIVIDE -------------------------------------------

    @Override
    public QuantityMeasurementDTO divideQuantities(QuantityInputDTO input) {
        QuantityDTO thisDTO = input.getThisQuantityDTO();
        QuantityDTO thatDTO = input.getThatQuantityDTO();
        try {
            logger.info("Dividing: {} / {}", thisDTO, thatDTO);
            validateSameType(thisDTO, thatDTO);
            Quantity<?> q1 = toQuantity(thisDTO);
            Quantity<?> q2 = toQuantity(thatDTO);

            @SuppressWarnings("unchecked")
            double result = ((Quantity<IMeasurable>) q1).divide((Quantity<IMeasurable>) q2);

            QuantityMeasurementEntity entity = buildEntity(thisDTO, thatDTO, "DIVIDE");
            entity.setResultValue(result);
            repository.save(entity);

            logger.info("Divide result: {}", result);
            return QuantityMeasurementDTO.fromEntity(entity);
        } catch (QuantityMeasurementException e) {
            return saveErrorAndThrow(thisDTO, thatDTO, "DIVIDE", e);
        } catch (Exception e) {
            throw new QuantityMeasurementException("Divide by zero", e);
        }
    }

    // -- HISTORY & QUERY METHODS --------------------------

    @Override
    public List<QuantityMeasurementDTO> getAllMeasurements() {
        return QuantityMeasurementDTO.fromEntityList(repository.findAll());
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByOperation(String operationType) {
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByOperationType(operationType.toUpperCase()));
    }

    @Override
    public List<QuantityMeasurementDTO> getHistoryByMeasurementType(String measurementType) {
        return QuantityMeasurementDTO.fromEntityList(
                repository.findByThisMeasurementType(measurementType));
    }

    @Override
    public List<QuantityMeasurementDTO> getErrorHistory() {
        return QuantityMeasurementDTO.fromEntityList(repository.findByIsErrorTrue());
    }

    @Override
    public long getCountByOperationSuccess(String operationType) {
        return repository.countByOperationTypeAndIsErrorFalse(operationType.toUpperCase());
    }

    @Override
    public long getTotalCount() {
        return repository.count();
    }

    @Override
    public void clearHistory() {
        repository.deleteAll();
        logger.info("All measurement history cleared");
    }

    // -- HELPERS ------------------------------------------

    private Quantity<IMeasurable> toQuantity(QuantityDTO dto) {
        IMeasurable unit = resolveUnit(dto.getUnit(), dto.getMeasurementType());
        return new Quantity<>(dto.getValue(), unit);
    }

    private IMeasurable resolveUnit(String unitName, String measurementType) {
        if (unitName == null || measurementType == null)
            throw new QuantityMeasurementException("Unit and measurement type must not be null");
        try {
            return switch (measurementType) {
                case "LengthUnit" -> LengthUnit.valueOf(unitName.toUpperCase());
                case "WeightUnit" -> WeightUnit.valueOf(unitName.toUpperCase());
                case "VolumeUnit" -> VolumeUnit.valueOf(unitName.toUpperCase());
                case "TemperatureUnit" -> TemperatureUnit.valueOf(unitName.toUpperCase());
                default -> throw new QuantityMeasurementException(
                        "Unknown measurement type: " + measurementType);
            };
        } catch (IllegalArgumentException e) {
            throw new QuantityMeasurementException(
                    "Unit must be valid for the specified measurement type", e);
        }
    }

    private void validateSameType(QuantityDTO a, QuantityDTO b) {
        if (!a.getMeasurementType().equalsIgnoreCase(b.getMeasurementType()))
            throw new QuantityMeasurementException(
                    "Cannot perform arithmetic between different measurement categories: "
                            + a.getMeasurementType() + " and " + b.getMeasurementType());
    }

    private QuantityMeasurementEntity buildEntity(QuantityDTO thisDTO, QuantityDTO thatDTO, String operation) {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setThisValue(thisDTO.getValue());
        entity.setThisUnit(thisDTO.getUnit());
        entity.setThisMeasurementType(thisDTO.getMeasurementType());
        entity.setThatValue(thatDTO.getValue());
        entity.setThatUnit(thatDTO.getUnit());
        entity.setThatMeasurementType(thatDTO.getMeasurementType());
        entity.setOperationType(operation.toLowerCase());
        entity.setError(false);
        return entity;
    }

    private QuantityMeasurementDTO saveErrorAndThrow(QuantityDTO thisDTO, QuantityDTO thatDTO,
                                                      String operation, QuantityMeasurementException e) {
        QuantityMeasurementEntity entity = buildEntity(
                thisDTO != null ? thisDTO : new QuantityDTO(),
                thatDTO != null ? thatDTO : new QuantityDTO(),
                operation);
        entity.setError(true);
        entity.setErrorMessage(e.getMessage());
        repository.save(entity);
        throw e;
    }
}
