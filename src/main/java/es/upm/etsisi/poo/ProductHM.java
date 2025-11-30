package es.upm.etsisi.poo;

import es.upm.etsisi.poo.products.AbstractProduct;

import java.util.HashMap;
import java.util.Map;

public class ProductHM {
    private static final int MAX_PRODUCTS = 200;
    private static final HashMap<String, AbstractProduct> list = new HashMap<>();

    private ProductHM(){}

    public static AbstractProduct getProduct(String id) {
        return list.get(id);
    }

    public static Map<String, AbstractProduct> getList()
    {
        return list;
    }

    public static void add(String id, AbstractProduct product)  throws IllegalArgumentException{
        //Si el prod no existe y se respeta la max cantidad con HashMap.put lo agrega

        if (list.containsKey(id)) {
            throw new IllegalArgumentException("ERROR: Product couldn't be added!");
        }
        if(list.size() >= MAX_PRODUCTS) {
            throw new IllegalArgumentException("ERROR: The list is full");
        }
            list.put(id, product);
            System.out.println(product);
            System.out.println("prod add: ok");
    }

    public static void remove(String id) {  // Si existe el prod en la lista , con HashMao.remove lo quita
        if (!list.containsKey(id)) {
            System.out.println("ERROR: Product with id " + id + " does not exist!");
        }
        else {
            AbstractProduct product = list.get(id);
            System.out.println(product);
            list.remove(id);
            System.out.println("prod remove: ok");
        }
    }

    public static void update(String id, AbstractProduct newProductInfo) {  // si exite la key con HashMap.put agrega en
                                                                 // esa llave un producto nuevo con la info nueva
        if (list.containsKey(id)) {
            list.put(id, newProductInfo);
            System.out.println(newProductInfo);
            System.out.println("prod update: ok");
        }
        else {
            System.out.println("ERROR: Product with id " + id + " does not exist!");
        }
    }

    public static void list() {

        if (list.isEmpty()) {
            System.out.println("There are no products in the list");
            return;
        }
        System.out.println("Catalog:");

        for (Map.Entry<String, AbstractProduct> couple : list.entrySet()) {  //  bucle que itera sobre cada pareja de datos (id, product) y los llama couple
            System.out.println(couple.getValue());
        }
        System.out.println("prod list: ok");
    }
}