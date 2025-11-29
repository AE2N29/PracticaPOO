package es.upm.etsisi.poo.persistance;

import es.upm.etsisi.poo.model.products.AbstractProduct;

import java.util.HashMap;
import java.util.Map;

public class ProductCatalog {
    private static final int MAX_PRODUCTS = 200;
    private static final HashMap<String, AbstractProduct> products = new HashMap<>();

    private ProductCatalog() {}

    public static AbstractProduct getProduct(String id) {
        return products.get(id);
    }

    public static Map<String, AbstractProduct> getList() {
        return products;
    }

    public static void add(String id, AbstractProduct product) throws IllegalArgumentException {
        //Si el prod no existe y se respeta la max cantidad con HashMap.put lo agrega

        if (products.containsKey(id)) {
            throw new IllegalArgumentException("ERROR: Product couldn't be added!");
        }
        if (products.size() >= MAX_PRODUCTS) {
            throw new IllegalArgumentException("ERROR: The list is full");
        }
        products.put(id, product);
        System.out.println(product);
        System.out.println("prod add: ok");
    }

    public static void remove(String id) {  // Si existe el prod en la lista , con HashMao.remove lo quita
        if (!products.containsKey(id)) {
            System.out.println("ERROR: Product with id " + id + " does not exist!");
        } else {
            AbstractProduct product = products.get(id);
            System.out.println(product);
            products.remove(id);
            System.out.println("prod remove: ok");
        }
    }

    public static void update(String id, AbstractProduct newProductInfo) {  // si exite la key con HashMap.put agrega en
        // esa llave un producto nuevo con la info nueva
        if (products.containsKey(id)) {
            products.put(id, newProductInfo);
            System.out.println(newProductInfo);
            System.out.println("prod update: ok");
        } else {
            System.out.println("ERROR: Product with id " + id + " does not exist!");
        }
    }

    public static void list() {
        if (products.isEmpty()) {
            System.out.println("There are no products in the list");
            return;
        }
        System.out.println("Catalog:");
        java.util.ArrayList<AbstractProduct> sortedList = new java.util.ArrayList<>(products.values());
        sortedList.sort(java.util.Comparator.comparing(AbstractProduct::getId));
        for (AbstractProduct product : sortedList) {
            System.out.println(product);
        }
        System.out.println("prod list: ok");
    }
}