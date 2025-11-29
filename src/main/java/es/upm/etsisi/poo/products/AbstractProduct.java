package es.upm.etsisi.poo.products;
import es.upm.etsisi.poo.ProductHM;

import java.util.Map;

public abstract class AbstractProduct {
    protected String name;
    protected String id;

    public AbstractProduct(String id, String name) {
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

    protected abstract double calculatePrice();
    protected abstract boolean availability();

    protected static String generateID() {
        Map<String, AbstractProduct> map = ProductHM.getList();
        String newID;
        do {
            int randomNumber = (int) (Math.random() * 10000000);
            newID = String.format("PR%07d", randomNumber);
        } while (map.containsKey(newID));

        return newID;
    }
}
