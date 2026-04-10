package com.app.quantitymeasurement.repository.impl;

import com.app.quantitymeasurement.entity.QuantityMeasurementEntity;
import com.app.quantitymeasurement.repository.IQuantityMeasurementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UC16 Singleton in-memory cache repository with optional disk persistence.
 * Implements all UC16 query methods with in-memory filtering.
 */
public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    private static final Logger logger = LoggerFactory.getLogger(QuantityMeasurementCacheRepository.class);

    // -- Singleton ----------------------------------------
    private static volatile QuantityMeasurementCacheRepository instance;

    private QuantityMeasurementCacheRepository() {
        loadFromDisk();
    }

    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            synchronized (QuantityMeasurementCacheRepository.class) {
                if (instance == null)
                    instance = new QuantityMeasurementCacheRepository();
            }
        }
        return instance;
    }

    // -- Storage ------------------------------------------
    private final List<QuantityMeasurementEntity> cache = new ArrayList<>();
    private static final String FILE_PATH = "quantity_measurements.dat";

    // -- Interface Methods --------------------------------

    @Override
    public void save(QuantityMeasurementEntity entity) {
        cache.add(entity);
        saveToDisk(entity);
        logger.info("Entity saved to cache repository: {}", entity);
    }

    @Override
    public List<QuantityMeasurementEntity> findAll() {
        return Collections.unmodifiableList(cache);
    }

    @Override
    public void clear() {
        cache.clear();
        new File(FILE_PATH).delete();
        logger.info("Cache repository cleared");
    }

    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return findAll();
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operationType) {
        return cache.stream()
                .filter(e -> operationType != null && operationType.equalsIgnoreCase(e.getOperationType()))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<QuantityMeasurementEntity> getMeasurementsByType(String unitType) {
        return cache.stream()
                .filter(e -> unitType != null &&
                        (unitType.equalsIgnoreCase(e.getOperand1Unit()) ||
                                unitType.equalsIgnoreCase(e.getOperand2Unit()) ||
                                unitType.equalsIgnoreCase(e.getResultUnit())))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void deleteAll() {
        clear();
    }

    @Override
    public int getTotalCount() {
        return cache.size();
    }

    // -- Persistence --------------------------------------

    private void saveToDisk(QuantityMeasurementEntity entity) {
        File file = new File(FILE_PATH);
        try {
            ObjectOutputStream oos = file.exists()
                    ? new AppendableObjectOutputStream(new FileOutputStream(file, true))
                    : new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(entity);
            oos.close();
        } catch (IOException e) {
            logger.warn("Could not persist entity: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromDisk() {
        File file = new File(FILE_PATH);
        if (!file.exists())
            return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Object obj = ois.readObject();
                    if (obj instanceof QuantityMeasurementEntity)
                        cache.add((QuantityMeasurementEntity) obj);
                } catch (EOFException eof) {
                    break;
                }
            }
            logger.info("Loaded {} entities from disk", cache.size());
        } catch (Exception e) {
            logger.warn("Could not load persisted data: {}", e.getMessage());
        }
    }

    // -- Inner class to support appending to existing file --
    private static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            reset(); // suppress duplicate stream header
        }
    }
}
