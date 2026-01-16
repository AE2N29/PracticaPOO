package es.upm.etsisi.poo.persistence;

import es.upm.etsisi.poo.model.products.AbstractProduct;
import es.upm.etsisi.poo.model.sales.Ticket;
import es.upm.etsisi.poo.model.users.User;
import java.io.Serializable;
import java.util.List;

public class StoreData implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<User> users;
    private List<AbstractProduct> products;
    private List<Ticket> tickets;

    public StoreData(List<User> users, List<AbstractProduct> products, List<Ticket> tickets) {
        this.users = users;
        this.products = products;
        this.tickets = tickets;
    }

    public List<User> getUsers() { return users; }
    public List<AbstractProduct> getProducts() { return products; }
    public List<Ticket> getTickets() { return tickets; }
}