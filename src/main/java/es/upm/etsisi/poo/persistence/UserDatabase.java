package es.upm.etsisi.poo.persistence;

import es.upm.etsisi.poo.Command.AppConfigurations;
import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.model.users.*;
import es.upm.etsisi.poo.model.sales.Ticket;
import es.upm.etsisi.poo.utils.StaticMessages;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class UserDatabase {

    private static UserDatabase instance; // Singleton
    private static ArrayList<User> users = new ArrayList<>();; // Unica Database

    private UserDatabase() {}

    public static UserDatabase getInstance() { //Acceso singleton
        if (instance == null) {
            instance = new UserDatabase();
        }
        return instance;
    }

    public static void setUsers(List<User> loadedUsers) {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.clear(); // Vaciamos lo que hubiera (seguridad)
        if (loadedUsers != null) {
            users.addAll(loadedUsers); // Añadimos lo cargado
        }
    }

    // Metodo auxiliar necesario para StoreData (Getter estático)
    public static ArrayList<User> getUsers() {
        return users;
    }

    public void add(User user) throws StoreException {
        if (getById(user.getIdentifier()) != null) {
            throw new StoreException(String.format(StaticMessages.USER_ALREADY_EXISTS, user.getIdentifier()));
        }
        users.add(user);
        System.out.println(user); // Usa el toString del cliente

        if (user instanceof Client) {
            System.out.println(StaticMessages.CLIENT_ADD_OK);
        } else if (user instanceof Cashier) {
            System.out.println(StaticMessages.CASH_ADD_OK);
        }
    }

    public void remove(String id) throws StoreException {
        User user = getById(id);
        if (user == null) {
            throw new StoreException(String.format(StaticMessages.USER_NOT_FOUND, id));
        }
        boolean isClient = user instanceof Client;
        users.remove(user);

        if (isClient) {
            System.out.println(StaticMessages.CLIENT_REMOVE_OK);
        } else {
            System.out.println(StaticMessages.CASH_REMOVE_OK);
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
        return result;
    }



    public void listClients() {
        System.out.println(StaticMessages.CLIENT_HEADER);
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

    public void listCashiers() {
        System.out.println(StaticMessages.CASH_HEADER);

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


    public String generateCashId() {
        String upmWorkerID;
        boolean exists;
        do {
            // Usa AppConfig en lugar de la generación de numeros aleatorios
            upmWorkerID = AppConfigurations.CASHIER_ID_PREFIX;
            for (int i = 0; i < AppConfigurations.CASHIER_ID_DIGITS; i++) {
                int random = (int) (Math.random() * 10);
                upmWorkerID += random;
            }
            exists = (getById(upmWorkerID) != null);

        } while (exists);

        return upmWorkerID;
    }


    public void showCashierTickets(String cashierId) throws StoreException {
        Cashier cashier = getById(cashierId, Cashier.class); // Devolver si encuentra y es Cashier
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }
        System.out.println(StaticMessages.TICKETS_HEADER);

        ArrayList<Ticket> tickets = cashier.getCreatedTickets();
        if (tickets != null) {
            tickets.sort(Comparator.comparing(Ticket::getId));
            for (Ticket t : tickets) {
                System.out.println("  " + t.getId() + "->" + t.getState());
            }
        }
        System.out.println(StaticMessages.CASH_TICKETS_OK);
    }
}
