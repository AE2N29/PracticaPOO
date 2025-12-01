package es.upm.etsisi.poo.model.users;

import es.upm.etsisi.poo.model.sales.Ticket;

import java.util.ArrayList;

public class Client extends User {
    private final String dni;
    private final Cashier associatedCashier; // relacion de asociacion
    private final ArrayList<Ticket> clientTickets; // relacion de agregacion

    public Client(String name, String dni, String email, Cashier associatedCashier) {
        super(name, email);
        this.dni = dni;
        this.associatedCashier = associatedCashier;
        this.clientTickets = new ArrayList<>();
    }

    public String getDni() {
        return dni;
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

    @Override
    public String toString() {
        return "Client{identifier='" + this.dni + "', name='" + this.name +
                "', email='" + this.email + "', cash=" + this.associatedCashier.getUPMWorkerID() + "}";
    }
}