package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.Command.AppConfigurations;
import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi.poo.model.users.Client;
import es.upm.etsisi.poo.persistence.ProductCatalog;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

public class Ticket {
    private final int MAX_PRODS_TICKET = AppConfigurations.MAX_PRODUCTS_PER_TICKET;   //  ticket no puede tener mas de 100 productos
    private final ArrayList<AbstractProduct> productList;
    private TicketState state;
    private TicketType type;//Añado en el constructor el tipo de ticket
    private String id;
    private Client client;
    private static final ArrayList<String> usedIds = new ArrayList<>();
    private TicketPrinter printer;                         // Guardamos los ids usados para no repetirlos

    public Ticket(String id, TicketType ticketType) {
        this.type = ticketType;
        this.productList = new ArrayList<>();
        this.state = TicketState.EMPTY;
        this.id = id;
        usedIds.add(id);
    }
    public Ticket(Client client, String ticketType) {
        this(createTicketId(), client, ticketType);
    }
    public Ticket(String id, Client client, String ticketType) {
        this.productList = new ArrayList<>();
        this.state = TicketState.EMPTY;
        this.id = id;
        this.client = client;
        this.type = parseTicketType(ticketType);
        usedIds.add(id);
    }
    private static TicketType parseTicketType(String typeStr) {
        if (typeStr == null) {
            return TicketType. PERSONAL;
        }

        return switch (typeStr.  toLowerCase()) {
            case "c" -> TicketType.COMBINED;
            case "s" -> TicketType.SERVICE;
            default -> TicketType.PERSONAL;
        };
    }
    public Client getClient() {
        return client;
    }
    public TicketType getType() {
        return type;
    }
    public void print() {
        if (printer == null) {
            printer = TicketPrinterFactory.getPrinterForClient(client);
        }
        printer. print(this);
    }
    //Enviar copia de la lista si es necesario para evitar modificaciones externas
    public ArrayList<AbstractProduct> getProductList() {
        return new ArrayList<>(productList);
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

    private AbstractProduct createProductCopy(AbstractProduct original) throws StoreException {
        if (original instanceof PersonalizedProduct) {
            PersonalizedProduct p = (PersonalizedProduct) original;
            return new PersonalizedProduct(
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getMaxCustomTexts()
            );
        } else if (original instanceof StockProduct) {
            StockProduct s = (StockProduct) original;
            return new StockProduct(
                    s.getId(),
                    s.getName(),
                    s. getCategory(),
                    s.getPrice()
            );
        } else if (original instanceof Event) {
            Event e = (Event) original;
            return new Event(
                    e.getId(),
                    e.getName(),
                    e.getExpirationDate(),
                    e.getPrice(),
                    e. getMaxPeopleAllowed(),
                    e.getEventType()
            );
        } else if (original instanceof Service) {
            Service svc = (Service) original;
            return new Service(
                    svc.getServiceType(),
                    svc.getExpirationDate()
            );
        }

        return original;
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
        if (state.  equals(TicketState. CLOSE)) {
            System.out.  println(StaticMessages. CLOSED_TICKET);
        } else {
            if (amount <= 0) {
                System.out. println(StaticMessages.NEGATIVE_AMOUNT);
            } else {
                AbstractProduct prod = ProductCatalog.getProduct(prodID);
                if (prod == null) {
                    System.out.println(StaticMessages. PROD_NO_EXIST);
                    return;
                }
                if (!  prod.isAvailable()) {
                    System.out.println("ERROR: Product " + prod.getName() +
                            " is not available (expired or time restrictions).");
                    return;
                }
                if (prod instanceof Event && productList. contains(prod)) {
                    System.out.println(StaticMessages.EVENT_ALREADY_EXISTS);
                    return;
                }
                AbstractProduct productToAdd = createProductCopy(prod);
                //Se crean copias para añadir al ticket, para que cuando se modifica el precio de un producto no cambie el ticket
                // Manejar customizaciones si es PersonalizedProduct

                if (prod instanceof PersonalizedProduct && customizations != null &&
                        !  customizations.  isEmpty()) {
                    PersonalizedProduct copy = (PersonalizedProduct) productToAdd;
                    for (String text : customizations) {
                        boolean added = copy.addCustomText(text);
                        if (!  added) {
                            System. out.println("WARNING: Could not add text '" + text +
                                    "'.  Max limit reached.");
                        }
                    }
                    productToAdd = copy;
                } else if (customizations != null && ! customizations. isEmpty()) {
                    System.out.println("WARNING: Product " + productToAdd.getName() +
                            " is not customizable.  Ignoring texts.");
                }

                if (amount + productTotalUnits() > MAX_PRODS_TICKET) {
                    System. out.println("ERROR: Max products allowed per ticket exceeded!");
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    productList.add(createProductCopy(productToAdd));
                }

                System.out.println("ticket add: ok");

                if (!  state.equals(TicketState.OPEN)) {
                    this.setState(TicketState.OPEN);
                }
            }
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


    private int productTotalUnits() {
        return productList.size();
    }

    private String rounded(double d) {
        double val = Math.round(d * 1000.0) / 1000.0;
        return String. valueOf(val);
    }
    public void printInitialState() {
        System.out.println("Ticket:  " + this.id);
        System.out.print("""
            Total price: 0.0
            Total discount: 0.0
            Final Price: 0.0
            """);
    }

    public TicketState getState() { return state; }

    public void setState(TicketState state) { this.state = state; }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public static boolean isIdRegistered(String id) { return usedIds.contains(id); }

}