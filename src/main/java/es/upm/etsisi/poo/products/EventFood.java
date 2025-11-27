package es.upm.etsisi.poo.products;

import java.time.LocalDateTime;

public class EventFood extends EventProd {

    public EventFood(String id, String name, LocalDateTime eventTime, double pricePerPerson, int personNumber) {
        super(id, name, eventTime, pricePerPerson, personNumber);

        if(eventTime.isBefore(LocalDateTime.now().plusDays(3))) {  //la fecha debe de se antes de 3 dias de antelacion para la "reserva"
            throw new IllegalArgumentException("Cualquier comida necesita 3 dias de antelacion para crearse");
        }
    }

    public EventFood(String name, LocalDateTime eventTime, double pricePerPerson, int personNumber) {
        super(AbstractProduct.generateID(), name, eventTime, pricePerPerson, personNumber);
        if(eventTime.isBefore(LocalDateTime.now().plusDays(3))) {
            throw new IllegalArgumentException("Cualquier comida necesita 3 dias de antelacion para crearse");
        }
    }

}
