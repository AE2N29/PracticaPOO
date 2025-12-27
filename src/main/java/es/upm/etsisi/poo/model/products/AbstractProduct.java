package es.upm.etsisi.poo.model.products;

import es.upm.etsisi.poo.persistence.ProductCatalog;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.util.Map;

public abstract class AbstractProduct {
    private String name;
    private final String id;

    public AbstractProduct(String id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        if (name.length() >= 100) {
            throw new IllegalArgumentException("Product name must be less than 100 characters.");
        }
        this.name = name;
        this.id = id;
    }
    public AbstractProduct(String name) {
        this.name = name;
        this.id = generateID();
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) {
        if (name == null || name.isBlank() || name.length() >= 100) {
            System.out.println(StaticMessages.INVALID_NAME);
            return;
        }
        this.name = name;
    }

    public boolean hasCategory() { //devuelve false por defecto, solo StockProducts devuelve true
        return false;
    }

    public boolean setCategory(Category category) { return false; } //igual

    public static String generateID() {
        Map<String, AbstractProduct> map = ProductCatalog.getList();
        String newID;
        do {
            int randomNumber = (int) (Math.random() * 10000000);
            newID = String.format("PR%07d", randomNumber);
        } while (map.containsKey(newID));

        return newID;
    }

    public abstract boolean isAvailable();

    public abstract double getPrice();

    public abstract boolean setPrice(double price);

    @Override
    public abstract String toString();
}