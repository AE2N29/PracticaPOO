package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.utils.AppConfigurations;
import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi.poo.utils.StaticMessages;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    public void printPreview(Ticket ticket) {
        printTicketDetails(ticket, false);
    }

    @Override
    public String getStrategyName() {
        return "Corporate";
    }

    private void printTicketDetails(Ticket ticket, boolean close) {
        List<AbstractProduct> services = new ArrayList<>();
        List<AbstractProduct> products = new ArrayList<>();

        // Recorremos la lista original del ticket
        for (AbstractProduct p : ticket.getProductList()) {
            if (p instanceof Service) {
                services.add(p);
            } else {
                products.add(p);
            }
        }

        services.sort(Comparator.comparing(AbstractProduct::getName));
        products.sort(Comparator.comparing(AbstractProduct::getName));
        int serviceCount = services.size();

        int merchCount = 0, clothesCount = 0, electronicsCount = 0,
                stationeryCount = 0, bookCount = 0;

        for (AbstractProduct p : products) {
            // Solo contamos si es StockProduct (ignoramos Eventos/Reuniones para estos descuentos)
            if (p instanceof StockProduct) {
                StockProduct sp = (StockProduct) p;
                switch (sp.getCategory()) {
                    case MERCH -> merchCount++;
                    case STATIONERY -> stationeryCount++;
                    case CLOTHES -> clothesCount++;
                    case BOOK -> bookCount++;
                    case ELECTRONICS -> electronicsCount++;
                }
            }
        }

        double companyDiscountRate = serviceCount * AppConfigurations.CORPORATE_SERVICE_DISCOUNT;

        if (close && ticket.getState() != TicketState.CLOSE) {
            ticket.setId(updateTicketId(ticket.getId()));
        }

        System.out.println(StaticMessages.TICKET_HEADER + ticket.getId());
        // ==================== IMPRESIÓN SERVICIOS ====================
        if (!services.isEmpty()) {
            System.out.println("Services Included:");
            for (AbstractProduct s : services) {
                System.out.println("  " + s + " | Price: ---");
            }
        }
        // ==================== IMPRESIÓN PRODUCTOS ====================
        double totalPrice = 0.0;
        double totalDiscount = 0.0;
        String lastId = "";
        if (!products.isEmpty()) {
            System.out.println("Product Included");
            for (AbstractProduct p : products) {
                totalPrice += p.getPrice();

                double discountPct = calculateDiscount(ticket, p, serviceCount,
                        merchCount, clothesCount, stationeryCount,
                        electronicsCount, bookCount, companyDiscountRate);
                if (discountPct > 0) {
                    totalDiscount += (p.getPrice() * discountPct);
                }
                if (!p.getId().equals(lastId)) {
                    System.out.println("  " + p);
                    lastId = p.getId();
                }
            }
        }

        double finalPrice = totalPrice - totalDiscount;

        // ==================== RESUMEN ====================
        boolean isOnlyServices = (ticket.getType() == TicketType.COMBINED || ticket.getType() == TicketType.SERVICE)
                && serviceCount > 0 && products.isEmpty();

        if (!isOnlyServices) {
            System.out.println(StaticMessages.TOTAL_PRICE_LABEL + rounded(totalPrice));
            if (totalDiscount > 0) {
                if (serviceCount > 0) {
                    System.out.println("  Extra Discount from services:" + rounded(totalDiscount));
                }
                System.out.println(StaticMessages.TOTAL_DISCOUNT_LABEL + rounded(totalDiscount));
            } else {
                System.out.println(StaticMessages.TOTAL_DISCOUNT_LABEL + "0.0");
            }
            System.out.println(StaticMessages.FINAL_PRICE_LABEL + rounded(finalPrice));
        } else {
            System.out.println("------");
        }

        // ==================== CIERRE ====================
        if (close) {
            if (ticket.getState() != TicketState.CLOSE) {
                ticket.setState(TicketState.CLOSE);
            }
            System.out.println(StaticMessages.TICKET_PRINT_OK);
        }
    }

    // MÉTODOS AUXILIARES (
    private String rounded(double d) {
        double val = Math.round(d * 1000.0) / 1000.0;
        return String.valueOf(val);
    }

    private String updateTicketId(String originalId) {
        String pattern = "-YY-MM-dd-HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(formatter);
        return (originalId + formattedDate);
    }

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
}