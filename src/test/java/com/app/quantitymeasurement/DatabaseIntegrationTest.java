package com.app.quantitymeasurement;

import com.app.quantitymeasurement.dto.QuantityDTO;
import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.QuantityMeasurementException;
import com.app.quantitymeasurement.repository.impl.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.service.IQuantityMeasurementService;
import com.app.quantitymeasurement.service.impl.QuantityMeasurementServiceImpl;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC16 Integration Test.
 * Tests end-to-end: Service -> Database Repository -> H2 -> Verify Persistence.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatabaseIntegrationTest {

    private static ConnectionPool connectionPool;
    private static QuantityMeasurementDatabaseRepository repository;
    private static IQuantityMeasurementService service;

    @BeforeAll
    static void setUp() {
        connectionPool = new ConnectionPool(
                "jdbc:h2:mem:testdb_integration;DB_CLOSE_DELAY=-1",
                "sa", "", 3);
        repository = new QuantityMeasurementDatabaseRepository(connectionPool);
        service = new QuantityMeasurementServiceImpl(repository);
    }

    @AfterAll
    static void tearDown() {
        if (connectionPool != null)
            connectionPool.close();
    }

    @BeforeEach
    void cleanDb() {
        repository.deleteAll();
    }

    // -- END-TO-END: ADD --

    @Test
    @Order(1)
    void testIntegration_Add_PersistsToDatabase() {
        QuantityDTO result = service.add(
                new QuantityDTO(1.0, "FEET", "LENGTH"),
                new QuantityDTO(12.0, "INCH", "LENGTH"));

        assertEquals(2.0, result.getValue(), 1e-4);
        assertEquals("FEET", result.getUnit());

        // Verify persistence
        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("ADD", all.get(0).getOperationType());
        assertEquals(2.0, all.get(0).getResultValue(), 1e-4);
    }

    // -- END-TO-END: COMPARE --

    @Test
    @Order(2)
    void testIntegration_Compare_PersistsToDatabase() {
        boolean result = service.compare(
                new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"),
                new QuantityDTO(1000.0, "GRAM", "WEIGHT"));

        assertTrue(result);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("COMPARE", all.get(0).getOperationType());
        assertTrue(all.get(0).getBooleanResult());
    }

    // -- END-TO-END: CONVERT --

    @Test
    @Order(3)
    void testIntegration_Convert_PersistsToDatabase() {
        QuantityDTO result = service.convert(
                new QuantityDTO(1.0, "LITRE", "VOLUME"), "MILLILITRE");

        assertEquals(1000.0, result.getValue(), 1e-4);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("CONVERT", all.get(0).getOperationType());
    }

    // -- END-TO-END: SUBTRACT --

    @Test
    @Order(4)
    void testIntegration_Subtract_PersistsToDatabase() {
        QuantityDTO result = service.subtract(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(6.0, "INCH", "LENGTH"));

        assertEquals(9.5, result.getValue(), 1e-4);

        assertEquals(1, repository.getTotalCount());
    }

    // -- END-TO-END: DIVIDE --

    @Test
    @Order(5)
    void testIntegration_Divide_PersistsToDatabase() {
        double result = service.divide(
                new QuantityDTO(10.0, "FEET", "LENGTH"),
                new QuantityDTO(2.0, "FEET", "LENGTH"));

        assertEquals(5.0, result, 1e-4);

        assertEquals(1, repository.getTotalCount());
        assertEquals("DIVIDE", repository.findAll().get(0).getOperationType());
    }

    // -- END-TO-END: ERROR PERSISTENCE --

    @Test
    @Order(6)
    void testIntegration_Error_PersistsToDatabase() {
        assertThrows(QuantityMeasurementException.class, () -> service.add(
                new QuantityDTO(100.0, "CELSIUS", "TEMPERATURE"),
                new QuantityDTO(50.0, "CELSIUS", "TEMPERATURE")));

        // Error should also be persisted
        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertTrue(all.get(0).isError());
    }

    // -- MULTIPLE OPERATIONS --

    @Test
    @Order(7)
    void testIntegration_MultipleOperations_AllPersisted() {
        service.add(new QuantityDTO(1.0, "FEET", "LENGTH"), new QuantityDTO(12.0, "INCH", "LENGTH"));
        service.compare(new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"), new QuantityDTO(1000.0, "GRAM", "WEIGHT"));
        service.convert(new QuantityDTO(1.0, "FEET", "LENGTH"), "INCH");
        service.subtract(new QuantityDTO(10.0, "FEET", "LENGTH"), new QuantityDTO(6.0, "INCH", "LENGTH"));
        service.divide(new QuantityDTO(10.0, "FEET", "LENGTH"), new QuantityDTO(2.0, "FEET", "LENGTH"));

        assertEquals(5, repository.getTotalCount());
    }

    // -- QUERY BY OPERATION --

    @Test
    @Order(8)
    void testIntegration_QueryByOperation() {
        service.add(new QuantityDTO(1.0, "FEET", "LENGTH"), new QuantityDTO(12.0, "INCH", "LENGTH"));
        service.add(new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"), new QuantityDTO(1000.0, "GRAM", "WEIGHT"));
        service.convert(new QuantityDTO(1.0, "FEET", "LENGTH"), "INCH");

        assertEquals(2, repository.getMeasurementsByOperation("ADD").size());
        assertEquals(1, repository.getMeasurementsByOperation("CONVERT").size());
    }

    // -- DELETE ALL --

    @Test
    @Order(9)
    void testIntegration_DeleteAll() {
        service.add(new QuantityDTO(1.0, "FEET", "LENGTH"), new QuantityDTO(12.0, "INCH", "LENGTH"));
        service.compare(new QuantityDTO(1.0, "KILOGRAM", "WEIGHT"), new QuantityDTO(1000.0, "GRAM", "WEIGHT"));

        assertEquals(2, repository.getTotalCount());
        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
    }
}
