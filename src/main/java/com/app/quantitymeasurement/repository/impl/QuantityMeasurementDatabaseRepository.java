package com.app.quantitymeasurement.repository.impl;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.DatabaseException;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UC16 JDBC-based database repository implementation.
 * Uses PreparedStatement for all queries (parameterized - no raw SQL).
 * Supports transactions and proper resource cleanup.
 */
public class QuantityMeasurementDatabaseRepository implements IQuantityMeasurementRepository {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementDatabaseRepository.class);

    private final ConnectionPool connectionPool;

    public QuantityMeasurementDatabaseRepository(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
        initializeSchema();
    }

    /**
     * Initialize database schema by running schema.sql from classpath.
     */
    private void initializeSchema() {
        logger.info("Initializing database schema...");
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            try (var is = getClass().getClassLoader().getResourceAsStream("db/schema.sql")) {
                if (is == null) {
                    logger.error("Schema file 'db/schema.sql' not found on classpath");
                    throw new DatabaseException("Schema file not found");
                }
                String sql = new String(is.readAllBytes());
                String[] statements = sql.split(";");
                try (Statement stmt = conn.createStatement()) {
                    for (String s : statements) {
                        String trimmed = s.trim();
                        if (!trimmed.isEmpty()) {
                            stmt.execute(trimmed);
                        }
                    }
                }
            }
            conn.commit();
            logger.info("Database schema initialized successfully");
        } catch (Exception e) {
            rollback(conn);
            logger.error("Failed to initialize schema: {}", e.getMessage());
            throw new DatabaseException("Failed to initialize database schema", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    // -- SAVE -----------------------------------------------

    @Override
    public void save(QuantityMeasurementEntity entity) {
        String sql = "INSERT INTO quantity_measurement_entity " +
                "(operand1_value, operand1_unit, operand2_value, operand2_unit, " +
                "operation_type, result_value, result_unit, boolean_result, " +
                "is_error, error_message) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, entity.getOperand1Value());
                ps.setString(2, entity.getOperand1Unit());
                ps.setDouble(3, entity.getOperand2Value());
                ps.setString(4, entity.getOperand2Unit());
                ps.setString(5, entity.getOperationType());
                ps.setDouble(6, entity.getResultValue());
                ps.setString(7, entity.getResultUnit());
                ps.setBoolean(8, entity.getBooleanResult());
                ps.setBoolean(9, entity.isError());
                ps.setString(10, entity.getErrorMessage());

                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        entity.setId(keys.getLong(1));
                    }
                }

                // Also insert into history table
                saveHistory(conn, entity.getId(), "INSERT", entity.toString());

                conn.commit();
                logger.info("Entity saved to database with id={}: {}", entity.getId(), entity);
            }
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Failed to save entity: {}", e.getMessage());
            throw new DatabaseException("Failed to save measurement", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    private void saveHistory(Connection conn, long measurementId, String action, String details) throws SQLException {
        String sql = "INSERT INTO measurement_history (measurement_id, action, details) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, measurementId);
            ps.setString(2, action);
            ps.setString(3, details);
            ps.executeUpdate();
        }
    }

    // -- FIND ALL -------------------------------------------

    @Override
    public List<QuantityMeasurementEntity> findAll() {
        return getAllMeasurements();
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        String sql = "SELECT * FROM quantity_measurement_entity ORDER BY created_at DESC";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                List<QuantityMeasurementEntity> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(mapResultSet(rs));
                }
                logger.debug("Retrieved {} measurements from database", results.size());
                return results;
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve measurements: {}", e.getMessage());
            throw new DatabaseException("Failed to retrieve measurements", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    // -- GET BY OPERATION -----------------------------------

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType) {
        String sql = "SELECT * FROM quantity_measurement_entity WHERE operation_type = ? ORDER BY created_at DESC";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, operationType);
                try (ResultSet rs = ps.executeQuery()) {
                    List<QuantityMeasurementEntity> results = new ArrayList<>();
                    while (rs.next()) {
                        results.add(mapResultSet(rs));
                    }
                    logger.debug("Retrieved {} measurements for operation={}", results.size(), operationType);
                    return results;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve measurements by operation: {}", e.getMessage());
            throw new DatabaseException("Failed to retrieve measurements by operation", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    // -- GET BY TYPE ----------------------------------------

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String unitType) {
        String sql = "SELECT * FROM quantity_measurement_entity WHERE " +
                "operand1_unit = ? OR operand2_unit = ? OR result_unit = ? ORDER BY created_at DESC";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, unitType);
                ps.setString(2, unitType);
                ps.setString(3, unitType);
                try (ResultSet rs = ps.executeQuery()) {
                    List<QuantityMeasurementEntity> results = new ArrayList<>();
                    while (rs.next()) {
                        results.add(mapResultSet(rs));
                    }
                    logger.debug("Retrieved {} measurements for type={}", results.size(), unitType);
                    return results;
                }
            }
        } catch (SQLException e) {
            logger.error("Failed to retrieve measurements by type: {}", e.getMessage());
            throw new DatabaseException("Failed to retrieve measurements by type", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    // -- CLEAR / DELETE ALL --------------------------------

    @Override
    public void clear() {
        deleteAll();
    }

    @Override
    public void deleteAll() {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement("DELETE FROM measurement_history");
                    PreparedStatement ps2 = conn.prepareStatement("DELETE FROM quantity_measurement_entity")) {
                ps1.executeUpdate();
                ps2.executeUpdate();
                conn.commit();
                logger.info("All measurements deleted from database");
            }
        } catch (SQLException e) {
            rollback(conn);
            logger.error("Failed to delete measurements: {}", e.getMessage());
            throw new DatabaseException("Failed to delete all measurements", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    // -- GET TOTAL COUNT -----------------------------------

    @Override
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM quantity_measurement_entity";
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            logger.error("Failed to get count: {}", e.getMessage());
            throw new DatabaseException("Failed to get measurement count", e);
        } finally {
            connectionPool.releaseConnection(conn);
        }
    }

    // -- HELPERS -------------------------------------------

    private QuantityMeasurementEntity mapResultSet(ResultSet rs) throws SQLException {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity();
        entity.setId(rs.getLong("id"));
        entity.setOperand1Value(rs.getDouble("operand1_value"));
        entity.setOperand1Unit(rs.getString("operand1_unit"));
        entity.setOperand2Value(rs.getDouble("operand2_value"));
        entity.setOperand2Unit(rs.getString("operand2_unit"));
        entity.setOperationType(rs.getString("operation_type"));
        entity.setResultValue(rs.getDouble("result_value"));
        entity.setResultUnit(rs.getString("result_unit"));
        entity.setBooleanResult(rs.getBoolean("boolean_result"));
        entity.setError(rs.getBoolean("is_error"));
        entity.setErrorMessage(rs.getString("error_message"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            entity.setCreatedAt(ts.toLocalDateTime());
        }
        return entity;
    }

    private void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                logger.error("Failed to rollback transaction: {}", e.getMessage());
            }
        }
    }
}
