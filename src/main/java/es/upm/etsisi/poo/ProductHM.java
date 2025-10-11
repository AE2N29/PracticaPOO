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

    public static void add(int id, Product product)   //Si el prod no existe y se respeta la max cantidad con HashMap.put lo agrega
    {
        if(!list.containsKey(id) && list.size() < MAX_PRODUCTS) {
            list.put(id, product);
            System.out.println(product.toString());
            System.out.println("prod add: ok");
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

    public static void remove(int id)  // Si existe el prod en la lista , con HashMao.remove lo quita
    {
        if(!list.containsKey(id)) {
            System.out.println("ERROR: couldn't find the product");
        }
        else{
            Product product = list.get(id);
            System.out.println(product.toString());
            list.remove(id);
            System.out.println("prod remove: ok");
        }
    }

    public static void update(int id, Product newProductInfo)   // si exite la key con HashMap.put agrega en
    {                                                           // esa llave un producto nuevo con la info nueva
        if(list.containsKey(id)) {
            list.put(id, newProductInfo);
            System.out.println(newProductInfo.toString());
            System.out.println("prod update: ok");
        }
        else{
            System.out.println("ERROR: Product with id " + id + " does not exist!");
        }
    }

    public static void list()  // metodo Hashmaps.forEach pilla cada llave y en este caso imprime el product con el .String
    {
        if(list.isEmpty()) {
            System.out.println("There are no products in the list");
            return;
        }
        System.out.println("Catalog:");
        list.forEach((key,product)->  {
            System.out.println(product.toString());
        });
        System.out.println("prod list: ok");
    }
}