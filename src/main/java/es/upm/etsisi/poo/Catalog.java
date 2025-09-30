package es.upm.etsisi.poo;

public class Catalog {
    private static final int PRODUCT_LIMIT = 200;
    private final static Product[] products = new Product[PRODUCT_LIMIT];
    private static int pointer = 0;  // Used as index

    public static void add(int id, String name, Category category, double price) {  // for the 'ticket add <prodID> <cantidad>' command
        if (pointer == PRODUCT_LIMIT) {
            System.out.println("Catalog is full");
        } else if (IDInProducts(id)){
            System.out.println("There is already an existing product with the same ID");
        } else if (nameInProducts(name)){
            System.out.println("There is already a product with the same name");
        } else {
            products[pointer] = new Product(id, name, category, price);
            System.out.println(products[pointer].toString());
            System.out.println("prod add: ok");
            pointer++;
        }
    }

    public static void list() {
        if (pointer == 0) {
            System.out.println("Catalog is empty");
        } else {
            System.out.println("Catalog:");
            for (int i=0; i<pointer; i++) {
                System.out.println("  " + products[i].toString());
            }
            System.out.println("prod list: ok");
        }
    }

    public static void update(int id, String field, String update) {   // Field indicates if you want to change price, name or category and update indicates the change
        boolean endCondition = false;
        if (!IDInProducts(id)) {
            System.out.println("There isn't any product with ID " + id);
        } else {
            int index = 0;
            while (!endCondition) {
                if (products[index].getId() == id) {
                    endCondition = true;
                    switch (field.toUpperCase()) {
                        case "PRICE":
                            double updatedPrice = Double.parseDouble(update); // The variable update is a String object, it is parsed to double when field corresponds to PRICE
                            products[index].setPrice(updatedPrice);
                            break;
                        case "CATEGORY":
                            if (inCategory(update)) {
                                products[index].setCategory(Category.valueOf(update));
                            }
                            break;
                        case "NAME":
                            products[index].setName(update);
                            break;
                        default:
                            System.out.println("No es un campo correcto");
                    }
                } else {
                    index++;
                }
            }
            Product prod = findByID(id);
            System.out.println(prod.toString());
            System.out.println("prod update: ok");
        }
    }

    public static void remove(int id) {
        Product prod = null;
        boolean found = false;
        for (int i=0; i<pointer; i++) {
            if (products[i].getId() == id) {
                found = true;
                prod = products[i];
                for (int j=i; j<pointer-1; j++) {
                    products[j] = products[j+1];
                }
                products[pointer - 1] = null;
                pointer--;
            }
        }
        if (found) {
            System.out.println(prod.toString());
            System.out.println("prod remove: ok");
        } else {
            System.out.println("Actually there isn't any product with ID " + id + " in the catalog");
        }
    }


    private static boolean IDInProducts(int id) {
        for (int i=0; i<pointer; i++) { // It iterates while i < pointer to avoid NullPointerException
            if (products[i].getId() == id) {
                return true;
            }
        }
        return false;
    }

    private static boolean nameInProducts(String name) {
        for (int i=0; i<pointer; i++) {
            if (products[i].getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean inCategory(String category) {
        boolean result = false;
        Category[] categories = Category.values();
        for (int i=0; i<categories.length; i++) {
            if (categories[i].name().equals(category)) {
                result = true;
            }
        }
        return result;
    }

    public static Product[] getProds() {
        return products;
    }

    public static Product findByID(int id) {
        Product result = null;
        for (int i=0; i<products.length; i++) {
            if (products[i] != null && products[i].getId() == id) {
                result = products[i];
            }
        }
        return result;
    }

}
