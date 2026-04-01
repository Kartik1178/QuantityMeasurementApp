package com.bridgelabz.dto;

/**
 * UC15 Data Transfer Object.
 * Carries value + unit name + measurement type between layers.
 * Example: new QuantityDTO(1.0, "FEET", "LENGTH")
 */
public class QuantityDTO {

    private double value;
    private String unit;   // e.g. "FEET", "KILOGRAM"
    private String type;   // e.g. "LENGTH", "WEIGHT", "VOLUME", "TEMPERATURE"

    public QuantityDTO() {}

    public QuantityDTO(double value, String unit, String type) {
        this.value = value;
        this.unit  = unit;
        this.type  = type;
    }

    public double getValue()        { return value; }
    public String getUnit()         { return unit;  }
    public String getType()         { return type;  }

    public void setValue(double v)  { this.value = v; }
    public void setUnit(String u)   { this.unit  = u; }
    public void setType(String t)   { this.type  = t; }

    @Override
    public String toString() {
        return "QuantityDTO{value=" + value + ", unit='" + unit + "', type='" + type + "'}";
    }
}
