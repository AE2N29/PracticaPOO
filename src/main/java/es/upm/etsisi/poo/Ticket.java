package es.upm.etsisi.poo;

import java.util.ArrayList;

public class Ticket {
    private final int MAX_PRODS_TICKET = 100; //  ticket no puede tener mas de 100 productos
    private final ArrayList<Product> productList;

    public Ticket() {
        this.productList = new ArrayList<>();
    }

    public void resetTicket() {  //  uso de ArrayList.clear para resetear el ticket
        this.productList.clear();
        System.out.println("ticket new: ok");
    }

    public void add(int prodID, int amount) {
        if (amount <= 0) {
            System.out.println("ticket add: ok");
            return;
        }
        Product prod = ProductHM.getProduct(prodID);
        if (prod == null) {
            System.out.println("ERROR: There isn't any product with " + prodID + " as ID");
            return;
        }
        if (amount + productTotalUnits() <= MAX_PRODS_TICKET){ // verificar que se respete la max cantidad
            for (int i=0; i<amount; i++) {
                productList.add(prod);
            }
        } else {
            System.out.println("ERROR: Couldn't add, max products allowed per ticket exceeded!");
            return;
        }
        // Imprimir el contenido del ticket
        printTicketLinesSorted(false);
        System.out.println("ticket add: ok");
    }

    public void remove(int prodID) {
        Product prod = ProductHM.getProduct(prodID);
        if (prod == null) {  // si no encuentra el producto con este id en la base de datos
            System.out.println("ERROR: There isn't any product with " + prodID + " as ID");
            return;
        }
        if (!productList.contains(prod)) {  // si no existe producto con ID proporcionada en el ticket, mensaje de error
            System.out.print("ERROR: the product with " + prodID + " as ID wasn't in the current ticket");
            return;
        }
        productList.remove(prod);
        System.out.print("ticket remove: ok");  // si existe el producto, se devuelve confirmacion de borrado
    }

    public void print() {
        printTicketLinesSorted(true);
    }

    private int productTotalUnits(){ // metodo que devuelve la cantidad de productos que haya en el mapa
        int sum = 0;
        for (Product prod: productList) {  // recorre todas las cantidades del mapa, llamando a cada una units
            if (prod != null) {
                sum++;
            }
        }
        return sum;
    }

    private void printTicketLinesSorted(boolean printOkAtEnd) {
        // contar cuantos productos de cada categoria hay
        int merchCount = 0, clothesCount = 0, electronicsCount = 0, stationeryCount = 0, bookCount = 0;
        for (Product prod : productList) {
            if (prod != null) {
                switch (prod.getCategory()) {
                    case MERCH -> merchCount++;
                    case STATIONERY -> stationeryCount++;
                    case CLOTHES -> clothesCount++;
                    case BOOK -> bookCount++;
                    case ELECTRONICS -> electronicsCount++;
                }
            }
        }
        for (Product prod : productList) {

        }
    }
}