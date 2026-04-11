package com.app.quantitymeasurement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UC17 Spring Boot Application Entry Point.
 * Bootstraps the Spring Boot application with auto-configuration,
 * component scanning, and embedded Tomcat server.
 */
@SpringBootApplication
public class QuantityMeasurementAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuantityMeasurementAppApplication.class, args);
    }
}
