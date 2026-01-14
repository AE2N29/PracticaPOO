package es.upm.etsisi.poo.model.users;

import es.upm.etsisi.poo.model.sales.Ticket;

import java.util.ArrayList;

public abstract class Client extends User {
    private final String id;
    private final Cashier associatedCashier; // relacion de asociacion
    private final ArrayList<Ticket> clientTickets; // relacion de agregacion

    public Client(String name, String id, String email, Cashier associatedCashier) {
        super(name, email);
        this.id = id;
        this.associatedCashier = associatedCashier;
        this.clientTickets = new ArrayList<>();
    }

    public Cashier getAssociatedCashier() {
        return associatedCashier;
    }

    public void addTicket(Ticket ticket) {
        this.clientTickets.add(ticket);
    }

    public ArrayList<Ticket> getClientTickets() {
        return clientTickets;
    }

    protected abstract String getHeader();

    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public String toString() {
        return getHeader() + "{identifier='" + this.id + "', name='" + this.name +
                "', email='" + this.email + "', cash=" + this.associatedCashier.getIdentifier() + "}";
    }
}