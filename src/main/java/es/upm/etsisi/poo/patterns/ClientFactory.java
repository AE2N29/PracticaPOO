package es.upm.etsisi.poo.patterns;

import es.upm.etsisi.poo.model.users.*;
import es.upm.etsisi.poo.utils.InputValidator;

public class ClientFactory {
    public static Client createClient(String name, String id, String email, Cashier cashier) {
        if (InputValidator.isNIF(id)) {
            return new CompanyClient(name, id, email, cashier);
        } else {
            return new IndividualClient(name, id, email, cashier);
        }
    }
}
