package es.upm.etsisi.poo.products;

import java.time.LocalDateTime;

public class EventFood extends EventProd {

    private static void validateTime(LocalDateTime eventTime) {
        if(eventTime.isBefore(LocalDateTime.now().plusDays(3))) {
            throw new IllegalArgumentException("Cualquier comida necesita 3 dias de antelacion para crearse");
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
