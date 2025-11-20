package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Comparator;

public class CashierDatabase {
    private static final ArrayList<Cashier> cashiersList = new ArrayList<>();

    private CashierDatabase(){}

    public static Cashier getCashierByUW(String UPMWorker) {
        Cashier result = null;
        for (Cashier c: cashiersList) {
            if (c.getUPMWorkerID().equalsIgnoreCase(UPMWorker)) {
                result = c;
            }
        }
        return result;
    }

    public static void add(String name, String email) {

    }

    public static void remove(String UPMWorker) {
        boolean deleted = false;
        for (Cashier c: cashiersList) {
            if (c.getUPMWorkerID().equalsIgnoreCase(UPMWorker)) {
                cashiersList.remove(c);
                deleted = true;
            }
        }
        if (!deleted) {
            System.out.println("There isn't any cashier with UPMWorker id " + UPMWorker);
        }
    }

    public static void list() {
        for (Cashier c: cashiersList) {
            if (c != null) {
                System.out.println(c);
            }
        }
    }

    public static void tickets(String UPMWorker) {
        Cashier cashier = getCashierByUW(UPMWorker);
        ArrayList<Ticket> cashierTickets = cashier.getCreatedTickets();
        cashierTickets.sort(Comparator.comparing(Ticket::getId));
        for (Ticket t: cashierTickets) {
            System.out.println("{id: " + t.getId() + ", state: " + t.getState() + "}");
        }
    }
}
