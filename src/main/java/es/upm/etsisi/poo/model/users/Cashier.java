package es.upm.etsisi.poo.model.users;

import es.upm.etsisi.poo.model.sales.Ticket;

import java.util.ArrayList;
import java.util.Comparator;

public class Cashier extends User {
    private final String UPMWorkerID;
    private final ArrayList<Ticket> createdTickets; // relacion de composicion

    public Cashier(String UPMWorkerID, String name, String email) {
        super(name, email);
        this.UPMWorkerID = UPMWorkerID;
        this.createdTickets = new ArrayList<Ticket>();
    }

    public void addTicket(Ticket ticket) {
        createdTickets.add(ticket);
    }

    public ArrayList<Ticket> getCreatedTickets() {
        return createdTickets;
    }

    public Ticket getTicketById(String id) {
        Ticket result = null;
        for (Ticket t : createdTickets) {
            if (t.getId().equalsIgnoreCase(id)) {
                result = t;
            }
        }
        return result;
    }

    public ArrayList<Ticket> getTicketsSortedById() {
        ArrayList<Ticket> sortedTickets = new ArrayList<>(this.createdTickets);
        sortedTickets.sort(Comparator.comparing(Ticket::getId));
        return sortedTickets;
    }

    @Override
    public String getIdentifier() {
        return UPMWorkerID;
    }

    @Override
    public String toString() {
        return "Cash{identifier='" + this.UPMWorkerID + "', name='" + this.name + "', email='" + this.email + "'}";
    }
}
