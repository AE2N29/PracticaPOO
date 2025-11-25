package es.upm.etsisi.poo.products;
import es.upm.etsisi.poo.Product;
import es.upm.etsisi.poo.ProductHM;

import java.util.Map;
import java.util.Random;

public abstract class AbstractProduct {
    protected String name;
    protected String id;

    public AbstractProduct(String name, String id) {
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

    private static String generateID() { // crea IDs con formato: PRnnnnnnn  (n = numeros)
        Map<Integer, Product> map = ProductHM.getList();
        Random random = new Random();
        String newID = "";
        boolean exist = false;
        while (!exist)
        {
            int randomNumber = random.nextInt(10000000);
            String filler = String.format("%07d", randomNumber);
            String newId = "PR"+ filler;
            if(map.containsKey(newId))
            {
                exist = true;
            }
        }
        return newID;
    }
}
