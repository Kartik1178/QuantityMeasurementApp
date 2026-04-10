-- UC16 Database Schema for Quantity Measurement App
-- H2 Database

-- Main entity table for storing measurement operations
CREATE TABLE IF NOT EXISTS quantity_measurement_entity (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    operand1_value  DOUBLE,
    operand1_unit   VARCHAR(50),
    operand2_value  DOUBLE,
    operand2_unit   VARCHAR(50),
    operation_type  VARCHAR(20),
    result_value    DOUBLE,
    result_unit     VARCHAR(50),
    boolean_result  BOOLEAN DEFAULT FALSE,
    is_error        BOOLEAN DEFAULT FALSE,
    error_message   VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- History table for tracking measurement changes over time
CREATE TABLE IF NOT EXISTS measurement_history (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    measurement_id  BIGINT,
    action          VARCHAR(20) NOT NULL,
    details         VARCHAR(500),
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (measurement_id) REFERENCES quantity_measurement_entity(id) ON DELETE CASCADE
);

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_operation_type ON quantity_measurement_entity(operation_type);
CREATE INDEX IF NOT EXISTS idx_created_at ON quantity_measurement_entity(created_at);
CREATE INDEX IF NOT EXISTS idx_history_measurement_id ON measurement_history(measurement_id);
CREATE INDEX IF NOT EXISTS idx_history_action ON measurement_history(action);
