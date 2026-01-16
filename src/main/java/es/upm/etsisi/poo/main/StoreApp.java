package es.upm. etsisi.poo.main;
import java.time.LocalDate;
import es.upm.etsisi. poo.model.products.ServiceTypes;
import es.upm.etsisi.poo. Command. AppConfigurations;
import es.upm.etsisi.poo. exceptions.StoreException;
import es.upm.etsisi.poo.model.products.*;
import es.upm. etsisi.poo.model. sales. Ticket;
import es.upm.etsisi.poo. model.users. Cashier;
import es.upm.etsisi.poo.model.users.Client;
import es.upm. etsisi.poo.patterns.ClientFactory;
import es.upm. etsisi.poo.persistence.*;
import es.upm. etsisi.poo.utils.*;
import es.upm.etsisi. poo.model.users.IndividualClient;
import es. upm.etsisi.poo.model.users.CorporateClient;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util. ArrayList;
import java.util. Comparator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import es.upm.etsisi. poo.model.users.User;


public class StoreApp {

    public StoreApp() {
    }

    public void start() {
        System.out. println(StaticMessages.WELCOME);
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print("tUPM> ");
            String input = scanner. nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            String[] commandParts = input.split(" ");

            try {
                if (!  InputValidator.validCommand(input)) {
                    System.out.println(StaticMessages.NOT_VALID_CMD);
                    continue;
                }

                switch (commandParts[0].toUpperCase()) {
                    case "PROD":
                        prodCommands(commandParts, input);
                        break;
                    case "CLIENT":
                        clientCommands(commandParts, input);
                        break;
                    case "CASH":
                        cashCommands(commandParts, input);
                        break;
                    case "TICKET":
                        ticketCommands(commandParts, input);
                        break;
                    case "HELP":
                        System.out.println(StaticMessages.HELP_TEXT);
                        break;
                    case "ECHO":
                        echo(input);
                        break;
                    case "EXIT":
                        System.out. println(StaticMessages. CLOSING_APP);
                        running = false;
                        break;
                    default:
                        System.out. println(StaticMessages.NOT_VALID_CMD);
                }
            } catch (StoreException e) {
                System.out.println("Error processing -> " + commandParts[0]. toLowerCase() + " " + (commandParts. length > 1 ? commandParts[1]. toLowerCase() : "") + " -> " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error processing -> " + commandParts[0].toLowerCase() + " " + (commandParts.length > 1 ? commandParts[1].toLowerCase() : "") + " -> " + e.getMessage());
            }
        }

        System.out.println(StaticMessages.GOODBYE);
    }

    // ==================== PRODUCT COMMANDS ====================

    public void prodCommands(String[] commands, String fullCommand) throws StoreException {
        String prodCommand = commands[1].toUpperCase();
        switch (prodCommand) {
            case "ADD":
                prodAdd(commands, fullCommand);
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
                System.out.println(StaticMessages. INVALID_INPUT);
        }
    }


    private void prodAdd(String[] commands, String fullCommand) throws StoreException {
        // ← CASO 1: Servicio (4 argumentos:    prod add <date> <serviceType>)
        if (commands.length == 4) {
            String possibleDate = commands[2];
            String possibleServiceType = commands[3];

            if (isValidDate(possibleDate) && isValidServiceType(possibleServiceType)) {
                prodAddService(possibleDate, possibleServiceType);
                return;
            }
        }

        // ← CASO 2: Producto normal (mínimo 5 argumentos)
        if (commands.length < 5) {
            System.out.println(StaticMessages.INVALID_INPUT);
            return;
        }

        try {
            String[] parsed = processProdAdd(fullCommand);
            String id = parsed[2].  equals("GENERATE") ? AbstractProduct.generateID() : parsed[2];
            String name = parsed[3];
            Category category = Category.valueOf(parsed[4].  toUpperCase());
            double price = Double.parseDouble(parsed[5]);

            // ← CORRECCIÓN: Verificar si hay maxPers ANTES de intentar parsearlo
            if (parsed.  length >= 7 && parsed[6] != null) {
                int maxPers = Integer.parseInt(parsed[6]);
                ProductCatalog.add(id, new PersonalizedProduct(id, name, category, price, maxPers));
            } else {
                ProductCatalog.add(id, new StockProduct(id, name, category, price));
            }
        } catch (StoreException e) {
            throw e;
        } catch (Exception e) {
            throw new StoreException("Error adding product:   " + e.getMessage());
        }
    }

    /**
     * Agrega un servicio al catálogo
     * prod add <date:    yyyy-MM-dd> <serviceType>
     */
    private void prodAddService(String dateStr, String serviceTypeStr) throws StoreException {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalDateTime dateTime = date.atStartOfDay();
            ServiceTypes serviceType = ServiceTypes.valueOf(serviceTypeStr.toUpperCase());

            Service service = new Service(serviceType, dateTime);
            ProductCatalog.add(service. getId(), service);

            System.out.println(service);
            System.out.println("prod add:    ok");
        } catch (StoreException e) {
            throw e;
        } catch (Exception e) {
            throw new StoreException("Error adding service: " + e.getMessage());
        }
    }

    /**
     * Valida que una fecha tenga formato yyyy-MM-dd
     */
    private boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida que el tipo de servicio sea válido
     */
    private boolean isValidServiceType(String serviceType) {
        try {
            ServiceTypes. valueOf(serviceType.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }


    private String[] processProdAdd(String fullCommand) throws StoreException {
        String afterProdAdd = fullCommand.substring(fullCommand.indexOf("add") + 3).trim();

        String[] result = new String[7];
        result[0] = "prod";
        result[1] = "add";

        try {
            // Check if first token is an ID (no quotes) or if we need to generate one
            if (afterProdAdd.startsWith("\"")) {
                result[2] = "GENERATE";
                int firstQuote = afterProdAdd.  indexOf('"');
                int lastQuote = afterProdAdd.  lastIndexOf('"');

                result[3] = afterProdAdd.substring(firstQuote + 1, lastQuote);

                String afterName = afterProdAdd.substring(lastQuote + 1).trim();
                String[] categoryAndPrice = afterName.split(" ");

                if (categoryAndPrice.length < 2) {
                    throw new StoreException("Missing category or price");
                }

                result[4] = categoryAndPrice[0];
                result[5] = categoryAndPrice[1];

                //   Solo asignar maxPers si existe
                if (categoryAndPrice. length > 2) {
                    result[6] = categoryAndPrice[2];
                } else {
                    result[6] = null;
                }
            } else {
                // First token is ID
                int spaceIdx = afterProdAdd.indexOf(" ");
                if (spaceIdx == -1) {
                    throw new StoreException("Invalid product add format");
                }
                result[2] = afterProdAdd.substring(0, spaceIdx);

                String rest = afterProdAdd.substring(spaceIdx).trim();
                int firstQuote = rest.indexOf('"');
                int lastQuote = rest.lastIndexOf('"');

                if (firstQuote == -1 || lastQuote == -1) {
                    throw new StoreException("Name must be enclosed in quotes");
                }

                result[3] = rest.substring(firstQuote + 1, lastQuote);

                String afterName = rest.substring(lastQuote + 1).trim();
                String[] categoryAndPrice = afterName.split(" ");

                if (categoryAndPrice.length < 2) {
                    throw new StoreException("Missing category or price");
                }

                result[4] = categoryAndPrice[0];
                result[5] = categoryAndPrice[1];


                if (categoryAndPrice.  length > 2) {
                    result[6] = categoryAndPrice[2];
                } else {
                    result[6] = null;
                }
            }

            return result;
        } catch (StoreException e) {
            throw e;
        } catch (Exception e) {
            throw new StoreException("Error parsing product add command:  " + e.getMessage());
        }
    }

    private void prodAddFood(String[] commands) throws StoreException {
        try {
            String commandsString = String.join(" ", commands);

            String name = commandsString.substring(commandsString.indexOf('"') + 1, commandsString.lastIndexOf('"'));
            String beforeName = commandsString.substring(0, commandsString.indexOf('"')).trim();
            String afterName = commandsString.substring(commandsString.lastIndexOf('"') + 1).trim();

            String[] beforeNameParts = beforeName.split(" ");
            String[] afterNameParts = afterName.split(" ");

            if (afterNameParts.length < 3) {
                throw new StoreException("Invalid food command format");
            }

            String dateString = afterNameParts[1];
            LocalDate date = LocalDate.parse(dateString);
            LocalDateTime eventTime = date.atStartOfDay();
            double price = Double.parseDouble(afterNameParts[0]);
            int maxPeople = Integer.parseInt(afterNameParts[2]);

            if (price <= 0) {
                throw new StoreException("Price must be greater than 0");
            }
            if (maxPeople <= 0 || maxPeople > 100) {
                throw new StoreException("Max people must be between 1 and 100");
            }

            String id = null;
            if (beforeNameParts.length > 2 && !  beforeNameParts[2].isEmpty()) {
                id = beforeNameParts[2];
            }

            Event product;
            if (id == null) {
                product = new Event(name, eventTime, price, maxPeople, EventType.FOOD);
            } else {
                product = new Event(id, name, eventTime, price, maxPeople, EventType. FOOD);
            }

            ProductCatalog.add(product. getId(), product);
            System. out.println(product);
            System.out.println("prod addFood:  ok");
        } catch (StoreException e) {
            throw e;
        } catch (Exception e) {
            throw new StoreException("Error adding food:  " + e.getMessage());
        }
    }

    private void prodAddMeeting(String[] commands) throws StoreException {
        try {
            String commandsString = String.join(" ", commands);

            String name = commandsString.substring(commandsString.indexOf('"') + 1, commandsString.lastIndexOf('"'));
            String beforeName = commandsString.substring(0, commandsString.indexOf('"')).trim();
            String afterName = commandsString.substring(commandsString. lastIndexOf('"') + 1).trim();

            String[] beforeNameParts = beforeName. split(" ");
            String[] afterNameParts = afterName.split(" ");

            if (afterNameParts.length < 3) {
                throw new StoreException("Invalid meeting command format");
            }

            String dateString = afterNameParts[1];
            LocalDate date = LocalDate.parse(dateString);
            LocalDateTime eventTime = date.atStartOfDay();
            double price = Double.parseDouble(afterNameParts[0]);
            int maxPeople = Integer.parseInt(afterNameParts[2]);

            if (price <= 0) {
                throw new StoreException("Price must be greater than 0");
            }
            if (maxPeople <= 0 || maxPeople > 100) {
                throw new StoreException("Max people must be between 1 and 100");
            }

            String id = null;
            if (beforeNameParts.length > 2 && ! beforeNameParts[2].isEmpty()) {
                id = beforeNameParts[2];
            }

            Event product;
            if (id == null) {
                product = new Event(name, eventTime, price, maxPeople, EventType. MEETING);
            } else {
                product = new Event(id, name, eventTime, price, maxPeople, EventType.MEETING);
            }

            ProductCatalog.add(product.getId(), product);
            System.out.println(product);
            System.out.println("prod addMeeting:  ok");
        } catch (StoreException e) {
            throw e;
        } catch (Exception e) {
            throw new StoreException("Error adding meeting: " + e.getMessage());
        }
    }

    public void prodUpdateManage(String id, String CategoryToChange, String change) throws StoreException {
        AbstractProduct product = ProductCatalog. getProduct(id);
        if (product == null) {
            throw new StoreException(StaticMessages.PROD_NO_EXIST);
        }

        switch (CategoryToChange) {
            case "NAME":
                product.setName(change);
                ProductCatalog.update(id, product);
                System.out.println(product);
                break;
            case "PRICE":
                try {
                    double newPrice = Double.parseDouble(change);
                    if (newPrice <= 0) {
                        throw new StoreException(StaticMessages.INVALID_INPUT);
                    }
                    if (product.setPrice(newPrice)) {
                        ProductCatalog.update(id, product);
                        System.out.println(product);
                    } else {
                        throw new StoreException(StaticMessages. INVALID_INPUT);
                    }
                } catch (NumberFormatException e) {
                    throw new StoreException(StaticMessages. INVALID_INPUT);
                }
                break;
            case "CATEGORY":
                if (product.hasCategory()) {
                    try {
                        Category newCategory = Category.valueOf(change.toUpperCase());
                        if (product.setCategory(newCategory)) {
                            ProductCatalog.update(id, product);
                            System.out.println(product);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new StoreException(StaticMessages.INVALID_INPUT);
                    }
                } else {
                    throw new StoreException(StaticMessages.INVALID_INPUT);
                }
                break;
            default:
                throw new StoreException(StaticMessages.INVALID_INPUT);
        }
    }

    private void prodUpdate(String[] commands, String fullCommand) throws StoreException {
        String id = commands[2];
        String category = commands[3]. toUpperCase();
        String value;

        if ("NAME".equals(category)) {
            int firstQuote = fullCommand.indexOf('"');
            int lastQuote = fullCommand.lastIndexOf('"');
            if (firstQuote == -1 || lastQuote == -1) {
                throw new StoreException("Name must be enclosed in quotes");
            }
            value = fullCommand. substring(firstQuote + 1, lastQuote);
        } else {
            value = commands[4];
        }

        prodUpdateManage(id, category, value);
        System.out.println("prod update: ok");
    }

    private void prodRemove(String id) throws StoreException {
        AbstractProduct product = ProductCatalog. getProduct(id);
        if (product == null) {
            throw new StoreException(StaticMessages.PROD_NO_EXIST);
        }

        ProductCatalog.remove(id);
        System.out.println(product);
        System.out. println("prod remove: ok");
    }

    // ==================== CLIENT COMMANDS ====================

    public void clientCommands(String[] commands, String fullCommand) throws StoreException {
        switch (commands[1]. toUpperCase()) {
            case "ADD":
                clientAdd(commands, fullCommand);
                break;
            case "REMOVE":
                clientRemove(commands[2]);
                break;
            case "LIST":
                clientList();
                break;
            default:
                throw new StoreException(StaticMessages.INVALID_INPUT);
        }
    }

    private void clientAdd(String[] commands, String fullCommand) throws StoreException {
        String commandString = String.join(" ", commands);

        if (commandString.split("\"").length < 3) {
            throw new StoreException(StaticMessages.INVALID_INPUT);
        }

        String name = commandString.substring(commandString.indexOf('"') + 1, commandString.lastIndexOf('"'));
        String substringAfterName = commandString.substring(commandString.lastIndexOf('"') + 1).trim();
        String[] subarray = substringAfterName.split(" ");

        if (subarray.length != 3) {
            throw new StoreException(StaticMessages. INVALID_INPUT);
        }

        String identifier = subarray[0];
        String email = subarray[1];
        String cashId = subarray[2];

        // ← Obtener el cajero
        Cashier cashier = UserDatabase.getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        // ← Detectar tipo de cliente:    Si empieza con letra mayúscula → Corporativo
        Client newClient;
        if (Character.isLetter(identifier.charAt(0)) && Character.isUpperCase(identifier.charAt(0))) {
            newClient = new CorporateClient(name, identifier, email, cashier);
        } else {
            newClient = new IndividualClient(name, identifier, email, cashier);
        }

        UserDatabase.getInstance().add(newClient);
    }

    private void clientRemove(String dni) throws StoreException {
        // ← CAMBIO:  Usar UserDatabase
        UserDatabase.getInstance().remove(dni);
    }

    private void clientList() {

        UserDatabase.getInstance().listClients();
    }

    // ==================== CASHIER COMMANDS ====================

    public void cashCommands(String[] commands, String fullCommand) throws StoreException {
        switch (commands[1].toUpperCase()) {
            case "ADD":
                cashierAdd(commands);
                break;
            case "REMOVE":
                cashierRemove(commands[2]);
                break;
            case "LIST":
                cashierList();
                break;
            case "TICKETS":
                cashierTickets(commands[2]);
                break;
            default:
                throw new StoreException(StaticMessages.INVALID_INPUT);
        }
    }

    private void cashierAdd(String[] commands) throws StoreException {
        String commandString = String.join(" ", commands);

        if (commandString.split("\"").length < 3) {
            throw new StoreException(StaticMessages.INVALID_INPUT);
        }

        String name = commandString.substring(commandString. indexOf('"') + 1, commandString.lastIndexOf('"'));
        String substringAfterName = commandString.substring(commandString.lastIndexOf('"') + 1).trim();
        String[] subarray = substringAfterName.split(" ");

        if (subarray.length != 1) {
            throw new StoreException(StaticMessages.INVALID_INPUT);
        }

        String email = subarray[0];
        String id = null;

        // Detectar si el primer parámetro después de "add" es un ID
        if (commands.length > 2 && InputValidator.isCashID(commands[2])) {
            id = commands[2];
        }

        Cashier newCashier;
        if (id == null) {
            id = UserDatabase.getInstance().generateCashId();
        }

        newCashier = new Cashier(id, name, email);
        UserDatabase.getInstance().add(newCashier);
    }

    private void cashierRemove(String id) throws StoreException {
        // ← CAMBIO:    Usar UserDatabase
        UserDatabase.getInstance().remove(id);
    }

    private void cashierList() {
        // ← CAMBIO:  Usar UserDatabase
        UserDatabase.getInstance().listCashiers();
    }

    private void cashierTickets(String cashierId) throws StoreException {
        UserDatabase.getInstance().showCashierTickets(cashierId);
    }

    // ==================== TICKET COMMANDS ====================

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

    private void ticketNew(String[] commands) throws StoreException {
        // ← VALIDAR longitud básica
        if (commands.length < 4 || commands.length > 6) {
            throw new StoreException(StaticMessages.INVALID_INPUT);
        }

        String ticketId = null;
        String cashId, userId;
        String ticketType = "p"; // default:    personal

        // ← Detectar flag al final (comienza con -)
        String flag = null;
        if (commands[commands.length - 1].startsWith("-")) {
            flag = commands[commands.length - 1];
            if (!  isValidTicketFlag(flag)) {
                throw new StoreException("Invalid ticket type flag.    Use -p, -c, or -s");
            }
            ticketType = flag. substring(1); // Extrae 'c', 'p', 's'
        }

        // Parsear argumentos según cantidad
        if (commands.length == 4 || (commands.length == 5 && flag != null)) {
            // Sin ID:    ticket new <cashId> <userId> [-p|-c|-s]
            cashId = commands[2];
            userId = commands[3];
        } else if (commands.length == 5 && flag == null) {
            // Con ID sin flag:  ticket new <ticketId> <cashId> <userId>
            ticketId = commands[2];
            cashId = commands[3];
            userId = commands[4];
        } else if (commands.length == 6) {
            // Con ID y flag:  ticket new <ticketId> <cashId> <userId> -[p|c|s]
            ticketId = commands[2];
            cashId = commands[3];
            userId = commands[4];
            flag = commands[5];
            if (! isValidTicketFlag(flag)) {
                throw new StoreException("Invalid ticket type flag.  Use -p, -c, or -s");
            }
            ticketType = flag.substring(1);
        } else {
            throw new StoreException(StaticMessages.INVALID_INPUT);
        }


        Client client = UserDatabase.getInstance().getById(userId, Client.class);
        if (client == null) {
            throw new StoreException(StaticMessages. CLIENT_NOT_FOUND);
        }

        // ← Validar tipo de ticket según cliente
        validateTicketTypeForClient(client, ticketType);


        Cashier cashier = UserDatabase.getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        // ← Crear ticket
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


    private void ticketAdd(String[] commands, String fullCommand) throws StoreException {
        String ticketId = commands[2];
        String cashId = commands[3];
        String prodId = commands[4];

        // Detectar si es un servicio (prodId termina en 'S') o un producto normal
        int amount = 0;
        ArrayList<String> customs = new ArrayList<>();

        if (! prodId.toUpperCase().endsWith("S")) {
            // Es un producto normal, requiere cantidad
            if (commands. length < 6) {
                throw new StoreException("Product requires amount");
            }
            amount = Integer.parseInt(commands[5]);

            // Procesar customizaciones si las hay
            if (commands.length > 6) {
                for (int i = 6; i < commands.length; i++) {
                    if (commands[i].startsWith("--p")) {
                        customs.add(commands[i]. substring(3));
                    }
                }
            }
        }

        //  Usar UserDatabase
        Cashier cashier = UserDatabase. getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        Ticket ticket = cashier.getTicketById(ticketId);
        if (ticket == null) {
            throw new StoreException(StaticMessages.TICKET_NOT_FOUND);
        }

        // Si es servicio, amount = 1; si es producto, use el amount indicado
        if (prodId.toUpperCase().endsWith("S")) {
            amount = 1;
        }

        ticket.add(prodId, amount, customs);
    }

    private void ticketRemove(String[] commands) throws StoreException {
        String ticketId = commands[2];
        String cashId = commands[3];
        String prodId = commands[4];

        // ← CAMBIO: Usar UserDatabase
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

    private void ticketPrint(String[] commands) throws StoreException {
        String ticketId = commands[2];
        String cashId = commands[3];

        Cashier cashier = UserDatabase. getInstance().getById(cashId, Cashier.class);
        if (cashier == null) {
            throw new StoreException(StaticMessages.CASHIER_NOT_FOUND);
        }

        Ticket ticket = cashier.getTicketById(ticketId);
        if (ticket == null) {
            throw new StoreException(StaticMessages. TICKET_NOT_FOUND);
        }

        ticket.print();
    }

    private void ticketList() {
        System.out.println(StaticMessages.TICKET_LIST);

        ArrayList<Cashier> cashiers = UserDatabase.getInstance().getAll(Cashier.class);
        cashiers.sort(Comparator.comparing(User::getName));

        for (Cashier cashier : cashiers) {
            ArrayList<Ticket> tickets = cashier.getTicketsSortedById();
            for (Ticket t : tickets) {
                System. out.println("  " + t.getId() + " - " + t.getState() + " [" + t.getClient().getIdentifier() + "]");
            }
        }

        System.out.println(StaticMessages.TICKET_LIST_OK);
    }


       //Valida que el flag sea válido (-p, -c, -s)

    private boolean isValidTicketFlag(String flag) {
        return flag. equals("-p") || flag.equals("-c") || flag.equals("-s");
    }

    /**
     *  Valida tipo de ticket según cliente
     */
    private void validateTicketTypeForClient(Client client, String ticketType) throws StoreException {
        // Si el cliente es Individual y pide servicio, error
        if (client instanceof IndividualClient && ("s".equals(ticketType) || "c".equals(ticketType))) {
            throw new StoreException(StaticMessages.WRONG_TICKET_TYPE);
        }

        // Si el cliente es CorporateClient, permitir todos los tipos
        // Si es CompanyClient, permitir servicio y combined
    }

    // ==================== UTILITY COMMANDS ====================

    private void echo(String fullCommand) {
        String message = fullCommand.substring(fullCommand.indexOf('"') + 1, fullCommand.lastIndexOf('"'));
        System.out. println("\"" + message + "\"");
    }

    public static void main(String[] args) {
        StoreApp app = new StoreApp();
        app.start();
    }
}