package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Comparator;

public class ClientDatabase {
    private static final ArrayList<Client> clientList = new ArrayList<>();

    private ClientDatabase(){}

    public static Client getClientByDNI(String dni) {
        for (Client client: clientList) {
            if (client.getDni().equals(dni)) {
                return client;
            }
        }
        return null;
    }

    public static void add(String name, String dni, String email, String cashID) {    // cashID se refiere a UPMWorkerID
        if (getClientByDNI(dni) != null) {
            System.out.println("ERROR: " + dni + " identifier is already in use");
            return;
        }
        for (Client client : clientList) {
            if (client.getEmail().equals(email)) {
                System.out.println("ERROR: " + email + " e-mail is already in use");
                return;
            }
        }
        Cashier associatedCashier = CashierDatabase.getCashierByUW(cashID);
        if (associatedCashier == null) {
            System.out.println("Error: Cashier with ID " + cashID + " not found");
            return;
        }
        Client newClient = new Client(name, dni, email, associatedCashier);
        clientList.add(newClient);
        System.out.println(newClient);
        System.out.println("client add: ok");
    }

    public static void remove(String dni) {
        Client clientToRemove = getClientByDNI(dni);
        if (clientToRemove != null) {
            clientList.remove(clientToRemove);
            System.out.println("client remove: ok");
        } else {
            System.out.println("ERROR: Client with identifier " + dni + " not found");
        }
    }

    public static void list() {
        System.out.println("Client:");
        ArrayList<Client> sortedClientList = new ArrayList<>(clientList);
        sortedClientList.sort(Comparator.comparing(Client::getName));     // Ordena la lista usando el nombre del cliente como criterio de comparación.

        for (Client cliente : sortedClientList) {
            System.out.println("  " + cliente);
        }
        System.out.println("client list: ok");
    }
}
