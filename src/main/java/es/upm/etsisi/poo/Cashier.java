package es.upm.etsisi.poo;
import java.util.ArrayList;

public class Cashier extends User {
    private final String cashId;
    private final ArrayList<Ticket> createdTickets;

    public Cashier(String cashId, String name, String dni, String email, ArrayList<Ticket> createdTickets) {
        super(name, email, dni);
        this.cashId = cashId;
        this.createdTickets = new ArrayList<Ticket>();
    }

    public String getCashId() {
        return cashId;
    }

    public ArrayList<Ticket> getCreatedTickets() {
        return createdTickets;
    }

    public void addTicket(Ticket ticket) {
        createdTickets.add(ticket);
    }

    public String toString() {
        return "{class:Cashier, dni:" + this.dni + ", name:" + this.name +
                ", email:" + this.email + ", cashId:" + this.cashId +
                ", ticketsCount:" + this.createdTickets.size() + "}";
    }
}
