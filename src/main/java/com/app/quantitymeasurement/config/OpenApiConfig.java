package com.app.quantitymeasurement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * UC17 OpenAPI / Swagger configuration for the Quantity Measurement REST API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI quantityMeasurementOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Quantity Measurement REST API")
                        .description("UC17 - Spring Boot REST API for quantity comparison, conversion, " +
                                "and arithmetic operations with persistent measurement history. " +
                                "Supports Length, Weight, Volume, and Temperature units.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Kartikeya")
                                .email("kartikeya@example.com"))
                );
    }
}