package com.bridgelabz.entity;

import java.io.Serializable;

/**
 * UC15 Persistence entity stored in the repository.
 * Immutable by design - use constructors matching the operation type.
 */
public class QuantityMeasurementEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    // Operands (raw DTO representation)
    private double operand1Value;
    private String operand1Unit;
    private double operand2Value;
    private String operand2Unit;

    // Operation metadata
    private String operationType;   // ADD, SUBTRACT, DIVIDE, CONVERT, COMPARE

    // Result
    private double resultValue;
    private String resultUnit;
    private boolean booleanResult;  // for COMPARE

    // Error
    private boolean error;
    private String  errorMessage;

    /** Constructor for binary arithmetic operations (ADD, SUBTRACT) */
    public QuantityMeasurementEntity(
            double op1Val, String op1Unit,
            double op2Val, String op2Unit,
            String opType,
            double resVal, String resUnit) {
        this.operand1Value  = op1Val;
        this.operand1Unit   = op1Unit;
        this.operand2Value  = op2Val;
        this.operand2Unit   = op2Unit;
        this.operationType  = opType;
        this.resultValue    = resVal;
        this.resultUnit     = resUnit;
        this.error          = false;
    }

    /** Constructor for COMPARE operation */
    public QuantityMeasurementEntity(
            double op1Val, String op1Unit,
            double op2Val, String op2Unit,
            boolean compareResult) {
        this.operand1Value  = op1Val;
        this.operand1Unit   = op1Unit;
        this.operand2Value  = op2Val;
        this.operand2Unit   = op2Unit;
        this.operationType  = "COMPARE";
        this.booleanResult  = compareResult;
        this.error          = false;
    }

    /** Constructor for CONVERT / DIVIDE (single primary operand) */
    public QuantityMeasurementEntity(
            double op1Val, String op1Unit,
            String opType,
            double resVal, String resUnit) {
        this.operand1Value  = op1Val;
        this.operand1Unit   = op1Unit;
        this.operationType  = opType;
        this.resultValue    = resVal;
        this.resultUnit     = resUnit;
        this.error          = false;
    }

    /** Error constructor */
    public QuantityMeasurementEntity(String errorMessage) {
        this.error        = true;
        this.errorMessage = errorMessage;
    }

    // â”€â”€ Getters â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public double  getOperand1Value()  { return operand1Value;  }
    public String  getOperand1Unit()   { return operand1Unit;   }
    public double  getOperand2Value()  { return operand2Value;  }
    public String  getOperand2Unit()   { return operand2Unit;   }
    public String  getOperationType()  { return operationType;  }
    public double  getResultValue()    { return resultValue;    }
    public String  getResultUnit()     { return resultUnit;     }
    public boolean getBooleanResult()  { return booleanResult;  }
    public boolean isError()           { return error;          }
    public String  getErrorMessage()   { return errorMessage;   }

    @Override
    public String toString() {
        if (error) return "Entity[ERROR: " + errorMessage + "]";
        if ("COMPARE".equals(operationType))
            return "Entity[" + operand1Value + " " + operand1Unit +
                   " " + operationType + " " + operand2Value + " " + operand2Unit +
                   " = " + booleanResult + "]";
        return "Entity[" + operand1Value + " " + operand1Unit +
               " " + operationType + " " + operand2Value + " " + operand2Unit +
               " = " + resultValue + " " + resultUnit + "]";
    }
}
