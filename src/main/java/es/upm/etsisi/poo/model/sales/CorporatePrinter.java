package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.model.users.CompanyClient;

public class CorporatePrinter implements TicketPrinter<CompanyClient> {



    /*
    FORMATO ESPERADO:

        Ticket : <ID>
        Services Included:
          {Servicio A}
          {Servicio B}
        Product Included
          {Producto 1}  <-- Sin texto de descuento aquí
          {Producto 2}
          Total price: <Suma Solo Productos>
          Extra Discount from services:<Calculado> **discount -<Calculado>
          Total discount: <Mismo valor>
          Final Price: <Resta>

     */
    @Override
    public String printTicket(ticketparametrizado) {
        return "";
    }

    @Override
    public boolean canClose(ticketparametrizado) {
        // Un ticket de empresa puede cerrarse siempre, o solo cuando tenga productos y al menos un servicio???
        // Por ahora, puede cerrarse si no está vacío
        return ticket.getState() != TicketState.EMPTY;
    }
}

