package es.upm.etsisi.poo.persistence;

import es.upm.etsisi.poo.model.users.*;
import es.upm.etsisi.poo.model.sales.Ticket;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.util.ArrayList;
import java.util.Comparator;

public class UserDatabase {

    private static UserDatabase instance; // Singleton
    private final ArrayList<User> users; // Unica Database

    private UserDatabase() {
        this.users = new ArrayList<>();
    }

    public static UserDatabase getInstance() { //Acceso singleton
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }


    public void add(User user) {
        if (getById(user.getIdentifier()) != null) {
            System.out.println(StaticMessages.USER_WITH_ID + user.getIdentifier() + StaticMessages.ALREADY_EXISTS);
            return;
        }
        this.users.add(user);

        if (user instanceof Client) {
            System.out.println(user); // Usa el toString del cliente
            System.out.println(StaticMessages.CLIENT_ADD_OK);
        } else if (user instanceof Cashier) {
            System.out.println(user);
            System.out.println(StaticMessages.CASH_ADD_OK);
        }
    }

    public void remove(String id) {
        User user = getById(id);
        if (user != null) {
            boolean isClient = user instanceof Client;

            this.users.remove(user);

            if (isClient) { System.out.println(StaticMessages.CLIENT_REMOVE_OK);
            } else { System.out.println(StaticMessages.CASH_REMOVE_OK); }

        } else {
            System.out.println(StaticMessages.USER_WITH_ID + id + StaticMessages.NOT_FOUND);
        }
    }

    public <T extends User> T getById(String id, Class<T> expectedType) { // devuelve objeto id en su respectiva clase(User o derivado)
        for (User u : users) {
            if (u.getIdentifier().equalsIgnoreCase(id)) {
                if (expectedType.isInstance(u)) {
                    return expectedType.cast(u);
                }
            }
        }
        return null;
    }

    public User getById(String id) { // Busca cualquier usuario sin importar si es Client o Cashier de manera simple
        return getById(id, User.class);
    }


    public <T extends User> ArrayList<T> getAll(Class<T> type) {
        ArrayList<T> result = new ArrayList<>();
        for (User u : users) {
            if (type.isInstance(u)) {
                result.add(type.cast(u));
            }
        }
        return result; // Devuelve la lista cruda
    }

    // METODOS ESPECIFICOS DE CLIENTES

    public void listClients() {
        System.out.println(StaticMessages.CLIENT);
        ArrayList<Client> clients = getAll(Client.class);

        clients.sort(Comparator.comparing(User::getName));

        if (clients.isEmpty()) {
            System.out.println(StaticMessages.CLIENT_LIST_EMPTY);
        }

        for (Client c : clients) {
            System.out.println("  " + c);
        }
        System.out.println(StaticMessages.CLIENT_LIST_OK);
    }

    // METODOS ESPECIFICOS DE CAJEROS

    public void listCashiers() {
        System.out.println(StaticMessages.CASH);

        ArrayList<Cashier> cashiers = getAll(Cashier.class);
        cashiers.sort(Comparator.comparing(User::getName));

        if (cashiers.isEmpty()) {
            System.out.println(StaticMessages.CASHIER_LIST_EMPTY);
        }

        for (Cashier c : cashiers) {
            System.out.println("  " + c);
        }
        System.out.println(StaticMessages.CASH_LIST_OK);
    }


    public String generateCashId() { // Uso en StoreApp
        String upmWorkerID;
        boolean exists;
        do {
            upmWorkerID = "UW";
            for (int i = 0; i < 7; i++) {
                int random = (int) (Math.random() * 10);
                upmWorkerID += random;
            }
            exists = (getById(upmWorkerID) != null);

        } while (exists);

        return upmWorkerID;
    }


    public void showCashierTickets(String cashierId) {
        Cashier cashier = getById(cashierId, Cashier.class); // Devolver si encuentra y es Cashier
        System.out.println(StaticMessages.TICKETS);

        if (cashier != null) {
            ArrayList<Ticket> tickets = cashier.getCreatedTickets();
            tickets.sort(Comparator.comparing(Ticket::getId));

            for (Ticket t : tickets) {
                System.out.println("  " + t.getId() + "->" + t.getState());
            }
        }
        System.out.println(StaticMessages.CASH_TICKETS_OK);
    }
}
