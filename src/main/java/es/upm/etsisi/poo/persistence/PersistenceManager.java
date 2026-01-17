package es.upm.etsisi.poo.persistence;

import java.io.*;

public class PersistenceManager {
    private static final String FILE_PATH = "store_data.dat"; // El archivo local

    public static void save(StoreData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public static StoreData load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return null; // Primera ejecución, no hay datos
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (StoreData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data (starting fresh): " + e.getMessage());
            return null;
        }
    }
}
