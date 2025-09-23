package es.upm.etsisi.poo;

public class Ticket {
    private final int PRODUCT_LIMIT = 200;
    private Product[] products;
    private int ocupation = 0;  // It represents the number of elements -1 in the list and used as index
    private int actualID = 1;

    public Ticket() {
        this.products = new Product[PRODUCT_LIMIT];
        this.ocupation = 0;
    }

    public void addProduct(String name, int quantity, Category category, double price) {  // for the 'ticket add <prodID> <cantidad>' command
        if (ocupation + quantity > PRODUCT_LIMIT) {
            System.out.println("El ticket ha alcanzado su límite");
        } else {
            for (int i=0; i<quantity; i++) {
                products[ocupation] = new Product(this.actualID, name, category, price);
                ocupation++;
                actualID++;
            }
        }
    }

    public void list() {
        if (ocupation == 0) {
            System.out.println("El ticket está vacío");
        } else {
            int iterator = 0;
            while (products[iterator] != null) {
                System.out.println(products[iterator].toString());
            }
        }
    }

    public void newTicket() {
        Product[] removedTicket = new Product[PRODUCT_LIMIT];
        this.products = removedTicket;
        this.ocupation = 0;
        this.actualID = 1;
    }
}
