package es.upm.etsisi.poo.persistence;

import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.model.products.AbstractProduct;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.util.*;

public class ProductCatalog {
    private static final int MAX_PRODUCTS = 200;
    private static HashMap<String, AbstractProduct> products = new HashMap<>();

    private ProductCatalog() {}

    public static AbstractProduct getProduct(String id) {
        return products.get(id);
    }

    public static Map<String, AbstractProduct> getList() {
        return products;
    }


    public static ArrayList<AbstractProduct> getProducts() { // Para guardar convierte el mapa en lista
        return new ArrayList<>(products.values());
    }

    public static void setProducts(List<AbstractProduct> loadedProducts) { // Para cargar recibe lista y reconstruye el mapa
        products = new HashMap<>(); // Creamos un mapa nuevo y limpio

        if (loadedProducts != null) {
            for (AbstractProduct p : loadedProducts) {
                products.put(p.getId(), p);
            }
        }
    }

    public static void add(String id, AbstractProduct product) throws StoreException {
        if (products.containsKey(id)) { //Si el prod no existe y se respeta la max cantidad con HashMap.put lo agrega
            throw new StoreException(String.format(StaticMessages.PROD_ALREADY_EXISTS, id));
        }
        if (products.size() >= MAX_PRODUCTS) {
            throw new StoreException(StaticMessages.CAT_FULL);
        }
        products.put(id, product);
        System.out.println(product);
        System.out.println(StaticMessages.PROD_ADD_OK);
    }

    public static void remove(String id) throws StoreException {  // Si existe el prod en la lista , con HashMao.remove lo quita
        if (!products.containsKey(id)) {
            throw new StoreException(StaticMessages.PROD_NO_EXIST);
        } else {
            AbstractProduct product = products.get(id);
            System.out.println(product);
            products.remove(id);
            System.out.println(StaticMessages.PROD_REMOVE_OK);
        }
    }

    public static void update(String id, AbstractProduct newProductInfo) throws StoreException{  // si exite la key con HashMap.put agrega en
        // esa llave un producto nuevo con la info nueva
        if (products.containsKey(id)) {
            products.put(id, newProductInfo);
            System.out.println(newProductInfo);
            System.out.println(StaticMessages.PROD_UPDATE_OK);
        } else {
            throw new StoreException(StaticMessages.PROD_NO_EXIST);
        }
    }

    public static void list() {
        if (products.isEmpty()) {
            System.out.println(StaticMessages.PROD_LIST_EMPTY);
            return;
        }
        System.out.println(StaticMessages.CATALOG_HEADER);

        ArrayList<AbstractProduct> sortedList = new ArrayList<>(products.values());
        sortedList.sort(Comparator.comparing(AbstractProduct::getId));

        for (AbstractProduct product : sortedList) {
            System.out.println("  " + product);
        }
        System.out.println(StaticMessages.PROD_LIST_OK);
    }
}