package es.upm.etsisi.poo;

import java.util.ArrayList;
import java.util.Comparator;

public class ClientDatabase {
    private static final ArrayList<Client> clientList = new ArrayList<>();

    private ClientDatabase(){}

    public static Client getClientByDNI(String dni) {
        Client result = null;
        for (Client client: clientList) {
            if (client.getDni().equals(dni)) {
                result = client;        // No se detiene ya que se asume que no hay dos personas con el mismo DNI
            }
        }
        return result;
    }

    public static void add(String name, String dni, String email) {
        boolean dniError = false;
        boolean emailError = false;
        for (Client client: clientList) {
            if (client.getDni().equals(dni)) {
                dniError = true;
            }
            if (client.getEmail().equals(email)) {
                emailError = true;
            }
        }
        if (dniError) {
            System.out.println("ERROR: " + dni + " identifier is already in use");
        } else if (emailError) {
            System.out.println("ERROR: " + email + " e-mail is already in use");
        } else {
            clientList.add(new Client(name, dni, email, null));     // Tengo mis dudas
        }
    }

    public static void remove(String dni) {
        boolean eliminated = false;
        for (int i=0; i<clientList.size(); i++) {
            if (clientList.get(i).getDni().equals(dni)) {
                clientList.remove(i);
                eliminated = true;
            }
        }
        if (!eliminated) {
            System.out.println("ERROR: Client with identifier " + dni + " not found");
        }
    }

    public static void list() {
        ArrayList<Client> sortedClientList = clientList;
        sortedClientList.sort(Comparator.comparing(Client::getName));
        for (Client cliente: clientList) {
            if (cliente != null) {
                System.out.println(cliente);
            }
        }
    }
}
