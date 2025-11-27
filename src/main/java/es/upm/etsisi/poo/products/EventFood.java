package es.upm.etsisi.poo.products;

import java.time.LocalDateTime;

public class EventFood extends EventProd {

    public EventFood(String id, String name, LocalDateTime eventTime, double pricePerPerson, int MAX_PEOPLE, int personNumber) {
        super(id, name, eventTime, pricePerPerson, MAX_PEOPLE,  personNumber);

        if(eventTime.isBefore(LocalDateTime.now().plusDays(3))) {  //la fecha debe de se antes de 3 dias de antelacion para la "reserva"
            throw new IllegalArgumentException("Cualquier comida necesita 3 dias de antelacion para crearse");
        }
    }
}
