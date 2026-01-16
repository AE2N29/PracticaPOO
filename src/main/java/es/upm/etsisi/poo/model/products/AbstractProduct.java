package es.upm.etsisi.poo.model.products;

import es.upm.etsisi.poo.Command.AppConfigurations;
import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.persistence.ProductCatalog;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.io.Serializable;
import java.util.Map;

public abstract class AbstractProduct implements Serializable {
    private String name;
    private final String id;

    public AbstractProduct(String id, String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be empty.");
        }
        //  Aquí usa AppConfig en lugar de número mágico
        if (name.length() >= AppConfigurations.MAX_PRODUCT_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Product name must be less than " +
                            AppConfigurations.MAX_PRODUCT_NAME_LENGTH + " characters."
            );
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
        if (name == null || name.isBlank() ||
                name.length() >= AppConfigurations.MAX_PRODUCT_NAME_LENGTH) {
            System.out.println(StaticMessages.INVALID_NAME);
            return;
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