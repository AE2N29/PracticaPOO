package es.upm.etsisi.poo;
import java.util.HashMap;

public class ProductHM {
    private static final int MAX_PRODUCTS = 200;
    private static final HashMap<Integer, Product> list = new HashMap<>();
    private ProductHM(){}

    public static Product getProduct(int id)
    {
        return list.get(id);
    }

    public static void add(int id, Product product)
    {
        if(!list.containsKey(id) && list.size() < MAX_PRODUCTS) {
            list.put(id, product);

        }
        else{
            if(list.containsKey(id)) {
                System.out.println("ERROR: Product already exists!");
            }
            else {
                System.out.println("ERROR: the list is full");
            }
        }
    }

    public static void remove(int id)
    {
        if(!list.containsKey(id)) {
            System.out.println("ERROR: couldn´t find the product");
        }
        else{
            list.remove(id);
        }
    }

    public static void update(int id, Product newProductInfo)
    {
        if(list.containsKey(id)) {
            list.put(id, newProductInfo);
        }
        else{
            System.out.println("ERROR: Product with id " + id + " does not exist!");
        }
    }

    public static void list()
    {
        if(list.isEmpty()) {
            System.out.println("There are no products in the list");
            return;
        }
        list.forEach((key,product)->  {
            System.out.println("Product with id " + key + " is " + product.toString());
        });
    }
}