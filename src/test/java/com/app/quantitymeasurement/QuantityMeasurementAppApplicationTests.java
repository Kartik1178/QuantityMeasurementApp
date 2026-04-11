package com.app.quantitymeasurement;

import com.app.quantitymeasurement.dto.*;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.QuantityMeasurementRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC17 Integration Tests using Spring Boot Test with TestRestTemplate.
 * Tests the full application stack including REST controllers, service layer,
 * JPA repository, and H2 database.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementAppApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private QuantityMeasurementRepository repository;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/v1/quantities";
    }

    // ============ Application Context Tests ============

    @Test
    @Order(1)
    void testSpringBootApplicationStarts() {
        assertNotNull(restTemplate);
        assertNotNull(repository);
    }

    // ============ Compare Tests ============

    @Test
    @Order(2)
    void testCompareQuantities_Equal() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(12.0, "INCH", "LengthUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/compare", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("compare", response.getBody().getOperation());
        assertEquals("true", response.getBody().getResultString());
        assertFalse(response.getBody().isError());
    }

    @Test
    @Order(3)
    void testCompareQuantities_NotEqual() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(10.0, "INCH", "LengthUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/compare", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("false", response.getBody().getResultString());
    }

    // ============ Convert Tests ============

    @Test
    @Order(4)
    void testConvertQuantity_FeetToInches() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(0.0, "INCH", "LengthUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/convert", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("convert", response.getBody().getOperation());
        assertEquals(12.0, response.getBody().getResultValue(), 0.01);
    }

    // ============ Add Tests ============

    @Test
    @Order(5)
    void testAddQuantities_FeetPlusInches() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(12.0, "INCH", "LengthUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/add", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("add", response.getBody().getOperation());
        assertEquals(2.0, response.getBody().getResultValue(), 0.01);
        assertEquals("FEET", response.getBody().getResultUnit());
        assertEquals("LengthUnit", response.getBody().getResultMeasurementType());
    }

    @Test
    @Order(6)
    void testAddQuantities_Weight() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "KILOGRAM", "WeightUnit"),
                new QuantityDTO(1000.0, "GRAM", "WeightUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/add", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 0.01);
    }

    // ============ Subtract Tests ============

    @Test
    @Order(7)
    void testSubtractQuantities_FeetMinusInches() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(10.0, "FEET", "LengthUnit"),
                new QuantityDTO(6.0, "INCH", "LengthUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/subtract", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("subtract", response.getBody().getOperation());
        assertEquals(9.5, response.getBody().getResultValue(), 0.01);
    }

    // ============ Divide Tests ============

    @Test
    @Order(8)
    void testDivideQuantities_Success() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(10.0, "FEET", "LengthUnit"),
                new QuantityDTO(2.0, "FEET", "LengthUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/divide", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5.0, response.getBody().getResultValue(), 0.01);
    }

    // ============ Error Handling Tests ============

    @Test
    @Order(9)
    void testCompareQuantities_CrossCategory_Returns400() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(1.0, "KILOGRAM", "WeightUnit")
        );

        ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(
                baseUrl + "/compare", input, ErrorResponseDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Quantity Measurement Error", response.getBody().getError());
    }

    @Test
    @Order(10)
    void testAddQuantities_Temperature_Returns400() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(100.0, "CELSIUS", "TemperatureUnit"),
                new QuantityDTO(50.0, "CELSIUS", "TemperatureUnit")
        );

        ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(
                baseUrl + "/add", input, ErrorResponseDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(11)
    void testAddQuantities_InvalidUnit_Returns400() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FOOT", "LengthUnit"),
                new QuantityDTO(12.0, "INCHE", "LengthUnit")
        );

        ResponseEntity<ErrorResponseDTO> response = restTemplate.postForEntity(
                baseUrl + "/add", input, ErrorResponseDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    // ============ History Tests ============

    @Test
    @Order(12)
    void testGetHistoryByOperation() {
        ResponseEntity<QuantityMeasurementDTO[]> response = restTemplate.getForEntity(
                baseUrl + "/history/operation/COMPARE", QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(13)
    void testGetHistoryByMeasurementType() {
        ResponseEntity<QuantityMeasurementDTO[]> response = restTemplate.getForEntity(
                baseUrl + "/history/type/LengthUnit", QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @Order(14)
    void testGetErrorHistory() {
        ResponseEntity<QuantityMeasurementDTO[]> response = restTemplate.getForEntity(
                baseUrl + "/history/errored", QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(15)
    void testGetCountByOperation() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + "/count/COMPARE", Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("COMPARE", response.getBody().get("operationType"));
    }

    // ============ JPA Persistence Tests ============

    @Test
    @Order(16)
    void testDatabasePersistence() {
        List<QuantityMeasurementEntity> entities = repository.findAll();
        assertFalse(entities.isEmpty(), "Database should have persisted records from previous tests");
    }

    @Test
    @Order(17)
    void testFindByOperationType() {
        List<QuantityMeasurementEntity> compareResults = repository.findByOperationType("compare");
        assertFalse(compareResults.isEmpty());
    }

    @Test
    @Order(18)
    void testFindByMeasurementType() {
        List<QuantityMeasurementEntity> lengthResults = repository.findByThisMeasurementType("LengthUnit");
        assertFalse(lengthResults.isEmpty());
    }

    @Test
    @Order(19)
    void testFindErrorRecords() {
        List<QuantityMeasurementEntity> errors = repository.findByIsErrorTrue();
        assertNotNull(errors);
    }

    // ============ Temperature Comparison Tests ============

    @Test
    @Order(20)
    void testCompareTemperature_CelsiusToFahrenheit() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(0.0, "CELSIUS", "TemperatureUnit"),
                new QuantityDTO(32.0, "FAHRENHEIT", "TemperatureUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/compare", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("true", response.getBody().getResultString());
    }

    // ============ Volume Tests ============

    @Test
    @Order(21)
    void testAddVolume_LitrePlusMillilitre() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "LITRE", "VolumeUnit"),
                new QuantityDTO(1000.0, "MILLILITRE", "VolumeUnit")
        );

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/add", input, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2.0, response.getBody().getResultValue(), 0.01);
    }

    // ============ Actuator Tests ============

    @Test
    @Order(22)
    void testActuatorHealthEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/actuator/health", Map.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
    }

    // ============ Content Negotiation Tests ============

    @Test
    @Order(23)
    void testContentNegotiation_JSON() {
        QuantityInputDTO input = new QuantityInputDTO(
                new QuantityDTO(1.0, "FEET", "LengthUnit"),
                new QuantityDTO(12.0, "INCH", "LengthUnit")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<QuantityInputDTO> request = new HttpEntity<>(input, headers);

        ResponseEntity<QuantityMeasurementDTO> response = restTemplate.postForEntity(
                baseUrl + "/compare", request, QuantityMeasurementDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().toString()
                .contains("application/json"));
    }

    // ============ Clear History Test ============

    @Test
    @Order(99)
    void testClearHistory() {
        restTemplate.delete(baseUrl + "/history");

        ResponseEntity<QuantityMeasurementDTO[]> response = restTemplate.getForEntity(
                baseUrl + "/history", QuantityMeasurementDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().length);
    }
}
