package com.app.quantitymeasurement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * UC16 Application Configuration.
 * Loads properties from application.properties on the classpath.
 * Determines repository type (cache vs database).
 */
public class ApplicationConfig {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private static final String CONFIG_FILE = "application.properties";

    private final Properties properties;

    public ApplicationConfig() {
        this.properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.warn("Configuration file '{}' not found on classpath. Using defaults.", CONFIG_FILE);
                setDefaults();
                return;
            }
            properties.load(input);
            logger.info("Application configuration loaded successfully from '{}'", CONFIG_FILE);
        } catch (IOException e) {
            logger.error("Failed to load configuration: {}", e.getMessage());
            setDefaults();
        }
    }

    private void setDefaults() {
        properties.setProperty("repository.type", "cache");
        properties.setProperty("db.url", "jdbc:h2:mem:quantitymeasurement;DB_CLOSE_DELAY=-1");
        properties.setProperty("db.driver", "org.h2.Driver");
        properties.setProperty("db.username", "sa");
        properties.setProperty("db.password", "");
        properties.setProperty("db.pool.size", "5");
    }

    // --- Accessors ---

    public String getRepositoryType() {
        return properties.getProperty("repository.type", "cache");
    }

    public boolean isDatabaseRepository() {
        return "database".equalsIgnoreCase(getRepositoryType());
    }

    public String getDbUrl() {
        return properties.getProperty("db.url", "jdbc:h2:mem:quantitymeasurement;DB_CLOSE_DELAY=-1");
    }

    public String getDbDriver() {
        return properties.getProperty("db.driver", "org.h2.Driver");
    }

    public String getDbUsername() {
        return properties.getProperty("db.username", "sa");
    }

    public String getDbPassword() {
        return properties.getProperty("db.password", "");
    }

    public int getPoolSize() {
        try {
            return Integer.parseInt(properties.getProperty("db.pool.size", "5"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid pool size in config. Defaulting to 5.");
            return 5;
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}
