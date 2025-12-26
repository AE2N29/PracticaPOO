package es.upm.etsisi.poo.persistence;

import es.upm.etsisi.poo.model.users.*;
import es.upm.etsisi.poo.model.sales.Ticket;

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
            System.out.println("Error: User with ID " + user.getIdentifier() + " already exists.");
            return;
        }
        this.users.add(user);

        if (user instanceof Client) {
            System.out.println(user); // Usa el toString del cliente
            System.out.println("client add: ok");
        } else if (user instanceof Cashier) {
            System.out.println(user);
            System.out.println("cash add: ok");
        }
    }

    public void remove(String id) {
        User user = getById(id);
        if (user != null) {
            boolean isClient = user instanceof Client;

            this.users.remove(user);

            if (isClient) { System.out.println("client remove: ok");
            } else { System.out.println("cash remove: ok"); }

        } else {
            System.out.println("ERROR: User with identifier " + id + " not found");
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
        System.out.println("Client:");
        ArrayList<Client> clients = getAll(Client.class);

        clients.sort(Comparator.comparing(User::getName));

        if (clients.isEmpty()) {
            System.out.println("ERROR: Client list is empty");
        }

        for (Client c : clients) {
            System.out.println("  " + c);
        }
        System.out.println("client list: ok");
    }

    // METODOS ESPECIFICOS DE CAJEROS

    public void listCashiers() {
        System.out.println("Cash:");

        ArrayList<Cashier> cashiers = getAll(Cashier.class);
        cashiers.sort(Comparator.comparing(User::getName));

        if (cashiers.isEmpty()) {
            System.out.println("ERROR: Cashier list is empty");
        }

        for (Cashier c : cashiers) {
            System.out.println("  " + c);
        }
        System.out.println("cash list: ok");
    }


    public String generateCashId() { // Uso en StoreApp
        String upmWorkerID;
        boolean exists;
        do {
            StringBuilder sb = new StringBuilder("UW");
            for (int i = 0; i < 7; i++) {
                int random = (int) (Math.random() * 10);
                sb.append(random);
            }
            upmWorkerID = sb.toString();

            // Reutilizamos getById para ver si existe
            exists = (getById(upmWorkerID) != null);

        } while (exists);
        return upmWorkerID;
    }


    public void showCashierTickets(String cashierId) {
        Cashier cashier = getById(cashierId, Cashier.class); // Devolver si encuentra y es Cashier
        System.out.println("Tickets: ");

        if (cashier != null) {
            ArrayList<Ticket> tickets = cashier.getCreatedTickets();
            tickets.sort(Comparator.comparing(Ticket::getId));

            for (Ticket t : tickets) {
                System.out.println("  " + t.getId() + "->" + t.getState());
            }
        }
        System.out.println("cash tickets: ok");
    }
}
