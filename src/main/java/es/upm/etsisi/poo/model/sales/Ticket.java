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
    private TicketType type;  //Añado en el constructor el tipo de ticket
    private String id;
    private Client client;
    private static final ArrayList<String> usedIds = new ArrayList<>();// Guardamos los ids usados para no repetirlos

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

                // Manejar customizaciones si es PersonalizedProduct
                if (prod instanceof PersonalizedProduct && customizations != null &&
                        !  customizations.  isEmpty()) {
                    PersonalizedProduct original = (PersonalizedProduct) prod;
                    PersonalizedProduct copy = new PersonalizedProduct(
                            original.getId(), original.getName(), original.getCategory(),
                            original.getPrice(), original.getMaxCustomTexts()
                    );

                    for (String text : customizations) {
                        boolean added = copy.addCustomText(text);
                        if (!  added) {
                            System. out.println("WARNING: Could not add text '" + text +
                                    "'.  Max limit reached.");
                        }
                    }
                    prod = copy;
                } else if (customizations != null && ! customizations. isEmpty()) {
                    System.out.println("WARNING: Product " + prod.getName() +
                            " is not customizable.  Ignoring texts.");
                }

                if (amount + productTotalUnits() > MAX_PRODS_TICKET) {
                    System. out.println("ERROR: Max products allowed per ticket exceeded!");
                    return;
                }
                for (int i = 0; i < amount; i++) {
                    productList.add(prod);
                }

                printTicketLinesSorted(false);
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

    public void print() {
        printTicketLinesSorted(true);
    }

    private int productTotalUnits() {
        return productList.size();
    }

    private void printTicketLinesSorted(boolean printOkAtEnd) {
        // Ver servicios
        int serviceCount = 0;
        if (this.type == TicketType. COMBINED || this.type == TicketType.SERVICE) {
            for (AbstractProduct p : productList) {
                if (p instanceof Service) {
                    serviceCount++;
                }
            }
        }

        // Usar AppConfigurations para el descuento corporativo
        double companyDiscountRate = serviceCount * AppConfigurations.CORPORATE_SERVICE_DISCOUNT;

        //  CONTEO DE PRODUCTOS
        double totalPrice = 0.0, totalDiscount = 0.0;
        ArrayList<AbstractProduct> sorted = new ArrayList<>(this.  productList);
        sorted.sort(Comparator.comparing(AbstractProduct::getName));

        int merchCount = 0, clothesCount = 0, electronicsCount = 0,
                stationeryCount = 0, bookCount = 0;

        for (AbstractProduct prod : sorted) {
            if (prod instanceof StockProduct) {
                switch (((StockProduct) prod). getCategory()) {
                    case MERCH -> merchCount++;
                    case STATIONERY -> stationeryCount++;
                    case CLOTHES -> clothesCount++;
                    case BOOK -> bookCount++;
                    case ELECTRONICS -> electronicsCount++;
                }
            }
        }

        // id
        String headerId = this.id;
        String futureClosedId = null;
        if (printOkAtEnd && state != TicketState.CLOSE) {
            futureClosedId = updateTicketId();
            headerId = futureClosedId;
        }

        System.out.println(StaticMessages.TICKET_HEADER + headerId);

        // Conteo
        for (AbstractProduct prod : sorted) {
            // Si es un servicio, imprime sin precio
            if (prod instanceof Service) {
                System.out.  println("  " + prod + " | Price: ---");
                continue;
            }

            totalPrice += prod.  getPrice();
            double discountPct = calculateDiscount(prod, serviceCount, merchCount,
                    clothesCount, stationeryCount,
                    electronicsCount, bookCount,
                    companyDiscountRate);

            // Imprimir con o sin descuento
            if (discountPct > 0) {
                double moneySaved = prod. getPrice() * discountPct;
                totalDiscount += moneySaved;
                System.out.println(" " + prod + StaticMessages.  DISCOUNT_SUFFIX +
                        rounded(moneySaved));
            } else {
                System.out.println(prod);
            }
        }

        // Conteo
        boolean isOnlyServices = (this.type == TicketType. COMBINED ||
                this.type == TicketType.SERVICE) &&
                serviceCount > 0 &&
                serviceCount == productList.  size();

        if (! isOnlyServices) {
            System.out.println(StaticMessages.TOTAL_PRICE_LABEL + rounded(totalPrice));
            System.  out. println(StaticMessages. TOTAL_DISCOUNT_LABEL + rounded(totalDiscount));
            System.out.println(StaticMessages.FINAL_PRICE_LABEL +
                    rounded(totalPrice - totalDiscount));
        } else {
            System.out. println("------");
        }

        //Cerrar
        if (printOkAtEnd) {
            if (state != TicketState.CLOSE) {
                setState(TicketState.CLOSE);
                this.id = headerId;
            }
            System.out.println(StaticMessages.TICKET_PRINT_OK);
        }
    }

    /**
     * Calcula el descuento aplicable a un producto
     * Parametrizado con AppConfigurations para valores de descuento
     */
    private double calculateDiscount(AbstractProduct prod, int serviceCount,
                                     int merchCount, int clothesCount,
                                     int stationeryCount, int electronicsCount,
                                     int bookCount, double companyDiscountRate) {
        double discountPct = 0.0;

        // Si es ticket corporativo con servicios, aplicar descuento corporativo
        if ((this.type == TicketType.COMBINED || this.type == TicketType.SERVICE) &&
                serviceCount > 0) {
            return companyDiscountRate;
        }

        // Si no es StockProduct, no aplicar descuento de categoría
        if (!  (prod instanceof StockProduct)) {
            return 0.0;
        }


        Category cat = ((StockProduct) prod). getCategory();

        // Verificar si se aplica descuento para esta categoría
        boolean applies = switch (cat) {
            case MERCH -> merchCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case STATIONERY -> stationeryCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case CLOTHES -> clothesCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case BOOK -> bookCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case ELECTRONICS -> electronicsCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case EVENT -> false;
        };

        // Obtener el valor de descuento desde AppConfigurations
        if (applies) {
            discountPct = switch (cat) {
                case MERCH -> AppConfigurations.DISCOUNT_MERCH;
                case STATIONERY -> AppConfigurations.  DISCOUNT_STATIONERY;
                case CLOTHES -> AppConfigurations.DISCOUNT_CLOTHES;
                case BOOK -> AppConfigurations.  DISCOUNT_BOOK;
                case ELECTRONICS -> AppConfigurations.DISCOUNT_ELECTRONICS;
                default -> throw new IllegalStateException("Unexpected value: " + cat);
            };
        }

        return discountPct;
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