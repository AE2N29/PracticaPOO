papackage es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi.poo.persistance.ProductCatalog;
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
            System.out.println("ticket new: ok");
        }
    }

    public void add(String prodID, int amount, ArrayList<String> customizations) {
        if (state.equals(TicketState.CLOSE)) {
            System.out.println(StaticMessages.CLOSED_TICKET);
        } else {
            if (amount <= 0) {
                System.out.println(StaticMessages.NEGATIVE_AMOUNT);
            } else {
                AbstractProduct prod = ProductCatalog.getProduct(prodID);
                if (prod == null) {
                    System.out.println(StaticMessages.PROD_NO_EXIST);
                    return;
                }
                if (!prod.isAvailable()) {
                    System.out.println("ERROR: Product " + prod.getName() + " is not available (expired or time restrictions).");
                    return;
                }
                if (prod instanceof Event && productList.contains(prod)) {
                    System.out.println(StaticMessages.EVENT_ALREADY_EXISTS);
                    return;
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
                            System.out.println("WARNING: Could not add text '" + text + "'. Max limit reached.");
                        }
                    }
                    prod = copy;
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

                if (!state.equals(TicketState.OPEN)) {
                    this.setState(TicketState.OPEN);
                }
            }
        }
    }

    public void remove(String prodID) {
        if (state.equals(TicketState.CLOSE)) {
            System.out.println("ERROR: Ticket is already closed");
        } else {
            boolean removed = productList.removeIf(p -> p.getId().equals(prodID));

            if (!removed) {
                System.out.print("ERROR: the product with " + prodID + " as ID wasn't in the current ticket");
            } else {
                System.out.print("ticket remove: ok");
                if (productList.isEmpty()) this.state = TicketState.EMPTY;
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
        String futureClosedId = null;
        if (printOkAtEnd && state != TicketState.CLOSE) {
            futureClosedId = updateTicketId();
            headerId = futureClosedId;
        }
        System.out.println("Ticket : " + headerId);
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
                System.out.println(" " + prod + " **discount -" + rounded(moneySaved));
            } else {
                System.out.println(prod);
            }
        }
        System.out.println(" Total price: " + rounded(totalPrice));
        System.out.println(" Total discount: " + rounded(totalDiscount));
        System.out.println(" Final Price: " + rounded(totalPrice - totalDiscount));
        if (printOkAtEnd) {
            if (state != TicketState.CLOSE) {
                setState(TicketState.CLOSE);
                this.id = headerId;
            }
            System.out.println("ticket print: ok");
        }
    }

    public void printInitialState() {
        System.out.println("Ticket: " + this.id);
        System.out.print("""
                  Total price: 0.0
                  Total discount: 0.0
                  Final Price: 0.0
                """);
    }

    private String rounded(double d) {
        double val = Math.round(d * 1000.0) / 1000.0;
        return String.valueOf(val);
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