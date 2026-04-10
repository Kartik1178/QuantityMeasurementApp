package com.app.quantitymeasurement.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * UC16 Persistence entity stored in the repository.
 * Immutable by design - use constructors matching the operation type.
 */
public class QuantityMeasurementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    // Operands (raw DTO representation)
    private double operand1Value;
    private String operand1Unit;
    private double operand2Value;
    private String operand2Unit;

    // Operation metadata
    private String operationType; // ADD, SUBTRACT, DIVIDE, CONVERT, COMPARE

    // Result
    private double resultValue;
    private String resultUnit;
    private boolean booleanResult; // for COMPARE

    // Error
    private boolean error;
    private String errorMessage;

    // Timestamp
    private LocalDateTime createdAt;

    /** Default constructor for JDBC result set mapping */
    public QuantityMeasurementEntity() {
        this.createdAt = LocalDateTime.now();
    }

    /** Constructor for binary arithmetic operations (ADD, SUBTRACT) */
    public QuantityMeasurementEntity(
            double op1Val, String op1Unit,
            double op2Val, String op2Unit,
            String opType,
            double resVal, String resUnit) {
        this.operand1Value = op1Val;
        this.operand1Unit = op1Unit;
        this.operand2Value = op2Val;
        this.operand2Unit = op2Unit;
        this.operationType = opType;
        this.resultValue = resVal;
        this.resultUnit = resUnit;
        this.error = false;
        this.createdAt = LocalDateTime.now();
    }

    /** Constructor for COMPARE operation */
    public QuantityMeasurementEntity(
            double op1Val, String op1Unit,
            double op2Val, String op2Unit,
            boolean compareResult) {
        this.operand1Value = op1Val;
        this.operand1Unit = op1Unit;
        this.operand2Value = op2Val;
        this.operand2Unit = op2Unit;
        this.operationType = "COMPARE";
        this.booleanResult = compareResult;
        this.error = false;
        this.createdAt = LocalDateTime.now();
    }

    /** Constructor for CONVERT / DIVIDE (single primary operand) */
    public QuantityMeasurementEntity(
            double op1Val, String op1Unit,
            String opType,
            double resVal, String resUnit) {
        this.operand1Value = op1Val;
        this.operand1Unit = op1Unit;
        this.operationType = opType;
        this.resultValue = resVal;
        this.resultUnit = resUnit;
        this.error = false;
        this.createdAt = LocalDateTime.now();
    }

    /** Error constructor */
    public QuantityMeasurementEntity(String errorMessage) {
        this.error = true;
        this.errorMessage = errorMessage;
        this.createdAt = LocalDateTime.now();
    }

    // -- Getters -------------------------------------------
    public long getId() {
        return id;
    }

    public double getOperand1Value() {
        return operand1Value;
    }

    public String getOperand1Unit() {
        return operand1Unit;
    }

    public double getOperand2Value() {
        return operand2Value;
    }

    public String getOperand2Unit() {
        return operand2Unit;
    }

    public String getOperationType() {
        return operationType;
    }

    public double getResultValue() {
        return resultValue;
    }

    public String getResultUnit() {
        return resultUnit;
    }

    public boolean getBooleanResult() {
        return booleanResult;
    }

    public boolean isError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // -- Setters -------------------------------------------
    public void setId(long id) {
        this.id = id;
    }

    public void setOperand1Value(double v) {
        this.operand1Value = v;
    }

    public void setOperand1Unit(String u) {
        this.operand1Unit = u;
    }

    public void setOperand2Value(double v) {
        this.operand2Value = v;
    }

    public void setOperand2Unit(String u) {
        this.operand2Unit = u;
    }

    public void setOperationType(String t) {
        this.operationType = t;
    }

    public void setResultValue(double v) {
        this.resultValue = v;
    }

    public void setResultUnit(String u) {
        this.resultUnit = u;
    }

    public void setBooleanResult(boolean b) {
        this.booleanResult = b;
    }

    public void setError(boolean e) {
        this.error = e;
    }

    public void setErrorMessage(String m) {
        this.errorMessage = m;
    }

    public void setCreatedAt(LocalDateTime t) {
        this.createdAt = t;
    }

    @Override
    public String toString() {
        if (error)
            return "Entity[ERROR: " + errorMessage + "]";
        if ("COMPARE".equals(operationType))
            return "Entity[" + operand1Value + " " + operand1Unit +
                    " " + operationType + " " + operand2Value + " " + operand2Unit +
                    " = " + booleanResult + "]";
        return "Entity[" + operand1Value + " " + operand1Unit +
                " " + operationType + " " + operand2Value + " " + operand2Unit +
                " = " + resultValue + " " + resultUnit + "]";
    }
}
