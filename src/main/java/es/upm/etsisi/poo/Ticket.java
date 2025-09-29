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
        int i = 0;         while(i < amount) {
             int products_amount = products.length + 1;
          products = new Product[products.length];
          i++;
         }
    }

    public void remove(Product product) {
        Product[] actualCatalog = Catalog.getProds();
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
            System.out.print("ticket remove: ok");
        }
    }

    public void print() {
        double totalPrice = 0, totalDiscount = 0;
        int books = 0, clothing = 0, stationery = 0, electronics = 0, merch = 0;
        for (int i=0; i<products.length; i++) {
            switch (products[i].getCategory()) {
                case Category.ELECTRONICA -> electronics++;
                case Category.ROPA -> clothing++;
                case Category.PAPELERIA -> stationery++;
                case Category.LIBRO -> books++;
            }
        }

        for (int i=0; i<products.length; i++) {
            int count = 0;
            switch (products[i].getCategory()) {
                case ELECTRONICA -> count = electronics;
                case ROPA -> count = clothing;
                case PAPELERIA -> count = stationery;
                case LIBRO -> count = books;
                case MERCH -> count = merch;
            }
            double discount = 0;
            switch (products[i].getCategory()) {
                case ELECTRONICA -> discount = 0.03;
                case PAPELERIA -> discount = 0.05;
                case ROPA -> discount = 0.07;
                case LIBRO -> discount = 0.1;
                case MERCH -> discount = 0.0;
            }
            if (count > 1 && products[i] != null) {
                double prodDiscount = products[i].getPrice() * discount;
                System.out.println(products[i].toString() + " **discount -" + prodDiscount);
                totalDiscount += prodDiscount;
                totalPrice += products[i].getPrice();
            }
        }
        System.out.println("Total price: " + totalPrice);
        System.out.println("Total discount: " + totalDiscount);
        System.out.println("Final price: " + (totalPrice - totalDiscount));
        System.out.println("ticket print: ok");
    }
}
