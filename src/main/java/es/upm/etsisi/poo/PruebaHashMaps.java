package es.upm.etsisi.poo;

import java.util.HashMap;

public class PruebaHashMaps {


    private static final int MAX_PRODUCTS = 200;
    private final HashMap<Integer, Product> list;

    public PruebaHashMaps() {
        this.list = new  HashMap<>();
    }

    public void add(int id, Product product)
    {
        if(!list.containsKey(id) && list.size() < MAX_PRODUCTS)
        {
            list.put(id, product);
        }
        else{
            System.out.println("ERROR: The product with id " + id + " already exists or the list is full");
        }
    }
    public void remove(int id)
    {
        list.remove(id);
    }

    public void update(int id, Product newProductInfo)
    {
        if(list.containsKey(id))
        {
            list.put(id, newProductInfo);
        }
    }

    public void list()
    {
        if(list.isEmpty())
        {
            System.out.println("There are no products in the list");
        }
        for(int id : list.keySet())
        {
            Product product = list.get(id);
            System.out.println(product.toString());
        }
    }
}