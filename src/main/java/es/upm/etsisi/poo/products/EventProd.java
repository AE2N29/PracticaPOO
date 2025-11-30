package es.upm.etsisi.poo.products;
import java.time.LocalDateTime;

public abstract class EventProd extends AbstractProduct {
    protected static final int MAX_PEOPLE = 100;
    protected int personNumber;
    protected LocalDateTime expirationDate;
    protected double pricePerPerson;

    public EventProd(String id, String name, LocalDateTime expirationDate, double pricePerPerson, int personNumber) {
        super(id, name);

        if(personNumber > MAX_PEOPLE) {
            throw new IllegalArgumentException("personNumber > " + MAX_PEOPLE);
        }

        this.expirationDate = expirationDate;
        this.pricePerPerson = pricePerPerson;
        this.personNumber = personNumber;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }
    public double getPricePerPerson() {
        return pricePerPerson;
    }

    @Override
    public boolean setPrice(double price) {
        if (price > 0) {
            this.pricePerPerson = price;
            return true;
        }
        return false;
    }
    public int getPersonNumber() {
        return personNumber;
    }

    @Override
    public boolean availability() {
        return LocalDateTime.now().isBefore(expirationDate);
    }

    @Override
    public double calculatePrice() {
        return pricePerPerson*personNumber;
    }
}
