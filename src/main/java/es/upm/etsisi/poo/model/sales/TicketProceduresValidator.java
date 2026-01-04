package es.upm.etsisi.poo.model.sales;


import es.upm.etsisi.poo.model.products.AbstractProduct;

//CLASE Q SEA LLAMADA LUEGO EN EL MAIN PARA PERMITIR O NO OPERACIONES VALIDAS DE TICKET

//Clase q haciendo uso del enum de tipos de tickets, decide si en x ticket se puede hacer la operacion de agregar o no;
// x producto, en el caso de q el ticket no sea CORPORATE no deja agregar servicios
public class TicketProceduresValidator {

    //No puedes agregar servicios a tickets q no sean de empresa por ejemplo
    public boolean canAdd(AbstractProduct abstractProduct, OldTicket ticket)
    {

    }
}
