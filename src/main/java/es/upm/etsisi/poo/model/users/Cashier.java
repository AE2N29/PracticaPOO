package es.upm.etsisi.poo.model.users;

import es.upm.etsisi.poo.model.sales.OldTicket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;

@Entity
@Table(name = "Cashiers")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Cashier extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int numericCashierId;
    private final String UPMWorkerID;
    private final ArrayList<OldTicket> createdTickets; // relacion de composicion

    public Cashier(String UPMWorkerID, String name, String email) {
        super(name, email);
        this.UPMWorkerID = UPMWorkerID;
        this.createdTickets = new ArrayList<>();
    }

    public void addTicket(OldTicket ticket) {
        createdTickets.add(ticket);
    }

    public ArrayList<OldTicket> getCreatedTickets() {
        return createdTickets;
    }

    public OldTicket getTicketById(String id) {
        OldTicket result = null;
        for (OldTicket t : createdTickets) {
            if (t.getId().equalsIgnoreCase(id)) {
                result = t;
            }
        }
        return result;
    }

    public ArrayList<OldTicket> getTicketsSortedById() {
        ArrayList<OldTicket> sortedTickets = new ArrayList<>(this.createdTickets);
        sortedTickets.sort(Comparator.comparing(OldTicket::getId));
        return sortedTickets;
    }

    @Override
    public String getIdentifier() {
        return UPMWorkerID;
    }

    @Override
    public String toString() {
        return "Cash{identifier='" + this.UPMWorkerID + "', name='" + this.name + "', email='" + this.email + "'}";
    }
}
