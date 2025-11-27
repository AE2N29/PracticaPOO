package es.upm.etsisi.poo;

import java.util.ArrayList;

public class Cashier extends User {
    private final String UPMWorkerID;
    private final ArrayList<Ticket> createdTickets; // relacion de composicion

    public Cashier(String UPMWorkerID, String name, String email) {
        super(name, email);
        this.UPMWorkerID = UPMWorkerID;
        this.createdTickets = new ArrayList<>();
    }

    public String getUPMWorkerID() {
        return UPMWorkerID;
    }

    public void addTicket(Ticket ticket) {
        createdTickets.add(ticket);
    }

    public ArrayList<Ticket> getCreatedTickets() {
        return createdTickets;
    }

    @Override
    public String toString() {
        return "Cash{identifier='" + this.UPMWorkerID + "', name='" + this.name + "', email='" + this.email + "'}";
    }
}
