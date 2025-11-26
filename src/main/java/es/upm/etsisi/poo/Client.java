package es.upm.etsisi.poo;

import java.util.ArrayList;

public class Client extends User {
    private String dni;
    private Cashier associatedCashier;
    private final ArrayList<Ticket> createdTickets;

    public Client(String name, String dni, String email, Cashier associatedCashier) {
        super(name, email);
        this.dni = dni;
        this.associatedCashier = associatedCashier;
        this.createdTickets = new ArrayList<Ticket>();
    }

    public String getDni() {
        return dni;
    }

    public Cashier getAssociatedCashier() {
        return associatedCashier;
    }

    public void setAssociatedCashier(Cashier attendant) {
        this.associatedCashier = attendant;
    }

    public ArrayList<Ticket> getCreatedTickets() {
        return createdTickets;
    }

    public void addTicket(Ticket ticket) {
        createdTickets.add(ticket);
    }

    @Override
    public String toString() {
        return "Client{identifier='" + this.dni + "', name='" + this.name +
                "', email='" + this.email + "', cash=" + this.associatedCashier.getUPMWorkerID() + "}";
    }
}