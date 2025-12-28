package es.upm.etsisi.poo.model.products;
import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.time.LocalDateTime;

public class Event extends AbstractProduct {
    protected static final int MAX_PEOPLE = 100;

    protected EventType eventType;
    protected int maxPeopleAllowed;
    protected LocalDateTime expirationDate;
    protected double pricePerPerson;

    public Event(String id, String name, LocalDateTime expirationDate, double pricePerPerson, int maxPeopleAllowed, EventType eventType) throws StoreException {
        super(id, name);
        if(maxPeopleAllowed > MAX_PEOPLE) {
            throw new IllegalArgumentException(StaticMessages.ADD_PROD_ERROR);
        }
        validateEventTimeByType(expirationDate, eventType);
        this.eventType = eventType;
        this.expirationDate = expirationDate;
        this.pricePerPerson = pricePerPerson;
        this.maxPeopleAllowed = maxPeopleAllowed;
    }

    //Constructor sin ID
    public Event( String name, LocalDateTime expirationDate, double pricePerPerson, int maxPeopleAllowed, EventType eventType) throws StoreException {
        this(AbstractProduct.generateID(), name, expirationDate, pricePerPerson, maxPeopleAllowed, eventType);
    }

    private void validateEventTimeByType(LocalDateTime eventTime, EventType eventType) {
        switch(eventType) {
            case FOOD:
                if(eventTime.isBefore(LocalDateTime.now().plusDays(3))) {
                    throw new IllegalArgumentException(StaticMessages.INVALID_FOOD_TIME);
                }
                break;
            case MEETING:
                if (eventTime.isBefore(LocalDateTime.now().plusHours(12))) {
                    throw new IllegalArgumentException(StaticMessages.INVALID_MEETING_TIME);
                }
                break;
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getMaxPeopleAllowed() {
        return maxPeopleAllowed;
    }

    @Override
    public double getPrice() {
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

    @Override
    public boolean isAvailable() {
        return LocalDateTime.now().isBefore(expirationDate);
    }

    @Override
    public String toString() {
        return "{class:" + getEventType() + ", id:" + getId() +
                ", name:'" + getName() + "'" +
                ", price:" + String.format("%.1f", 0.0) +
                ", date of Event:" + this.expirationDate.toLocalDate() +
                ", max people allowed:" + this.maxPeopleAllowed + "}";
    }
}