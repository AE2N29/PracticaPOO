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

    public void resetTicket() {
        this.products = new Product[MAX_PRODS_TICKET];
    }

    public void add(int prodID, int amount) {
        Product prodToAdd = null;
        for (int i=0; i<Catalog.getProds().length; i++) {
            if (Catalog.getProds()[i].getId() == prodID) {
                prodToAdd = Catalog.getProds()[i];
                break;
            }
        }
        if (prodToAdd == null) {
            System.out.println("There isn't any product with " + prodID + " as ID");
        } else {
            int added = 0;
            int iterator = 0;
            while (iterator < products.length && added < amount) {
                if (products[iterator] == null) {
                    products[iterator] = prodToAdd;
                    System.out.println(products[iterator].toString());
                    added++;
                }
                iterator++;
            }
            if (added < amount) {
                System.out.println("Only " + added + " units could be added. Ticket is full.");
            } else {
                System.out.println("ticket add: ok");
            }
        }
    }

    public void remove(int prodID) {
        Product[] catalogProds = Catalog.getProds();
        Product prodToAdd = null;
        for (int i=0; i<catalogProds.length; i++) {
            if (prodID == catalogProds[i].getId()) {
                prodToAdd = catalogProds[i];
            }
        }
        if (prodToAdd == null) {
            System.out.println("There isn't any product with " + prodID + " as ID");
        } else {
            for (int i=0; i<products.length; i++) {
                if (products[i].equals(prodToAdd)) {
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
            if (products[i] != null) {
                switch (products[i].getCategory()) {
                    case Category.ELECTRONICS -> electronics++;
                    case Category.CLOTHES -> clothing++;
                    case Category.STATIONERY -> stationery++;
                    case Category.BOOK -> books++;
                }
            }
        }

        for (int i=0; i<products.length; i++) {
            int count = 0;
            if (products[i] != null) {
                switch (products[i].getCategory()) {
                    case ELECTRONICS -> count = electronics;
                    case CLOTHES -> count = clothing;
                    case STATIONERY -> count = stationery;
                    case BOOK -> count = books;
                    case MERCH -> count = merch;
                }
            }
            double discount = 0;
            if (products[i] != null) {
                switch (products[i].getCategory()) {
                    case ELECTRONICS -> discount = 0.03;
                    case STATIONERY -> discount = 0.05;
                    case CLOTHES -> discount = 0.07;
                    case BOOK -> discount = 0.1;
                    case MERCH -> discount = 0.0;
                }
            }
            double prodDiscount = 0;
            if (count > 1 && products[i] != null) {
                prodDiscount += products[i].getPrice() * discount;
                totalDiscount += prodDiscount;
            }
            if (products[i] != null) {
                totalPrice += products[i].getPrice();
            }
            if (prodDiscount > 0) {
                if (products[i] != null) {
                    System.out.println(products[i].toString() + " **discount -" + Math.round(prodDiscount * 100.0) / 100.0);
                }
            } else {
                if (products[i] != null) {
                    System.out.println(products[i].toString());
                }
            }
        }
        System.out.println("Total price: " + Math.round(totalPrice * 100)/100);
        System.out.println("Total discount: " + Math.round(totalDiscount*100)/100);
        System.out.println("Final price: " + Math.round((totalPrice - totalDiscount) * 100)/100);
        System.out.println("ticket print: ok");
    }
}
