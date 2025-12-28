package es.upm.etsisi.poo.model.products;

import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.persistence.ProductCatalog;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.util.Map;

public abstract class AbstractProduct {
    private String name;
    private final String id;

    public AbstractProduct(String id, String name) throws StoreException {
        if (name == null || name.isBlank() || name.length() >= 100) {
            throw new StoreException(StaticMessages.INVALID_NAME);
        }
        this.name = name;
        this.id = id;
    }
    public AbstractProduct(String name) throws StoreException {
        this(generateID(), name);
    }

    public String getName() {
        return name;
    }
    public String getId() {
        return id;
    }

    public void setName(String name) throws StoreException {
        if (name == null || name.isBlank() || name.length() >= 100) {
            throw new StoreException(StaticMessages.INVALID_NAME);
        }
        this.name = name;
    }

    public boolean hasCategory() { //devuelve false por defecto, solo StockProducts devuelve true
        return false;
    }

    public boolean setCategory(Category category) { return false; }

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