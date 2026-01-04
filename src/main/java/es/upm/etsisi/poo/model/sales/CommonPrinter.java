package es.upm.etsisi.poo.model.sales;

import es.upm.etsisi.poo.model.users.IndividualClient;

public class CommonPrinter implements TicketPrinter<IndividualClient> {


    /*
        FORMATO ESPERADO;

            Ticket : <ID>
              {Producto 1} **discount -<dto_volumen>
              {Producto 2} **discount -<dto_volumen>
              Total price: <Suma Precio Original>
              Total discount: <Suma Descuentos>
              Final Price: <Resta>

     */
    @Override
    public String printTicket(ticketparametrizado) {
        return "";
    }

    @Override
    public boolean canClose(ticketparametrizado) {
        // Un ticket común puede cerrarse si tiene al menos un producto
        return ticket.getState() != TicketState.EMPTY;
    }
}

