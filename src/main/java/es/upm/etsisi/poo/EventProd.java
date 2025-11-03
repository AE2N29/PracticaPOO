package es.upm.etsisi.poo;

import java.time.LocalDateTime;

public class EventProd extends Product {
    protected static final int MAX_PEOPLE = 100;
    protected LocalDateTime expirationDate;
    protected double pricePerPerson;

    public EventProd(int id, String name, double price, LocalDateTime expirationDate, double pricePerPerson) {
        super(id, name, Category.EVENT, price);
        this.expirationDate = expirationDate;
        this.pricePerPerson = pricePerPerson;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public double getPricePerPerson() {
        return pricePerPerson;
    }
}
