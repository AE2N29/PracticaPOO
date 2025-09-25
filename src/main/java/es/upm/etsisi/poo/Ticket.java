package es.upm.etsisi.poo;

public class Ticket {
    private final int MAX_PRODS_TICKET = 100; // For this version it is assumed that tickets won't have more than 100 products
    private Product[] products;

    public Ticket(Product[] products) {
        this.products = products;
    }

    public Ticket() {
       this.products = new Product[MAX_PRODS_TICKET]; // Everything is initialized to null
    }

    public Ticket ticketNew() {
        return new Ticket();
    }

    public void add(Product product, int amount){
        int i = 0;
         while(i < amount){
             int products_amount = products.length + 1;
          products = new Product[products.length];
          i++;
         }
    }

    public void ticketRemove(Product product){
        Product[] actualCatalog = Catalog.getCopyOfProds();
        boolean IDexists = false;
        int iterator = 0;
        for (int i=0; i<actualCatalog.length; i++) {
            if (product.getId() == actualCatalog[i].getId()) {
                IDexists = true;    // Acts like IDInProducts from this class
            }
        }
        if (!IDexists) {
            System.out.println("The product " + product.getName() + " does not exist");
        } else {
            for (int i=0; i<products.length; i++) {
                if (products[i].equals(product)) {
                    for (int j=i; j<products.length-1; j++) { // For sentence ends after reaching products.length - 1 to avoid IndexOutOfBounds
                        products[j] = products[j+1];
                    }
                    products[products.length-1] = null; // It removes the last object correctly
                    i--; // returns to the previous position. Otherwise, it would skip an index
                }
            }
        }
    }
}
