package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.utils.AppConfigurations;
import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi. poo.utils.StaticMessages;
import java.time.LocalDateTime;
import java.time. format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Estrategia de impresión para tickets corporativos
 * Imprime productos Y servicios con descuentos corporativos
 */
public class CorporateTicketPrinter implements TicketPrinter {

    @Override
    public void print(Ticket ticket) {
        printTicketDetails(ticket, true);
    }

    @Override
    public void printPreview (Ticket ticket) {
        printTicketDetails(ticket, false);
    }

    @Override
    public String getStrategyName() {
        return "Corporate";
    }

    private void printTicketDetails(Ticket ticket, boolean close) {
        ArrayList<AbstractProduct> productList = ticket.getProductList();

        // ==================== CÁLCULO DE SERVICIOS ====================
        int serviceCount = 0;
        if (ticket.getType() == TicketType.COMBINED || ticket.getType() == TicketType.SERVICE) {
            for (AbstractProduct p : productList) {
                if (p instanceof Service) {
                    serviceCount++;
                }
            }
        }

        // Usar AppConfigurations para el descuento corporativo
        double companyDiscountRate = serviceCount * AppConfigurations.CORPORATE_SERVICE_DISCOUNT;

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
        if (close && ticket.getState() != TicketState.CLOSE) {
            ticket.setId(updateTicketId(ticket.getId()));
        }

        System.out.println(StaticMessages.TICKET_HEADER + ticket.getId());

        // ==================== IMPRESIÓN DE PRODUCTOS ====================
        for (AbstractProduct prod : sorted) {
            // Si es un servicio, imprime sin precio
            if (prod instanceof Service) {
                System.out.println("  " + prod + " | Price: ---");
                continue;
            }

            totalPrice += prod.getPrice();
            double discountPct = calculateDiscount(ticket,prod, serviceCount, merchCount,
                    clothesCount, stationeryCount,
                    electronicsCount, bookCount,
                    companyDiscountRate);

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
        boolean isOnlyServices = (ticket.getType() == TicketType.COMBINED ||
                ticket.getType() == TicketType.SERVICE) &&
                serviceCount > 0 &&
                serviceCount == productList.size();

        if (! isOnlyServices) {
            System.out.println(StaticMessages.TOTAL_PRICE_LABEL + rounded(totalPrice));
            System.out.println(StaticMessages.TOTAL_DISCOUNT_LABEL + rounded(totalDiscount));
            System.out.println(StaticMessages. FINAL_PRICE_LABEL +
                    rounded(totalPrice - totalDiscount));
        } else {
            System.out. println("------");
        }

        // ==================== CIERRE DEL TICKET ====================
        if (close) {
            if (ticket.getState() != TicketState.CLOSE) {
                ticket.setState(TicketState.CLOSE);
            }
            System.out.println(StaticMessages.TICKET_PRINT_OK);
        }
    }

    /**
     * Calcula el descuento aplicable a un producto
     * Parametrizado con AppConfigurations para valores de descuento
     */
    private double calculateDiscount(Ticket ticket,AbstractProduct prod, int serviceCount,
                                     int merchCount, int clothesCount,
                                     int stationeryCount, int electronicsCount,
                                     int bookCount, double companyDiscountRate) {

        double discountPct = 0.0;

        // Si es ticket corporativo con servicios, aplicar descuento corporativo
        if ((ticket.getType() == TicketType.COMBINED || ticket.getType() == TicketType.SERVICE) &&
                serviceCount > 0) {
            return companyDiscountRate;
        }

        // Si no es StockProduct, no aplicar descuento de categoría
        if (!(prod instanceof StockProduct)) {
            return 0.0;
        }

        Category cat = ((StockProduct) prod).getCategory();

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
                case STATIONERY -> AppConfigurations.DISCOUNT_STATIONERY;
                case CLOTHES -> AppConfigurations. DISCOUNT_CLOTHES;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(formatter);
        return (originalId + formattedDate);
    }
}
