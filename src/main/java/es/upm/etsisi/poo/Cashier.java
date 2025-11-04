package es.upm.etsisi.poo;
import java.util.ArrayList;

public class Cashier extends User {
    private final String UPMWorkerID;
    private final ArrayList<Ticket> createdTickets;

    public Cashier(String UPMWorkerID, String name, String dni, String email, ArrayList<Ticket> createdTickets) {
        super(name, email);
        this.UPMWorkerID = UPMWorkerID;
        this.createdTickets = new ArrayList<Ticket>();
    }

    public String getUPMWorkerID() {
        return UPMWorkerID;
    }

    public ArrayList<Ticket> getCreatedTickets() {
        return createdTickets;
    }

    public void addTicket(Ticket ticket) {
        createdTickets.add(ticket);
    }

    public String toString() {
        return "{class:Cashier, name:" + this.name +
                ", email:" + this.email + ", UPMWorkerId:" + this.UPMWorkerID +
                ", ticketsCount:" + this.createdTickets.size() + "}";
    }
}
