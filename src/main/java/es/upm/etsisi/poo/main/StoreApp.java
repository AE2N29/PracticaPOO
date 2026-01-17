package es.upm.etsisi.poo.main;

import es.upm.etsisi.poo.utils.AppConfigurations;
import es.upm.etsisi.poo.exceptions.StoreException;
import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi.poo.model.sales.Ticket;
import es.upm.etsisi.poo.model.users.*;
import es.upm.etsisi.poo.patterns.ClientFactory;
import es.upm.etsisi.poo.persistence.*;
import es.upm.etsisi.poo.utils.*;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;

public class StoreApp {
    private static Scanner sc = null;
    private static boolean fromKeyboard;

    public static void main(String[] args) {
        StoreApp app = new StoreApp();
        try {
            if (args.length == 0) {
                sc = new Scanner(System.in);
                fromKeyboard = true;
                app.init();
            } else if (args.length == 1) {
                String nombreArchivo = args[0];
                File archivo = new File(nombreArchivo);
                sc = new Scanner(archivo);
                fromKeyboard = false;
                app.init();
            } else {
                System.out.println(StaticMessages.ARGS_EXCEED);
                app.end();
            }
        } catch (FileNotFoundException e) {
            System.out.println(StaticMessages.FILE_NOT_FOUND);
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    private void init() {
        System.out.println(StaticMessages.WELCOME);
        start();
    }

    private void start() {

        StoreData loadedData = PersistenceManager.load();
        if (loadedData != null) {
            // Restaurar los datos en clases estáticas
            UserDatabase.setUsers(loadedData.getUsers());
            ProductCatalog.setProducts(loadedData.getProducts());
            // Recopilamos todos los tickets de todos los cajeros para avisar a la clase Ticket
            ArrayList<Ticket> allTickets = new ArrayList<>();

            for (User u : UserDatabase.getUsers()) {
                if (u instanceof Cashier) {
                    Cashier c = (Cashier) u;
                    if (c.getCreatedTickets() != null) {
                        allTickets.addAll(c.getCreatedTickets());
                    }
                }
            }
            Ticket.rebuildUsedIds(allTickets); // Restauramos la lista de IDs prohibidos (ya usados)
        }

        boolean keepGoing = true;
        while (keepGoing) {
            String command = typeCommand().trim();
            if (command.isEmpty()) continue;

            if (!InputValidator.validCommand(command)) {
                System.out.println(StaticMessages.NOT_VALID_CMD);
            } else {
                String[] commandParts = command.split(" ");
                try {
                    switch (commandParts[0].toUpperCase()) {
                        case "PROD":
                            prodCommands(commandParts, command);
                            break;
                        case "TICKET":
                            ticketCommands(commandParts, command);
                            break;
                        case "HELP":
                            help();
                            break;
                        case "ECHO":
                            echo(command);
                            break;
                        case "EXIT":
                            // Recopilar datos actuales
                            StoreData currentData = new StoreData(UserDatabase.getUsers(), ProductCatalog.getProducts(), new ArrayList<>());
                            PersistenceManager.save(currentData);
                            keepGoing = false;
                            end();
                            break;
                        case "CLIENT":
                            clientCommands(commandParts);
                            break;
                        case "CASH":
                            cashierCommands(commandParts);
                            break;
                        default:
                            System.out.println(StaticMessages.NOT_VALID_CMD);
                    }
                } catch (StoreException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println(StaticMessages.ERROR_PROCESSING + " -> " +
                            commandParts[0].toLowerCase() +
                            (commandParts.length > 1 ? " " + commandParts[1].toLowerCase() : "") +
                            " -> " + e.getMessage());
                }
                System.out.println();
            }
        }
        sc.close();
    }

    private void end() {
        System.out.println(StaticMessages.CLOSING_APP);
        System.out.println(StaticMessages.GOODBYE);
    }
    // ==================== COMANDOS DE PRODUCTO ====================

    public void prodCommands(String[] commands, String fullCommand) throws StoreException {
        String prodCommand = commands[1].toUpperCase();
        switch (prodCommand) {
            case "ADD":
                prodAdd(fullCommand);
                break;
            case "ADDFOOD":
                prodAddFood(commands);
                break;
            case "ADDMEETING":
                prodAddMeeting(commands);
                break;
            case "LIST":
                ProductCatalog.list();
                break;
            case "UPDATE":
                prodUpdate(commands, fullCommand);
                break;
            case "REMOVE":
                prodRemove(commands[2]);
                break;
            default:
                System.out.println(StaticMessages.INVALID_INPUT);
        }
    }

    private void prodAdd(String fullCommand) throws StoreException {
        String[] parts = fullCommand.trim().split(" ");
        boolean isService = false;
        String transportField = parts[parts.length - 1];
        for (ServiceTypes type: ServiceTypes.values()) {
            if (type.name().equalsIgnoreCase(transportField)) {
                isService = true;
            }
        }

        if (isService) {
            if (InputValidator.isDate(parts[2])) {
                Service service = new Service(ServiceTypes.valueOf(transportField), LocalDate.parse(parts[2]).atStartOfDay());
                ProductCatalog.add(service.getId(), service);
                return;
            }
        }

        String[] parsed = processProdAdd(fullCommand);

        // ← Cambio: En lugar de parsed[2]. equals("GENERATE") ?  ...
        String id;
        if (parsed[2].equals("GENERATE")) {
            id = AbstractProduct.generateID();
        } else {
            id = parsed[2];
        }

        String name = parsed[3];
        Category category = Category.valueOf(parsed[4].toUpperCase());
        double price = Double.parseDouble(parsed[5]);

        if (parsed.length == 7) {
            int maxPers = Integer.parseInt(parsed[6]);
            ProductCatalog.add(id, new PersonalizedProduct(id, name, category, price, maxPers));
        } else {
            ProductCatalog.add(id, new StockProduct(id, name, category, price));
        }
    }

    private void prodAddFood(String[] commands) throws StoreException {
        String fullCommand = String.join(" ", commands);
        String name = extractName(fullCommand);
        String beforeName = fullCommand.substring(0, fullCommand.indexOf('"')).trim();
        String afterName = fullCommand.substring(fullCommand.lastIndexOf('"') + 1).trim();
        String[] beforeParts = beforeName.split(" ");
        String[] afterParts = afterName.split(" ");

        LocalDate date = LocalDate.parse(afterParts[1]);
        LocalDateTime eventTime = date.atStartOfDay();
        double price = Double.parseDouble(afterParts[0]);
        int personCount = Integer.parseInt(afterParts[2]);

        String id;
        if ("GENERATE".equals(beforeParts[2])) {
            id = null;
        } else {
            id = beforeParts[2];
        }

        Event product;
        if (id != null) {
            product = new Event(id, name, eventTime, price, personCount, EventType.FOOD);
        } else {
            product = new Event(name, eventTime, price, personCount, EventType.FOOD);
        }
        ProductCatalog.add(product.getId(), product);
    }

    private void prodAddMeeting(String[] commands) throws StoreException {
        String fullCommand = String.join(" ", commands);
        String name = extractName(fullCommand);
        String beforeName = fullCommand.substring(0, fullCommand.indexOf('"')).trim();
        String afterName = fullCommand.substring(fullCommand.lastIndexOf('"') + 1).trim();
        String[] beforeParts = beforeName.split(" ");
        String[] afterParts = afterName.split(" ");

        LocalDate date = LocalDate.parse(afterParts[1]);
        LocalDateTime eventTime = date.atStartOfDay();
        double price = Double.parseDouble(afterParts[0]);
        int personCount = Integer.parseInt(afterParts[2]);
        String id;
        if ("GENERATE".equals(beforeParts[2])) {
            id = null;
        } else {
            id = beforeParts[2];
        }

        Event product;
        if (id != null) {
            product = new Event(id, name, eventTime, price, personCount, EventType.MEETING);
        } else {
            product = new Event(name, eventTime, price, personCount, EventType.MEETING);
        }
        ProductCatalog.add(product.getId(), product);
    }

    private void prodUpdate(String[] commands, String fullCommand) throws StoreException {
        String id = commands[2];
        String fieldToChange = commands[3].toUpperCase();

        String newValue;
        if ("NAME".equals(fieldToChange)) {
            newValue = extractName(fullCommand);
        } else {
            newValue = commands[4];
        }

        AbstractProduct product = ProductCatalog.getProduct(id);
        if (product == null) {
            throw new StoreException(StaticMessages.PROD_NO_EXIST);
        }

        switch (fieldToChange) {
            case "NAME":
                product.setName(newValue);
                ProductCatalog.update(id, product);
                break;
            case "PRICE":
                updatePrice(product, newValue, id);
                break;
            case "CATEGORY":
                updateCategory(product, newValue, id);
                break;
            default:
                System.out.println(StaticMessages.INVALID_INPUT);
        }
    }

    private void updatePrice(AbstractProduct product, String priceStr, String id) throws StoreException {
        try {
            double newPrice = Double.parseDouble(priceStr);
            if (newPrice <= 0) {
                throw new StoreException(StaticMessages.NEGATIVE_AMOUNT);
            }
            if (product.setPrice(newPrice)) {
                ProductCatalog.update(id, product);
            } else {
                System.out.println(StaticMessages.INVALID_INPUT);
            }
        } catch (NumberFormatException e) {
            throw new StoreException(StaticMessages.INVALID_INPUT);
        }
    }

    private void updateCategory(AbstractProduct product, String categoryStr, String id) throws StoreException {
        if (!product.hasCategory()) {
            System.out.println(StaticMessages.INVALID_INPUT);
            return;
        }
        try {
            Category newCategory = Category.valueOf(categoryStr.toUpperCase());
            if (product.setCategory(newCategory)) {
                ProductCatalog.update(id, product);
                System.out.println(product);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(StaticMessages.INVALID_INPUT);
        }
    }

    private void prodRemove(String id) throws StoreException {
        ProductCatalog.remove(id);
    }

    public void ticketCommands(String[] commands, String fullCommand) throws StoreException {
        switch (commands[1].toUpperCase()) {
            case "NEW":
                ticketNew(commands);
                break;
            case "ADD":
                ticketAdd(commands, fullCommand);
                break;
            case "REMOVE":
                ticketRemove(commands);
                break;
            case "PRINT":
                ticketPrint(commands);
                break;
            case "LIST":
                ticketList();
                break;
            default:
                throw new StoreException(StaticMessages.INVALID_INPUT);
        }
    }

    //Crea un nuevo ticket validando el tipo de cliente
    // IndividualClient: Solo puede crear tickets individuales (-p)
    //CorporateClient: Puede crear individuales, corporativos (-c) y servicios (-s)

    private void ticketNew(String[] commands) throws StoreException {
        String ticketId = null;
        String cashId, userId;
        String ticketType = "p"; // default:  personal

        // Parsear argumentos considerando el flag de tipo de ticket
        if (commands.length == 4) {
            cashId = commands[2];
            userId = commands[3];
        } else if (commands.length == 5) {
            // ← Cambio: En lugar de operador ternario
            if (commands[4].startsWith("-")) {
                ticketType = commands[4].substring(1); // Extrae 'c', 'p', 's'
                cashId = commands[2];
                userId = commands[3];
            } else {
                ticketId = commands[2];
                cashId = commands[3];
                userId = commands[4];
            }
        } else if (commands.length == 6) {
            ticketId = commands[2];
            cashId = commands[3];
            userId = commands[4];
            ticketType = commands[5].substring(1);
        } else {
            throw new StoreException("Invalid ticket new command format");
        }

        // Obtener el cliente
        Client client = UserDatabase.getInstance().getById(userId, Client.class);
        if (client == null) {
            throw new StoreException(StaticMessages.CLIENT_NOT_FOUND);
        }

        // Validar permisos:  Qué tipos de ticket puede crear cada cliente
        validateTicketTypeForClient(client, ticketType);

        // Obtener el cajero
        Cashier cashier = UserDatabase.getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        // Crear el ticket con el tipo y cliente apropiado
        Ticket newTicket;
        if (ticketId == null) {
            newTicket = new Ticket(client, ticketType);
        } else {
            if (Ticket.isIdRegistered(ticketId)) {
                throw new StoreException(StaticMessages.TICKET_ALREADY_EXISTS);
            }
            newTicket = new Ticket(ticketId, client, ticketType);
        }

        cashier.addTicket(newTicket);
        client.addTicket(newTicket);
        newTicket.printInitialState();
        System.out.println(StaticMessages.TICKET_NEW_OK);
    }

    /**
     * Valida que el cliente tenga permiso para crear ese tipo de ticket
     *
     * @param client     Cliente que intenta crear el ticket
     * @param ticketType Tipo de ticket ('p', 'c', 's')
     * @throws StoreException si el cliente no tiene permiso
     */
    private void validateTicketTypeForClient(Client client, String ticketType) throws StoreException {

        // Cliente Individual solo puede crear tickets personales (p)
        if (client instanceof IndividualClient) {
            if (!"p".equalsIgnoreCase(ticketType)) {
                throw new StoreException(
                        StaticMessages.WRONG_TICKET_TYPE + ticketType
                );
            }
            if (!AppConfigurations.INDIVIDUAL_TICKET_ALLOWS_SERVICES &&
                    "s ".equalsIgnoreCase(ticketType)) {
                throw new StoreException(
                        StaticMessages.ERROR_PROCESSING
                );
            }
        }

        // Cliente Corporativo puede crear todos los tipos
        if (client instanceof CorporateClient) {
            if ("c".equalsIgnoreCase(ticketType)) {
                if (!AppConfigurations.CORPORATE_TICKET_ALLOWS_COMBINED) {
                    throw new StoreException(
                            StaticMessages.WRONG_TICKET_ACTION
                    );
                }
            } else if ("s".equalsIgnoreCase(ticketType)) {
                if (!AppConfigurations.CORPORATE_TICKET_ALLOWS_SERVICES) {
                    throw new StoreException(
                            StaticMessages.ERROR_PROCESSING
                    );
                }
            }
        } else if (!"p".equalsIgnoreCase(ticketType)) {
            // Si no es CorporateClient y no es 'p', rechazar
            throw new StoreException(
                    StaticMessages.WRONG_TICKET_ACTION
            );
        }
    }

    /**
     * Agrega un producto al ticket
     * Valida que el tipo de cliente sea compatible con el tipo de producto
     */
    private void ticketAdd(String[] commands, String fullCommand) throws StoreException {
        String ticketId = commands[2];
        String cashId = commands[3];
        String prodId = commands[4];
        int amount = 1;

        if(commands.length != 5) { //si no es un servicio
            amount = Integer.parseInt(commands[5]);
        }

        Cashier cashier = UserDatabase.getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        Ticket ticket = cashier.getTicketById(ticketId);
        if (ticket == null) {
            throw new StoreException(StaticMessages.TICKET_NOT_FOUND);
        }

        // Obtener el producto y validar acceso
        AbstractProduct product = ProductCatalog.getProduct(prodId);
        if (product == null) {
            throw new StoreException(StaticMessages.PROD_NO_EXIST);
        }

        // Si es un servicio y el cliente es Individual, rechazar
        if (product instanceof Service && ticket.getClient() instanceof IndividualClient) {
            throw new StoreException(
                    StaticMessages.WRONG_TICKET_ACTION
            );
        }

        ArrayList<String> customizations = extractCustomizations(fullCommand);
        ticket.add(prodId, amount, customizations);
    }

    private void ticketRemove(String[] commands) throws StoreException {
        String ticketId = commands[2];
        String cashId = commands[3];
        String prodId = commands[4];

        Cashier cashier = UserDatabase.getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        Ticket ticket = cashier.getTicketById(ticketId);
        if (ticket == null) {
            throw new StoreException(StaticMessages.TICKET_NOT_FOUND);
        }

        ticket.remove(prodId);
    }

    /**
     * Imprime el ticket con visualización diferente según tipo de cliente
     */
    private void ticketPrint(String[] commands) throws StoreException {
        String ticketId = commands[2];
        String cashId = commands[3];

        Cashier cashier = UserDatabase.getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        Ticket ticket = cashier.getTicketById(ticketId);
        if (ticket == null) {
            throw new StoreException(StaticMessages.TICKET_NOT_FOUND);
        }

        // Visualización diferente:  Mostrar header según tipo de cliente
        Client client = ticket.getClient();

        // ← Cambio: En lugar de operador ternario
        if (client instanceof CorporateClient) {
            System.out.println(StaticMessages.CORPORATE_TICKET_HEADER);
        } else {
            System.out.println(StaticMessages.INDIVIDUAL_TICKET_HEADER);
        }

        ticket.print();
    }

    private void ticketList() throws StoreException {
        System.out.println(StaticMessages.TICKET_LIST);
        ArrayList<Cashier> cashiers = UserDatabase.getInstance().getAll(Cashier.class);

        for (Cashier cashier : cashiers) {
            ArrayList<Ticket> tickets = cashier.getTicketsSortedById();
            for (Ticket ticket : tickets) {
                // Mostrar identificador del cliente
                System.out.println("  " + ticket.getId() + " - " +
                        ticket.getState() +
                        " [" + ticket.getClient().getIdentifier() + "]");
            }
        }
        System.out.println(StaticMessages.TICKET_LIST_OK);
    }

// ==================== COMANDOS DE CAJERO ====================

    public void cashierCommands(String[] commands) throws StoreException {
        switch (commands[1].toUpperCase()) {
            case "ADD":
                cashierAdd(commands);
                break;
            case "REMOVE":
                UserDatabase.getInstance().remove(commands[2]);
                break;
            case "LIST":
                UserDatabase.getInstance().listCashiers();
                break;
            case "TICKETS":
                UserDatabase.getInstance().showCashierTickets(commands[2]);
                break;
            default:
                throw new StoreException(StaticMessages.NOT_VALID_CMD);
        }
    }

    private void cashierAdd(String[] commands) throws StoreException {
        String fullCommand = String.join(" ", commands);
        String name = extractName(fullCommand);
        String email = commands[commands.length - 1];

        // ← Cambio: En lugar de operador ternario
        if (InputValidator.isCashID(commands[2])) {
            UserDatabase.getInstance().add(new Cashier(commands[2], name, email));
        } else {
            String generatedId = UserDatabase.getInstance().generateCashId();
            UserDatabase.getInstance().add(new Cashier(generatedId, name, email));
        }
    }

// ==================== COMANDOS DE CLIENTE ====================

    public void clientCommands(String[] commands) throws StoreException {
        switch (commands[1].toUpperCase()) {
            case "ADD":
                clientAdd(commands);
                break;
            case "REMOVE":
                UserDatabase.getInstance().remove(commands[2]);
                break;
            case "LIST":
                UserDatabase.getInstance().listClients();
                break;
            default:
                throw new StoreException(StaticMessages.INVALID_INPUT);
        }
    }

    private void clientAdd(String[] commands) throws StoreException {
        String fullCommand = String.join(" ", commands);
        String name = extractName(fullCommand);
        String rest = fullCommand.substring(fullCommand.lastIndexOf('"') + 1).trim();
        String[] parts = rest.split(" ");

        Cashier cashier = UserDatabase.getInstance().getById(parts[2], Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        Client newClient = ClientFactory.createClient(name, parts[0], parts[1], cashier);
        UserDatabase.getInstance().add(newClient);
    }



    /**
     * Extrae el nombre entre comillas de un comando
     */
    private String extractName(String command) {
        int first = command.indexOf('"');
        int last = command.lastIndexOf('"');
        if (first == -1 || last == -1 || first == last) { // Si es un productoServicio
            return "";
        }
        return command.substring(first + 1, last);
    }

    public String[] processProdDate(String command) {
        return command.trim().split(" ");
    }

    /**
     * Procesa el comando prod add y retorna un array con los parámetros
     */
    public String[] processProdAdd(String command) {
        String name = extractName(command);
        String beforeName = command.substring(0, command.indexOf('"')).trim();
        String afterName = command.substring(command.lastIndexOf('"') + 1).trim();
        String[] beforeParts = beforeName.split(" ");
        String[] afterParts = afterName.split(" ");

        boolean hasId = beforeParts.length == 3;
        boolean hasMaxPers = afterParts.length == 3;

        int size;
        if ((hasId && hasMaxPers) || (!hasId && hasMaxPers)) {
            size = 7;
        } else {
            size = 6;
        }

        String[] result = new String[size];
        result[0] = "Prod";
        result[1] = "add";

        if (hasId) {
            result[2] = beforeParts[2];
        } else {
            result[2] = "GENERATE";
        }

        result[3] = name;
        result[4] = afterParts[0];
        result[5] = afterParts[1];

        if (hasMaxPers) {
            result[6] = afterParts[2];
        }
        return result;
    }

    /**
     * Extrae las customizaciones (--p) del comando
     */
    private ArrayList<String> extractCustomizations(String fullCommand) {
        ArrayList<String> customs = new ArrayList<>();

        if (fullCommand.toLowerCase().contains("--p")) {
            String tail = fullCommand.substring(fullCommand.toLowerCase().indexOf("--p"));
            String[] parts = tail.split("(?i)--p");

            for (String part : parts) {
                String text = part.trim();
                if (text.startsWith("\"") && text.endsWith("\"") && text.length() > 1) {
                    text = text.substring(1, text.length() - 1);
                }
                if (!text.isEmpty()) {
                    customs.add(text);
                }
            }
        }
        return customs;
    }

    private void help() {
        System.out.print(StaticMessages.HELP_TEXT);
    }

    private void echo(String fullCommand) {
        System.out.println(fullCommand.substring(5));
    }

    private String typeCommand() {
        String command;
        try {
            if (fromKeyboard) {
                System.out.print(AppConfigurations.PROMPT);
                command = sc.nextLine();
                System.out.println(command);
            } else {
                if (!sc.hasNextLine()) {
                    return "EXIT";
                }
                command = sc.nextLine();
                System.out.println(AppConfigurations.PROMPT + command);
            }
            return command;
        } catch (NoSuchElementException e) {
            System.out.println(StaticMessages.INFO_NOT_FOUND);
            return "EXIT";
        }
    }

    private int countNotNull(String[] s) {
        int c = 0;
        for (String str : s) {
            if (str != null) c++;
        }
        return c;
    }
}