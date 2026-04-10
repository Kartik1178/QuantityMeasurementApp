package com.app.quantitymeasurement;

import com.app.quantitymeasurement.controller.QuantityMeasurementController;
import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import com.app.quantitymeasurement.repository.impl.QuantityMeasurementCacheRepository;
import com.app.quantitymeasurement.repository.impl.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.impl.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.util.ApplicationConfig;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UC16 Application Entry Point.
 *
 * Responsibilities:
 * - Load configuration via ApplicationConfig
 * - Bootstrap dependencies (Factory pattern)
 * - Wire repository -> service -> controller (DI)
 * - Delegate all work to controller
 * - Cleanup resources on exit
 */
public class QuantityMeasurementApp {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementApp.class);

    private static ConnectionPool connectionPool;

    // -- Factory: create repository -------------------------
    public static IQuantityMeasurementRepository createRepository(ApplicationConfig config) {
        if (config.isDatabaseRepository()) {
            logger.info("Using DATABASE repository");
            connectionPool = new ConnectionPool(config);
            return new QuantityMeasurementDatabaseRepository(connectionPool);
        }
        logger.info("Using CACHE repository");
        return QuantityMeasurementCacheRepository.getInstance();
    }

    // -- Factory: create service ----------------------------
    public static IQuantityMeasurementService createService(
            IQuantityMeasurementRepository repository) {
        return new QuantityMeasurementServiceImpl(repository);
    }

    // -- Factory: create controller -------------------------
    public static QuantityMeasurementController createController(
            IQuantityMeasurementService service) {
        return new QuantityMeasurementController(service);
    }

    // -- Delete all measurements ----------------------------
    public static void deleteAllMeasurements(IQuantityMeasurementRepository repository) {
        logger.info("Deleting all measurements...");
        repository.deleteAll();
        logger.info("All measurements deleted");
    }

    // -- Close resources ------------------------------------
    public static void closeResources() {
        if (connectionPool != null) {
            logger.info("Closing connection pool...");
            connectionPool.close();
            logger.info("Connection pool closed");
        }
    }

    // -- Entry Point ----------------------------------------
    public static void main(String[] args) {

        logger.info("============================================================");
        logger.info(" UC16 - Quantity Measurement Application with Database");
        logger.info("============================================================");

        // 1. Load configuration
        ApplicationConfig config = new ApplicationConfig();

        // 2. Initialize repository based on config
        IQuantityMeasurementRepository repository = createRepository(config);

        // 3. Wire service with DI
        IQuantityMeasurementService service = createService(repository);

        // 4. Wire controller with DI
        QuantityMeasurementController controller = createController(service);

        try {
            // -- Example 1: Length Equality -----------
            logger.info("--- Example 1: Length Equality ---");
            controller.performComparison(
                    new QuantityDTO(1.0, "FEET", "LENGTH"),
                    new QuantityDTO(12.0, "INCH", "LENGTH"));

            // -- Example 2: Length Conversion ----------
            logger.info("--- Example 2: Length Conversion ---");
            controller.performConversion(
                    new QuantityDTO(1.0, "FEET", "LENGTH"), "INCH");

            // -- Example 3: Length Addition ------------
            logger.info("--- Example 3: Length Addition ---");
            controller.performAddition(
                    new QuantityDTO(1.0, "FEET", "LENGTH"),
                    new QuantityDTO(12.0, "INCH", "LENGTH"));

            // -- Example 4: Weight Equality -----------
            logger.info("--- Example 4: Weight Equality ---");
            controller.performComparison(
                    new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"),
                    new QuantityDTO(1000.0, "GRAM", "WEIGHT"));

            // -- Example 5: Volume Addition -----------
            logger.info("--- Example 5: Volume Addition ---");
            controller.performAddition(
                    new QuantityDTO(1.0, "LITRE", "VOLUME"),
                    new QuantityDTO(1000.0, "MILLILITRE", "VOLUME"));

            // -- Example 6: Temperature Comparison ----
            logger.info("--- Example 6: Temperature Comparison ---");
            controller.performComparison(
                    new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE"),
                    new QuantityDTO(32.0, "FAHRENHEIT", "TEMPERATURE"));

            // -- Example 7: Temperature Addition (error expected) --
            logger.info("--- Example 7: Temperature Addition (expect error) ---");
            controller.performAddition(
                    new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE"),
                    new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE"));

            // -- Example 8: Cross-Category Prevention --
            logger.info("--- Example 8: Cross-Category Prevention (expect error) ---");
            controller.performAddition(
                    new QuantityDTO(1.0, "FEET", "LENGTH"),
                    new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"));

            // -- Example 9: Division ------------------
            logger.info("--- Example 9: Division ---");
            controller.performDivision(
                    new QuantityDTO(10.0, "FEET", "LENGTH"),
                    new QuantityDTO(2.0, "FEET", "LENGTH"));

            // -- Example 10: Subtraction --------------
            logger.info("--- Example 10: Subtraction ---");
            controller.performSubtraction(
                    new QuantityDTO(10.0, "FEET", "LENGTH"),
                    new QuantityDTO(6.0, "INCH", "LENGTH"));

            logger.info("============================================================");
            logger.info(" Total records in repository: {}", repository.getTotalCount());
            logger.info("============================================================");

        } finally {
            closeResources();
        }
    }
}
