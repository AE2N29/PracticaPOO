package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.model.users.Client;
import java.io.Serializable; // da permiso a java de achicar la info generada de aquí en bytes en el caso de cerrar, y luego abrir el programa

public interface TicketPrinter<T extends Client> extends Serializable {

    static String printTicket(ticketparametrizado);

    boolean canClose(Ticketparametrizado);
}
