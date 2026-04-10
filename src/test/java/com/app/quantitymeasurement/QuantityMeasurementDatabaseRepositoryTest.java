package com.app.quantitymeasurement;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.exception.DatabaseException;
import com.app.quantitymeasurement.repository.impl.QuantityMeasurementDatabaseRepository;
import com.app.quantitymeasurement.util.ConnectionPool;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC16 Unit Tests for QuantityMeasurementDatabaseRepository.
 * Uses H2 in-memory database.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class QuantityMeasurementDatabaseRepositoryTest {

    private static ConnectionPool connectionPool;
    private QuantityMeasurementDatabaseRepository repository;

    @BeforeAll
    static void initPool() {
        connectionPool = new ConnectionPool(
                "jdbc:h2:mem:testdb_repo;DB_CLOSE_DELAY=-1",
                "sa", "", 3);
    }

    @AfterAll
    static void closePool() {
        if (connectionPool != null)
            connectionPool.close();
    }

    @BeforeEach
    void setUp() {
        repository = new QuantityMeasurementDatabaseRepository(connectionPool);
        repository.deleteAll();
    }

    // -- SAVE & FIND ALL --

    @Test
    @Order(1)
    void testSave_AndFindAll() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET");
        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("ADD", all.get(0).getOperationType());
        assertEquals(2.0, all.get(0).getResultValue(), 1e-4);
    }

    @Test
    @Order(2)
    void testSave_GeneratesId() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                10.0, "FEET", 2.0, "FEET", "DIVIDE", 5.0, "SCALAR");
        repository.save(entity);
        assertTrue(entity.getId() > 0, "ID should be generated after save");
    }

    @Test
    @Order(3)
    void testSave_CompareEntity() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity(
                1.0, "KILOGRAM", 1000.0, "GRAM", true);
        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertEquals("COMPARE", all.get(0).getOperationType());
        assertTrue(all.get(0).getBooleanResult());
    }

    @Test
    @Order(4)
    void testSave_ErrorEntity() {
        QuantityMeasurementEntity entity = new QuantityMeasurementEntity("test error message");
        repository.save(entity);

        List<QuantityMeasurementEntity> all = repository.findAll();
        assertEquals(1, all.size());
        assertTrue(all.get(0).isError());
        assertEquals("test error message", all.get(0).getErrorMessage());
    }

    // -- GET ALL MEASUREMENTS --

    @Test
    @Order(5)
    void testGetAllMeasurements() {
        repository.save(new QuantityMeasurementEntity(1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET"));
        repository.save(new QuantityMeasurementEntity(10.0, "FEET", 2.0, "FEET", "DIVIDE", 5.0, "SCALAR"));

        List<QuantityMeasurementEntity> all = repository.getAllMeasurements();
        assertEquals(2, all.size());
    }

    // -- GET BY OPERATION --

    @Test
    @Order(6)
    void testGetMeasurementsByOperation() {
        repository.save(new QuantityMeasurementEntity(1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET"));
        repository.save(new QuantityMeasurementEntity(1.0, "FEET", "CONVERT", 12.0, "INCH"));
        repository.save(new QuantityMeasurementEntity(10.0, "FEET", 2.0, "FEET", "DIVIDE", 5.0, "SCALAR"));

        List<QuantityMeasurementEntity> adds = repository.getMeasurementsByOperation("ADD");
        assertEquals(1, adds.size());

        List<QuantityMeasurementEntity> divides = repository.getMeasurementsByOperation("DIVIDE");
        assertEquals(1, divides.size());
    }

    // -- GET BY TYPE --

    @Test
    @Order(7)
    void testGetMeasurementsByType() {
        repository.save(new QuantityMeasurementEntity(1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET"));
        repository.save(new QuantityMeasurementEntity(1.0, "KILOGRAM", 1000.0, "GRAM", true));

        List<QuantityMeasurementEntity> feetResults = repository.getMeasurementsByType("FEET");
        assertEquals(1, feetResults.size());

        List<QuantityMeasurementEntity> kgResults = repository.getMeasurementsByType("KILOGRAM");
        assertEquals(1, kgResults.size());
    }

    // -- DELETE ALL --

    @Test
    @Order(8)
    void testDeleteAll() {
        repository.save(new QuantityMeasurementEntity(1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET"));
        repository.save(new QuantityMeasurementEntity(10.0, "FEET", 2.0, "FEET", "DIVIDE", 5.0, "SCALAR"));

        assertEquals(2, repository.getTotalCount());
        repository.deleteAll();
        assertEquals(0, repository.getTotalCount());
    }

    // -- GET TOTAL COUNT --

    @Test
    @Order(9)
    void testGetTotalCount() {
        assertEquals(0, repository.getTotalCount());

        repository.save(new QuantityMeasurementEntity(1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET"));
        assertEquals(1, repository.getTotalCount());

        repository.save(new QuantityMeasurementEntity(1.0, "FEET", "CONVERT", 12.0, "INCH"));
        assertEquals(2, repository.getTotalCount());
    }

    // -- CLEAR --

    @Test
    @Order(10)
    void testClear() {
        repository.save(new QuantityMeasurementEntity(1.0, "FEET", 12.0, "INCH", "ADD", 2.0, "FEET"));
        repository.clear();
        assertEquals(0, repository.findAll().size());
    }
}
