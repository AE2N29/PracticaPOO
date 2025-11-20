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

    public static void add(String UPMWorkerID, String name, String email) {
        if (cashiersList.contains(UPMWorkerID)) {
            System.out.println("Error: Cashier with ID " + UPMWorkerID + " already exists.");
            return;
        }
        cashiersList.add(new Cashier(UPMWorkerID, name, email));
    }

    public static void add(String name, String email) {
        String UPMWorkerID;
        boolean exists;
        do {
            UPMWorkerID = "UW";
            for (int i = 0; i < 7; i++) {
                int random = (int) (Math.random() * 10);
                UPMWorkerID += random;
            }
            exists = false;
            for (Cashier c : cashiersList) {
                if (c.getUPMWorkerID().equals(UPMWorkerID)) {
                    exists = true;
                }
            }
        } while (exists);

        cashiersList.add(new Cashier(UPMWorkerID, name, email));
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
