package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi.poo.persistence.ProductCatalog;
import es.upm.etsisi.poo.utils.StaticMessages;

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
        if (!isIdRegistered(formattedDate + random5DigitsNum)) {
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
        if (state.equals(TicketState.CLOSE)) {
            System.out.println(StaticMessages.CLOSED_TICKET);
        } else {
            this.productList.clear();
            this.state = TicketState.EMPTY;
            System.out.println(StaticMessages.TICKET_NEW_OK);
        }
    }

    public void add(String prodID, int amount, ArrayList<String> customizations) throws StoreException {
        if (state.equals(TicketState.CLOSE)) {
            throw new StoreException(StaticMessages.CLOSED_TICKET);
        }
        if (amount <= 0) {
            System.out.println(StaticMessages.NEGATIVE_AMOUNT);
        }
        AbstractProduct prod = ProductCatalog.getProduct(prodID);
        if (prod == null) {
            throw new StoreException(StaticMessages.PROD_NO_EXIST);
        }
        if (!prod.isAvailable()) {
            throw new StoreException(String.format(StaticMessages.PROD_UNAVAILABLE, prod.getName()));
        }
        if (prod instanceof Event && productList.contains(prod)) {
            throw new StoreException(StaticMessages.EVENT_ALREADY_EXISTS);
        }
        if (prod instanceof PersonalizedProduct && customizations != null && !customizations.isEmpty()) {
            PersonalizedProduct original = (PersonalizedProduct) prod;
            PersonalizedProduct copy = new PersonalizedProduct(
                    original.getId(), original.getName(), original.getCategory(),
                    original.getPrice(), original.getMaxCustomTexts()
            );

            for (String text : customizations) {
                boolean added = copy.addCustomText(text);
                if (!added) {
                    System.out.println(String.format(StaticMessages.WARN_CUSTOM_LIMIT, text));
                }
            }
            prod = copy;
        } else
        {
            if (customizations != null && !customizations.isEmpty()) {
                System.out.println(String.format(StaticMessages.WARN_NOT_CUSTOMIZABLE, prod.getName()));
            }
            // Copiar
            if (prod instanceof StockProduct) {
                prod = new StockProduct((StockProduct) prod);
            }
        }
        if (amount + productTotalUnits() > MAX_PRODS_TICKET) {
            throw new StoreException(StaticMessages.MAX_PRODS_EXCEED);
        }
        for (int i = 0; i < amount; i++) {
            productList.add(prod);
        }

        printTicketLinesSorted(false);
        System.out.println(StaticMessages.TICKET_ADD_OK);

        if (!state.equals(TicketState.OPEN)) {
            this.setState(TicketState.OPEN);
        }

    }

    public void remove(String prodID) throws StoreException {
        if (state.equals(TicketState.CLOSE)) {
            throw new StoreException(StaticMessages.CLOSED_TICKET);
        }
        boolean removed = productList.removeIf(p -> p.getId().equals(prodID));

        if (!removed) {
            throw new StoreException(String.format(StaticMessages.PROD_NOT_IN_TICKET, prodID));
        } else {
            System.out.print(StaticMessages.TICKET_REMOVE_OK);
            if (productList.isEmpty()) {
                this.state = TicketState.EMPTY;
            }
        }
    }

    public void print() {
        printTicketLinesSorted(true);
    }

    private int productTotalUnits() {
        return productList.size();
    }

    private void printTicketLinesSorted(boolean printOkAtEnd) {
        double totalPrice = 0.0, totalDiscount = 0.0;
        ArrayList<AbstractProduct> sorted = new ArrayList<>(this.productList); // Copia segura
        sorted.sort(Comparator.comparing(AbstractProduct::getName));
        int merchCount = 0, clothesCount = 0, electronicsCount = 0, stationeryCount = 0, bookCount = 0;
        for (AbstractProduct prod : sorted) {
            if (prod instanceof StockProduct) {
                switch (((StockProduct) prod).getCategory()) {
                    case MERCH -> merchCount++;
                    case STATIONERY -> stationeryCount++;
                    case CLOTHES -> clothesCount++;
                    case BOOK -> bookCount++;
                    case ELECTRONICS -> electronicsCount++;
                }
            }
        }

        String headerId = this.id;
        String futureClosedId;
        if (printOkAtEnd && state != TicketState.CLOSE) {
            futureClosedId = updateTicketId();
            headerId = futureClosedId;
        }
        System.out.println(StaticMessages.TICKET_HEADER + headerId);
        for (AbstractProduct prod : sorted) {
            totalPrice += prod.getPrice();
            double discountPct = 0.0;
            if (prod instanceof StockProduct) {
                Category cat = ((StockProduct) prod).getCategory();
                boolean applies = false;
                switch (cat) {
                    case MERCH -> applies = merchCount >= 2;
                    case STATIONERY -> applies = stationeryCount >= 2;
                    case CLOTHES -> applies = clothesCount >= 2;
                    case BOOK -> applies = bookCount >= 2;
                    case ELECTRONICS -> applies = electronicsCount >= 2;
                }
                if (applies) {
                    switch (cat) {
                        case STATIONERY -> discountPct = 0.05;
                        case CLOTHES -> discountPct = 0.07;
                        case BOOK -> discountPct = 0.1;
                        case ELECTRONICS -> discountPct = 0.03;
                    }
                }
            }
            if (discountPct > 0) {
                double moneySaved = prod.getPrice() * discountPct;
                totalDiscount += moneySaved;
                System.out.println(" " + prod + StaticMessages.DISCOUNT_SUFFIX + rounded(moneySaved));
            } else {
                System.out.println(prod);
            }
        }
        System.out.println(StaticMessages.TOTAL_PRICE_LABEL + rounded(totalPrice));
        System.out.println(StaticMessages.TOTAL_DISCOUNT_LABEL + rounded(totalDiscount));
        System.out.println(StaticMessages.FINAL_PRICE_LABEL + rounded(totalPrice - totalDiscount));
        if (printOkAtEnd) {
            if (state != TicketState.CLOSE) {
                setState(TicketState.CLOSE);
                this.id = headerId;
            }
            System.out.println(StaticMessages.TICKET_PRINT_OK);
        }
    }

    public void printInitialState() {
        System.out.println(StaticMessages.TICKET_HEADER + this.id);
        System.out.print(StaticMessages.INITIAL_STATE_BLOCK);
    }

    private String rounded(double d) {
        double val = Math.round(d * 1000.0) / 1000.0;
        return String.valueOf(val);
    }

    public TicketState getState() { return state; }

    public void setState(TicketState state) { this.state = state; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public static boolean isIdRegistered(String id) { return usedIds.contains(id); }

}