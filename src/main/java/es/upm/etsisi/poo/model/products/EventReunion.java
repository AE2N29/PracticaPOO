package es.upm.etsisi.poo.model.products;

import java.time.LocalDateTime;

public class EventReunion extends EventProd {

    private static void validateTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(12))) {
            throw new IllegalArgumentException("A meeting must be created at least 12 hours in advance of its scheduled time.");
        }
    }

    public EventReunion(String id, String name, LocalDateTime eventTime, double pricePerPerson, int maxPeopleAllowed) {
        super(id, name, eventTime, pricePerPerson, maxPeopleAllowed);
        validateTime(eventTime);
    }

    public EventReunion(String name, LocalDateTime eventTime, double pricePerPerson, int maxPeopleAllowed) {
        super(AbstractProduct.generateID(), name, eventTime, pricePerPerson, maxPeopleAllowed);
        validateTime(eventTime);
    }

    @Override
    public String toString() {
        return "{class:Meeting, id:" + getId() +
                ", name:'" + getName() + "'" +
                ", price:" + String.format("%.1f", 0.0) +
                ", date of Event:" + this.expirationDate.toLocalDate() +
                ", max people allowed:" + this.maxPeopleAllowed + "}";
    }
}