package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.utils.AppConfigurations;
import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi.poo.utils.StaticMessages;
import java.time.LocalDateTime;
import java. time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util. Comparator;

/**
 * Estrategia de impresión para tickets individuales
 * Solo imprime productos, sin servicios ni descuentos corporativos
 */
public class IndividualTicketPrinter implements TicketPrinter {

    @Override
    public void print(Ticket ticket) {
        System.out.println(StaticMessages.INDIVIDUAL_TICKET_HEADER);
        printTicketDetails(ticket);
    }

    @Override
    public String getStrategyName() {
        return "Individual";
    }

    private void printTicketDetails(Ticket ticket) {
        ArrayList<AbstractProduct> productList = ticket.getProductList();

        // ==================== CONTEO DE PRODUCTOS ====================
        double totalPrice = 0.0, totalDiscount = 0.0;
        ArrayList<AbstractProduct> sorted = new ArrayList<>(productList);
        sorted.sort(Comparator.comparing(AbstractProduct::getName));

        int merchCount = 0, clothesCount = 0, electronicsCount = 0,
                stationeryCount = 0, bookCount = 0;

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

        // ==================== PREPARACIÓN DE ID ====================
        String headerId = ticket.getId();
        String futureClosedId = updateTicketId(ticket. getId());
        headerId = futureClosedId;

        System.out.println(StaticMessages.TICKET_HEADER + headerId);

        // ==================== IMPRESIÓN DE PRODUCTOS ====================
        for (AbstractProduct prod : sorted) {
            totalPrice += prod.getPrice();
            double discountPct = calculateDiscount(prod, merchCount, clothesCount,
                    stationeryCount, electronicsCount, bookCount);

            // Imprimir con o sin descuento
            if (discountPct > 0) {
                double moneySaved = prod.getPrice() * discountPct;
                totalDiscount += moneySaved;
                System.out.println(" " + prod + StaticMessages.DISCOUNT_SUFFIX +
                        rounded(moneySaved));
            } else {
                System.out.println(prod);
            }
        }

        // ==================== RESUMEN FINAL ====================
        System.out.println(StaticMessages. TOTAL_PRICE_LABEL + rounded(totalPrice));
        System.out.println(StaticMessages. TOTAL_DISCOUNT_LABEL + rounded(totalDiscount));
        System.out.println(StaticMessages.FINAL_PRICE_LABEL +
                rounded(totalPrice - totalDiscount));

        // ==================== CIERRE DEL TICKET ====================
        if (ticket.getState() != TicketState.CLOSE) {
            ticket.setState(TicketState. CLOSE);
            ticket.setId(headerId);
        }
        System.out.println(StaticMessages.TICKET_PRINT_OK);
    }

    /**
     * Calcula el descuento aplicable a un producto (solo por categoría)
     */
    private double calculateDiscount(AbstractProduct prod,
                                     int merchCount, int clothesCount,
                                     int stationeryCount, int electronicsCount, int bookCount) {

        double discountPct = 0.0;

        // Si no es StockProduct, no aplicar descuento
        if (!(prod instanceof StockProduct)) {
            return 0.0;
        }

        Category cat = ((StockProduct) prod).getCategory();

        // Verificar si se aplica descuento para esta categoría
        boolean applies = switch (cat) {
            case MERCH -> merchCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case STATIONERY -> stationeryCount >= AppConfigurations. MIN_UNITS_FOR_DISCOUNT;
            case CLOTHES -> clothesCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case BOOK -> bookCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case ELECTRONICS -> electronicsCount >= AppConfigurations.MIN_UNITS_FOR_DISCOUNT;
            case EVENT -> false;
        };

        // Obtener el valor de descuento desde AppConfigurations
        if (applies) {
            discountPct = switch (cat) {
                case MERCH -> AppConfigurations. DISCOUNT_MERCH;
                case STATIONERY -> AppConfigurations. DISCOUNT_STATIONERY;
                case CLOTHES -> AppConfigurations.DISCOUNT_CLOTHES;
                case BOOK -> AppConfigurations.DISCOUNT_BOOK;
                case ELECTRONICS -> AppConfigurations.DISCOUNT_ELECTRONICS;
                case EVENT -> 0.0;
            };
        }

        return discountPct;
    }

    private String rounded(double d) {
        double val = Math.round(d * 1000.0) / 1000.0;
        return String.valueOf(val);
    }

    private String updateTicketId(String originalId) {
        String pattern = "-YY-MM-dd-HH: mm";
        DateTimeFormatter formatter = DateTimeFormatter. ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(formatter);
        return (originalId + formattedDate);
    }
}