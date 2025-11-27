package es.upm.etsisi.poo.products;

import java.time.LocalDateTime;

public class EventReunion extends EventProd {

    public EventReunion(String id, String name, LocalDateTime eventTime, double pricePerPerson, int personNumber) {
        super(id, name, eventTime, pricePerPerson, personNumber);

        if (eventTime.isBefore(LocalDateTime.now().plusHours(12))) {  //la fecha debe de se antes de 12 hrs de antelacion para la "reserva"
            throw new IllegalArgumentException("Cualquier reunion necesita 12 horas de antelacion para crearse");
        }
    }

    public EventReunion(String name, LocalDateTime eventTime, double pricePerPerson, int personNumber) {
        super(AbstractProduct.generateID(), name, eventTime, pricePerPerson, personNumber);

        if (eventTime.isBefore(LocalDateTime.now().plusHours(12))) {  //la fecha debe de se antes de 12 hrs de antelacion para la "reserva"
            throw new IllegalArgumentException("Cualquier reunion necesita 12 horas de antelacion para crearse");
        }
    }
}