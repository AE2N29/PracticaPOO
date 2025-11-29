package es.upm.etsisi.poo.products;

import java.time.LocalDateTime;

public class EventReunion extends EventProd {

    private static void validateTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(12))) {
            throw new IllegalArgumentException("Cualquier reunion necesita 12 horas de antelacion para crearse");
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
        // En el enunciado el tipo se llama 'Meeting' en el output, no 'EventReunion'
        // Truco: Usamos "Meeting" hardcodeado para cumplir con el output exacto
        return "{class:Meeting, id:" + this.id +
                ", name:'" + this.name + "'" +
                ", price:" + String.format("%.1f", 0.0) +
                ", date of Event:" + this.expirationDate.toLocalDate() +
                ", max people allowed:" + this.maxPeopleAllowed + "}";
    }
}