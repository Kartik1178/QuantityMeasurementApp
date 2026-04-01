package com.bridgelabz;

import com.bridgelabz.controller.QuantityMeasurementController;
import com.bridgelabz.dto.QuantityDTO;
import com.bridgelabz.repository.IQuantityMeasurementRepository;
import com.bridgelabz.repository.impl.QuantityMeasurementCacheRepository;
import com.bridgelabz.service.IQuantityMeasurementService;
import com.bridgelabz.service.impl.QuantityMeasurementServiceImpl;

/**
 * UC15 Application Entry Point.
 *
 * Responsibilities (only):
 *   - Bootstrap dependencies (Factory pattern)
 *   - Wire repository -> service -> controller (DI)
 *   - Delegate all work to controller
 *
 * Design patterns used:
 *   - Factory:    createService() / createController()
 *   - Facade:     controller hides service complexity
 *   - Singleton:  repository (QuantityMeasurementCacheRepository)
 *   - DI:         constructor injection throughout
 */
public class QuantityMeasurementApp {

    // â”€â”€ Factory: create service â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static IQuantityMeasurementService createService(
            IQuantityMeasurementRepository repository) {
        return new QuantityMeasurementServiceImpl(repository);
    }

    // â”€â”€ Factory: create controller â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static QuantityMeasurementController createController(
            IQuantityMeasurementService service) {
        return new QuantityMeasurementController(service);
    }

    // â”€â”€ Entry Point â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public static void main(String[] args) {

        // 1. Initialize repository (Singleton)
        IQuantityMeasurementRepository repository =
                QuantityMeasurementCacheRepository.getInstance();

        // 2. Wire service with DI
        IQuantityMeasurementService service = createService(repository);

        // 3. Wire controller with DI
        QuantityMeasurementController controller = createController(service);

        System.out.println("============================================================");
        System.out.println(" UC15 - N-Tier Quantity Measurement Application");
        System.out.println("============================================================");

        // â”€â”€ Example 1: Length Equality â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 1: Length Equality ---");
        controller.performComparison(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH"));

        // â”€â”€ Example 2: Length Conversion â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 2: Length Conversion ---");
        controller.performConversion(
                new QuantityDTO(1.0, "FEET", "LENGTH"), "INCH");

        // â”€â”€ Example 3: Length Addition â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 3: Length Addition ---");
        controller.performAddition(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH"));

        // â”€â”€ Example 4: Weight Equality â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 4: Weight Equality ---");
        controller.performComparison(
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"),
                new QuantityDTO(1000.0, "GRAM", "WEIGHT"));

        // â”€â”€ Example 5: Volume Addition â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 5: Volume Addition ---");
        controller.performAddition(
                new QuantityDTO(1.0, "LITRE", "VOLUME"),
                new QuantityDTO(1000.0, "MILLILITRE", "VOLUME"));

        // â”€â”€ Example 6: Temperature Comparison â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 6: Temperature Comparison ---");
        controller.performComparison(
                new QuantityDTO(0.0, "CELSIUS", "TEMPERATURE"),
                new QuantityDTO(32.0, "FAHRENHEIT", "TEMPERATURE"));

        // â”€â”€ Example 7: Temperature Addition (should error) â”€â”€â”€
        System.out.println("\n--- Example 7: Temperature Addition (expect error) ---");
        controller.performAddition(
                new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE"),
                new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE"));

        // â”€â”€ Example 8: Cross-Category Prevention â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 8: Cross-Category Prevention (expect error) ---");
        controller.performAddition(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"));

        // â”€â”€ Example 9: Division â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 9: Division ---");
        controller.performDivision(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(2.0, "FEET", "LENGTH"));

        // â”€â”€ Example 10: Subtraction â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        System.out.println("\n--- Example 10: Subtraction ---");
        controller.performSubtraction(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(6.0, "INCH", "LENGTH"));

        System.out.println("\n============================================================");
        System.out.println(" History stored in repository: " +
                repository.findAll().size() + " records");
        System.out.println("============================================================");
    }
}
