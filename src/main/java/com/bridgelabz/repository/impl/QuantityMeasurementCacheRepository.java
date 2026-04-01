package com.bridgelabz.repository.impl;

import com.bridgelabz.entity.QuantityMeasurementEntity;
import com.bridgelabz.repository.IQuantityMeasurementRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UC15 Singleton in-memory cache repository with optional disk persistence.
 */
public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    // â”€â”€ Singleton â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private static volatile QuantityMeasurementCacheRepository instance;

    private QuantityMeasurementCacheRepository() {
        loadFromDisk();
    }

    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            synchronized (QuantityMeasurementCacheRepository.class) {
                if (instance == null) instance = new QuantityMeasurementCacheRepository();
            }
        }
        return instance;
    }

    // â”€â”€ Storage â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private final List<QuantityMeasurementEntity> cache = new ArrayList<>();
    private static final String FILE_PATH = "quantity_measurements.dat";

    // â”€â”€ Interface Methods â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    @Override
    public void save(QuantityMeasurementEntity entity) {
        cache.add(entity);
        saveToDisk(entity);
    }

    @Override
    public List<QuantityMeasurementEntity> findAll() {
        return Collections.unmodifiableList(cache);
    }

    @Override
    public void clear() {
        cache.clear();
        new File(FILE_PATH).delete();
    }

    // â”€â”€ Persistence â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void saveToDisk(QuantityMeasurementEntity entity) {
        File file = new File(FILE_PATH);
        try {
            ObjectOutputStream oos = file.exists()
                    ? new AppendableObjectOutputStream(new FileOutputStream(file, true))
                    : new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(entity);
            oos.close();
        } catch (IOException e) {
            System.err.println("[WARN] Could not persist entity: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromDisk() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return;
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
        } catch (Exception e) {
            System.err.println("[WARN] Could not load persisted data: " + e.getMessage());
        }
    }

    // â”€â”€ Inner class to support appending to existing file â”€â”€â”€â”€
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
