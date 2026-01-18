package es.upm.etsisi.poo.model.sales;

public interface TicketPrinter {
    void print(Ticket ticket);
    void printPreview(Ticket ticket);
    String getStrategyName();
}
