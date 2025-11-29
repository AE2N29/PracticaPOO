package es.upm.etsisi.poo.persistance;

import es.upm.etsisi.poo.model.sales.Ticket;
import es.upm.etsisi.poo.model.users.Cashier;

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
        if (getCashierByUW(UPMWorkerID) != null) {
            System.out.println("Error: Cashier with ID " + UPMWorkerID + " already exists.");
            return;
        }
        Cashier newCashier = new Cashier(UPMWorkerID, name, email);
        cashiersList.add(newCashier);
        System.out.println(newCashier);
        System.out.println("cash add: ok");
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

        Cashier newCashier = new Cashier(UPMWorkerID, name, email);
        cashiersList.add(newCashier);
        System.out.println(newCashier);
        System.out.println("cash add: ok");
    }

    public static void remove(String UPMWorker) {
        boolean found = false;
        int i = 0;
        while (i < cashiersList.size() && !found) {
            if (cashiersList.get(i).getUPMWorkerID().equalsIgnoreCase(UPMWorker)) {
                cashiersList.remove(i);
                found = true;
            } else {
                i++;
            }
        }
        if (found) {
            System.out.println("cash remove: ok");
        } else {
            System.out.println("There isn't any cashier with UPMWorker id " + UPMWorker);
        }
    }

    public static void list() {
        if (cashiersList.isEmpty()) {
            System.out.println("ERROR: Cashiers list is empty");
        }
        System.out.println("Cash:");
        ArrayList<Cashier> sortedList = new ArrayList<>(cashiersList);
        sortedList.sort(Comparator.comparing(Cashier::getName));    // Ordena la lista usando el nombre del cajero como criterio de comparación.
        for (Cashier c: sortedList) {
            if (c != null) {
                System.out.println("  " + c);
            }
        }
    }

    public static void tickets(String UPMWorker) {
        Cashier cashier = null;
        for (Cashier c: cashiersList) {
            if (c.getUPMWorkerID().equalsIgnoreCase(UPMWorker)) {
                cashier = c;
            }
        }
        if (cashier == null) {
            System.out.println("There is no cashier with UPMWorkerID " + UPMWorker);
        } else {
            ArrayList<Ticket> cashierTickets = cashier.getCreatedTickets();
            cashierTickets.sort(Comparator.comparing(Ticket::getId));
            if (cashier.getCreatedTickets().isEmpty()) {
                System.out.println("ERROR: Cashier with UPMWorkerID " + UPMWorker + " doesn't have any ticket created yet");
            } else {
                System.out.println("Tickets: ");
                for (Ticket t: cashierTickets) {
                    System.out.println("  " + t.getId() + "->" + t.getState());
                }
            }
        }
        System.out.println("cash tickets: ok");
    }

    public static String generateCashId() {
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
        return UPMWorkerID;
    }
}
