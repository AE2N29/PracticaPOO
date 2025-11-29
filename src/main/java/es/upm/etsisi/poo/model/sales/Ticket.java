package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.persistance.ProductCatalog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class Ticket {
    private final int MAX_PRODS_TICKET = 100; //  ticket no puede tener mas de 100 productos
    private final ArrayList<Product> productList;
    private TicketState state;
    private String id;

    public Ticket(String id) {
        this.productList = new ArrayList<>();
        this.state = TicketState.EMPTY;
        this.id = id;
    }

    public Ticket() {
        this(createTicketId());
    }

    private static String createTicketId(){ // crea el Id cuando no se pasa como parámetro
        String pattern = "YY-MM-dd-HH:mm-";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(formatter);
        int random5DigitsNum = (int) (Math.random() * 90000) + 10000;
        return (formattedDate + random5DigitsNum);
    }

    private String updateTicketId() { // actualiza el id cuando el ticket se cierra
        String pattern = "-YY-MM-dd-HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(formatter);
        return (this.id + formattedDate);
    }

    public void resetTicket() {//  uso de ArrayList.clear para resetear el ticket
        if (state.equals(TicketState.CLOSED)) {
            System.out.println("ERROR: Ticket is already closed");
        } else {
            this.productList.clear();
            this.state = TicketState.EMPTY;
            System.out.println("ticket new: ok");
        }
    }

    public void add(int prodID, int amount) {
        if (state.equals(TicketState.CLOSED)) {
            System.out.println("ERROR: Ticket is already closed");
        } else {
            if (amount <= 0) {
                System.out.println("ticket add: ok");
                return;
            }
            Product prod = ProductCatalog.getProduct(prodID);
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
            if (!state.equals(TicketState.ACTIVE)) {
                this.state = TicketState.ACTIVE;
            }
        }
    }

    public void remove(int prodID) {
        if (state.equals(TicketState.CLOSED)) {
            System.out.println("ERROR: Ticket is already closed");
        } else {
            Product prod = ProductCatalog.getProductD(prodID);
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
        double totalPrice = 0.0, totalDiscount = 0.0;
        ArrayList<Product> sorted = this.productList;
        sorted.sort(Comparator.comparing(Product::getName));
        int merchCount = 0, clothesCount = 0, electronicsCount = 0, stationeryCount = 0, bookCount = 0;
        for (Product prod : sorted) {
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
        for (Product prod : sorted) {
            totalPrice += prod.getPrice();
            int actualCount = 0;
            switch (prod.getCategory()) {
                case MERCH -> actualCount = merchCount;
                case STATIONERY ->  actualCount = stationeryCount;
                case CLOTHES -> actualCount = clothesCount;
                case BOOK ->  actualCount = bookCount;
                case ELECTRONICS -> actualCount = electronicsCount;
            }
            if (actualCount > 1) {
                double actualDiscount = 0.0;
                switch (prod.getCategory()) {
                    case MERCH -> actualDiscount = 0.0;
                    case STATIONERY -> actualDiscount = 0.05;
                    case CLOTHES -> actualDiscount = 0.07;
                    case BOOK -> actualDiscount = 0.1;
                    case ELECTRONICS -> actualDiscount = 0.03;
                }
                totalDiscount += actualDiscount;
                System.out.println(prod + " **discount -" + rounded(actualDiscount));
            } else {
                System.out.println(prod);
            }
        }
        if (printOkAtEnd) {
            System.out.println("Total price: " + rounded(totalPrice));
            System.out.println("Total discount: " + rounded(totalDiscount));
            System.out.println("Final price: " + rounded(totalPrice - totalDiscount));
            setState(TicketState.CLOSED);
            this.id = updateTicketId();
        }
    }

    private double rounded(double d) {
        return (d*100.0) / 100.0;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}