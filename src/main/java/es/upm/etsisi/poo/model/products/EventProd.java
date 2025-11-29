package es.upm.etsisi.poo.model.products;
import java.time.LocalDateTime;

public abstract class EventProd extends AbstractProduct {
    protected static final int MAX_PEOPLE = 100;
    protected int maxPeopleAllowed;
    protected LocalDateTime expirationDate;
    protected double pricePerPerson;

    public EventProd(String id, String name, LocalDateTime expirationDate, double pricePerPerson, int maxPeopleAllowed) {
        super(id, name);

        if(maxPeopleAllowed > MAX_PEOPLE) {
            throw new IllegalArgumentException("maxPeopleAllowed > " + MAX_PEOPLE);
        }

        this.expirationDate = expirationDate;
        this.pricePerPerson = pricePerPerson;
        this.maxPeopleAllowed = maxPeopleAllowed;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getMaxPeopleAllowed() {
        return maxPeopleAllowed;
    }



    @Override
    public double getUnitPrice() {
        return pricePerPerson;
    }

    @Override
    protected boolean availability() {
        return LocalDateTime.now().isBefore(expirationDate);
    }

    @Override
    public String toString() {
        // Implementación en EventProd para que EventFood y EventReunion lo hereden
        return "{class:" + this.getClass().getSimpleName() +
                ", id:" + this.id +
                ", name:'" + this.name + "'" +
                ", price:" + String.format("%.1f", 0.0) +
                ", date of Event:" + this.expirationDate.toLocalDate() +
                ", max people allowed:" + this.maxPeopleAllowed + "}";
    }
}