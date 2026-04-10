package com.app.quantitymeasurement.util;

import com.app.quantitymeasurement.exception.DatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UC16 Connection Pool.
 * Manages a fixed-size pool of JDBC connections with statistics tracking.
 */
public class ConnectionPool {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private final String url;
    private final String username;
    private final String password;
    private final int poolSize;

    private final List<Connection> availableConnections;
    private final List<Connection> activeConnections;
    private boolean closed = false;

    // Statistics
    private int totalConnectionsCreated = 0;
    private int totalConnectionsRequested = 0;

    public ConnectionPool(String url, String username, String password, int poolSize) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.poolSize = poolSize;
        this.availableConnections = new ArrayList<>(poolSize);
        this.activeConnections = new ArrayList<>(poolSize);

        logger.info("Initializing connection pool: url={}, poolSize={}", url, poolSize);
        initializePool();
    }

    public ConnectionPool(ApplicationConfig config) {
        this(config.getDbUrl(), config.getDbUsername(), config.getDbPassword(), config.getPoolSize());
    }

    private void initializePool() {
        try {
            for (int i = 0; i < poolSize; i++) {
                Connection conn = createConnection();
                availableConnections.add(conn);
            }
            logger.info("Connection pool initialized with {} connections", poolSize);
        } catch (SQLException e) {
            logger.error("Failed to initialize connection pool: {}", e.getMessage());
            throw new DatabaseException("Failed to initialize connection pool", e);
        }
    }

    private Connection createConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(url, username, password);
        totalConnectionsCreated++;
        logger.debug("Created new database connection (total: {})", totalConnectionsCreated);
        return conn;
    }

    /**
     * Get a connection from the pool.
     * If no connections are available, creates a new one (up to pool size).
     */
    public synchronized Connection getConnection() {
        if (closed) {
            throw new DatabaseException("Connection pool is closed");
        }

        totalConnectionsRequested++;

        if (!availableConnections.isEmpty()) {
            Connection conn = availableConnections.remove(availableConnections.size() - 1);
            try {
                if (conn.isClosed()) {
                    logger.debug("Replacing closed connection from pool");
                    conn = createConnection();
                }
            } catch (SQLException e) {
                try {
                    conn = createConnection();
                } catch (SQLException ex) {
                    throw new DatabaseException("Failed to get connection from pool", ex);
                }
            }
            activeConnections.add(conn);
            logger.debug("Connection acquired from pool (active: {}, available: {})",
                    activeConnections.size(), availableConnections.size());
            return conn;
        }

        // No available connections - create temporary one
        try {
            Connection conn = createConnection();
            activeConnections.add(conn);
            logger.warn("Pool exhausted, created overflow connection (active: {})", activeConnections.size());
            return conn;
        } catch (SQLException e) {
            throw new DatabaseException("Failed to create connection", e);
        }
    }

    /**
     * Return a connection to the pool.
     */
    public synchronized void releaseConnection(Connection conn) {
        if (conn == null)
            return;

        activeConnections.remove(conn);
        try {
            if (!conn.isClosed()) {
                if (availableConnections.size() < poolSize) {
                    availableConnections.add(conn);
                    logger.debug("Connection returned to pool (active: {}, available: {})",
                            activeConnections.size(), availableConnections.size());
                } else {
                    conn.close();
                    logger.debug("Overflow connection closed");
                }
            }
        } catch (SQLException e) {
            logger.warn("Error releasing connection: {}", e.getMessage());
        }
    }

    /**
     * Close all connections and shut down the pool.
     */
    public synchronized void close() {
        if (closed)
            return;
        closed = true;
        logger.info("Shutting down connection pool...");

        for (Connection conn : availableConnections) {
            try {
                conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
        availableConnections.clear();

        for (Connection conn : activeConnections) {
            try {
                conn.close();
            } catch (SQLException e) {
                /* ignore */ }
        }
        activeConnections.clear();

        logger.info("Connection pool shut down. Total connections created: {}, Total requests: {}",
                totalConnectionsCreated, totalConnectionsRequested);
    }

    // --- Statistics ---

    public synchronized int getActiveCount() {
        return activeConnections.size();
    }

    public synchronized int getAvailableCount() {
        return availableConnections.size();
    }

    public int getTotalConnectionsCreated() {
        return totalConnectionsCreated;
    }

    public int getTotalConnectionsRequested() {
        return totalConnectionsRequested;
    }

    public boolean isClosed() {
        return closed;
    }
}
