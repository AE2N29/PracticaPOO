package es.upm.etsisi.poo.model.products;

import java.time.LocalDateTime;

public class EventFood extends EventProd {

    private static void validateTime(LocalDateTime eventTime) {
        if(eventTime.isBefore(LocalDateTime.now().plusDays(3))) {
            throw new IllegalArgumentException("A food must be created at least 3 days in advance of its scheduled time.");
        }
    }

    public EventFood(String id, String name, LocalDateTime eventTime, double pricePerPerson, int maxPeopleAllowed) {
        super(id, name, eventTime, pricePerPerson, maxPeopleAllowed);
        validateTime(eventTime);
    }

    public EventFood(String name, LocalDateTime eventTime, double pricePerPerson, int maxPeopleAllowed) {
        super(AbstractProduct.generateID(), name, eventTime, pricePerPerson, maxPeopleAllowed);
        validateTime(eventTime);
    }

}
