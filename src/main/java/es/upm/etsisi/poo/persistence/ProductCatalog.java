package es.upm.etsisi.poo.persistence;

import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.model.products.AbstractProduct;
import es.upm.etsisi.poo.model.repositories.ProductsRepo;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class ProductCatalog {
    private static final int MAX_PRODUCTS = 200;
    private static final HashMap<String, AbstractProduct> products = new HashMap<>();
    private static ProductsRepo productsRepo;  // Gestor de productos de la BBDD local

    private ProductCatalog() {}

    public static AbstractProduct getProduct(String id) {
        return products.get(id);
    }

    public static Map<String, AbstractProduct> getList() {
        return products;
    }

    public static void add(String id, AbstractProduct product) throws StoreException {
        if (products.containsKey(id)) { //Si el prod no existe y se respeta la max cantidad con HashMap.put lo agrega
            throw new StoreException(String.format(StaticMessages.PROD_ALREADY_EXISTS, id));
        }
        if (products.size() >= MAX_PRODUCTS) {
            throw new StoreException(StaticMessages.CAT_FULL);
        }
        products.put(id, product);
        productsRepo.save(product); // Guarda el producto en la BBDD local
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
            productsRepo.delete(product);  // Elimina el producto de la BBDD local
            System.out.println(StaticMessages.PROD_REMOVE_OK);
        }
    }

    public static void update(String id, AbstractProduct newProductInfo) throws StoreException{  // si exite la key con HashMap.put agrega en
        // esa llave un producto nuevo con la info nueva
        if (products.containsKey(id)) {
            products.put(id, newProductInfo);
            // Referenciar al producto actualizado por ID
            AbstractProduct referencedProd = getProduct(id);
            productsRepo.save(referencedProd);  // El método save también sirve para actualizar en la BBDD local
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