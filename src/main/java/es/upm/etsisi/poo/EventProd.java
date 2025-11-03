package es.upm.etsisi.poo;

import java.time.LocalDateTime;

public class EventProd extends Product {
    protected LocalDateTime expirationDate;
    protected int maxPeople;
    protected double pricePerPerson;

    public EventProd(int id, String name, double price, LocalDateTime expirationDate, int maxPeople,
                     double pricePerPerson) {
        super(id, name, Category.EVENT, price);
        this.expirationDate = expirationDate;
        this.maxPeople = maxPeople;
        this.pricePerPerson = pricePerPerson;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getMaxPeople() {
        return maxPeople;
    }

    public double getPricePerPerson() {
        return pricePerPerson;
    }
}
