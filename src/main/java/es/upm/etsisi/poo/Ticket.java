package es.upm.etsisi.poo;

import es.upm.etsisi.poo.products.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class Ticket {
    private final int MAX_PRODS_TICKET = 100; //  ticket no puede tener mas de 100 productos
    private final ArrayList<AbstractProduct> productList;
    private TicketState state;
    private String id;
    private static final ArrayList<String> usedIds = new ArrayList<>(); // Guardamos los ids usados para no repetirlos

    public Ticket(String id) {
        this.productList = new ArrayList<>();
        this.state = TicketState.EMPTY;
        this.id = id;
        usedIds.add(id);
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
        if(!isIdRegistered(formattedDate + random5DigitsNum)){
            return (formattedDate + random5DigitsNum);
        } else {
            return createTicketId();
        }
    }

    private String updateTicketId() { // actualiza el id cuando el ticket se cierra
        String pattern = "-YY-MM-dd-HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(formatter);
        return (this.id + formattedDate);
    }

    // Este metodo ya no se utiliza en la segunda entrega, el new ticket ya no resetea el ticket. No lo borro por si lo usamos en un futuro
    public void resetTicket() {//  uso de ArrayList.clear para resetear el ticket
        if (state.equals(TicketState.CLOSED)) {
            System.out.println("ERROR: Ticket is already closed");
        } else {
            this.productList.clear();
            this.state = TicketState.EMPTY;
            System.out.println("ticket new: ok");
        }
    }

    public void add(String prodID, int amount, ArrayList<String> customizations) {
        if (state.equals(TicketState.CLOSED)) {
            System.out.println("ERROR: Ticket is already closed");
        } else {
            if (amount <= 0) {
                System.out.println("ERROR: Amount must be greater than 0");
            }else {
                AbstractProduct prod = ProductHM.getProduct(prodID);
                if (prod == null) {
                    System.out.println("ERROR: There isn't any product with " + prodID + " as ID");
                    return;
                }
                if (!prod.availability()) {
                    System.out.println("ERROR: Product " + prod.getName() + " is not available (expired or time restrictions).");
                    return;
                }
                if (prod instanceof EventProd && productList.contains(prod)) {
                    System.out.println("ERROR: Event/Food product " + prodID + " is already in the ticket.");
                    return;
                }
                if (prod instanceof WrapProduct) {
                    if (customizations != null && !customizations.isEmpty()) {
                        WrapProduct wrapProd = (WrapProduct) prod;
                        for (String text : customizations) {
                            boolean added = wrapProd.addCustomText(text);
                            if (!added) {
                                System.out.println("WARNING: Could not add text '" + text + "'. Max limit reached.");
                            }
                        }
                        System.out.println("   (Added with customizations)");
                    }
                } else if (customizations != null && !customizations.isEmpty()) {
                    System.out.println("WARNING: Product " + prod.getName() + " is not customizable. Ignoring texts.");
                }
                if (amount + productTotalUnits() > MAX_PRODS_TICKET) {
                    System.out.println("ERROR: Max products allowed per ticket exceeded!");
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    productList.add(prod);
                }

                printTicketLinesSorted(false);
                System.out.println("ticket add: ok");

                if (!state.equals(TicketState.ACTIVE)) {
                    this.setState(TicketState.ACTIVE);
                }
            }
        }
    }

    public void remove(String prodID) {
        if (state.equals(TicketState.CLOSED)) {
            System.out.println("ERROR: Ticket is already closed");
        } else {
            AbstractProduct prod = ProductHM.getProduct(prodID);
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
        for (AbstractProduct prod: productList) {  // recorre todas las cantidades del mapa, llamando a cada una units
            if (prod != null) {
                sum++;
            }
        }
        return sum;
    }

    private void printTicketLinesSorted(boolean printOkAtEnd) {
        double totalPrice = 0.0, totalDiscount = 0.0;
        ArrayList<AbstractProduct> sorted = this.productList;
        sorted.sort(Comparator.comparing(AbstractProduct::getName));
        int merchCount = 0, clothesCount = 0, electronicsCount = 0, stationeryCount = 0, bookCount = 0;
        for (AbstractProduct prod : sorted) {
            if (prod != null) {
                if (prod instanceof StockProducts) {
                    switch (((StockProducts) prod).getCategory()) {
                        case MERCH -> merchCount++;
                        case STATIONERY -> stationeryCount++;
                        case CLOTHES -> clothesCount++;
                        case BOOK -> bookCount++;
                        case ELECTRONICS -> electronicsCount++;
                    }
                }
            }
        }
        for (AbstractProduct prod : sorted) {
            totalPrice += prod.calculatePrice();
            int actualCount = 0;
            if (prod instanceof StockProducts) {
                switch (((StockProducts) prod).getCategory()) {
                    case MERCH -> actualCount = merchCount;
                    case STATIONERY ->  actualCount = stationeryCount;
                    case CLOTHES -> actualCount = clothesCount;
                    case BOOK ->  actualCount = bookCount;
                    case ELECTRONICS -> actualCount = electronicsCount;
                }
            }
            if (actualCount > 1) {
                double actualDiscount = 0.0;
                if (prod instanceof StockProducts) {
                    switch (((StockProducts) prod).getCategory()) {
                        case MERCH -> actualDiscount = 0.0;
                        case STATIONERY -> actualDiscount = 0.05;
                        case CLOTHES -> actualDiscount = 0.07;
                        case BOOK -> actualDiscount = 0.1;
                        case ELECTRONICS -> actualDiscount = 0.03;
                    }
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
            setId(updateTicketId());
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

    public static boolean isIdRegistered(String id) {
        return usedIds.contains(id);
    }
}