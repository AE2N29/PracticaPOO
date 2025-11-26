package es.upm.etsisi.poo;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class AppHM {
    private final Scanner sc = new Scanner(System.in);
    private final Ticket ticket = new Ticket();

    public static void main( String[] args ) {
        AppHM app = new AppHM();
        app.init();
    }

    private void init() {
        System.out.println("\nWelcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");
        start();
    }

    private void start() {
        boolean keepGoing = true;
        while (keepGoing) {
            String command = typeCommand().trim();
            if (!InputValidator.validCommand(command)) {
                System.out.println("ERROR: Not a valid command");
            } else {
                String[] commandParts = command.split(" ");
                switch (commandParts[0].toUpperCase()) {
                    case "PROD":
                        prodCommands(commandParts);
                        break;
                    case "TICKET":
                        ticketCommands(commandParts);
                        break;
                    case "HELP":
                        help();
                        break;
                    case "ECHO":
                        System.out.println(command.substring(5)); // no hace falta llamar a toString(), es redundante
                        break;                                              // .substring(5) crea un substring desde index 5 hasta el final
                    case "EXIT":
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
                        System.out.println("ERROR: Not a valid command");
                }
                System.out.println();
            }
        }
    }

    private void end() {
        System.out.println("Closing application.");
        System.out.print("Goodbye!");
    }

    public void prodCommands(String[] commands) {

        String prodCommand = commands[1].toUpperCase();
        switch (prodCommand) {
            case "ADD":
                String fullCommand = String.join(" ", commands); // conseguimos el comando original(String)
                String[] commandPartsAdd = processProdAdd(fullCommand);
                int id1 = Integer.parseInt(commandPartsAdd[2]);
                String name = commandPartsAdd[3];
                Category category = Category.valueOf(commandPartsAdd[4].toUpperCase());
                double price = Double.parseDouble(commandPartsAdd[5]);
                Product product = new Product(id1, name, category, price);
                ProductHM.add(id1, product);
                break;
            case "LIST":
                ProductHM.list();
                break;
            case "UPDATE":
                int id2 = Integer.parseInt(commands[2]);
                String categoryToChange = commands[3].toUpperCase();
                String change;
                if ("NAME".equals(categoryToChange)) {
                    String full = String.join(" ", commands);
                    change = full.substring(full.indexOf('"') + 1, full.lastIndexOf('"'));
                } else {
                    change = commands[4];
                }
                prodUpdateManage(id2, categoryToChange, change);
                break;
            case "REMOVE":
                int id3 = Integer.parseInt(commands[2]);
                ProductHM.remove(id3);
                break;
            default:
                System.out.println("ERROR: Invalid input");
                break;
        }
    }

    public static String[] processProdAdd(String command) {
        String[] commandSplitted = command.split(" ");
        String id = commandSplitted[2];
        String name = command.substring(command.indexOf('"') + 1, command.lastIndexOf('"'));
        String restOfCommand = command.substring(command.lastIndexOf('"')+1).trim();
        String[] partsRest = restOfCommand.split(" ");
        String[] array = new String[6];
        array[0]= "prod";
        array[1]= "add";
        array[2]= id;
        array[3]= name;
        array[4] = partsRest[0];
        array[5] = partsRest[1];
        return array;
    }

    public void prodUpdateManage(int id, String CategoryToChange, String change) {
        Product product = ProductHM.getProduct(id);
        if (product == null) {
            System.out.println("ERROR: Product with id " + id + " does not exist!");
            return;
        }
        switch (CategoryToChange) {
            case "NAME":
                product.setName(change);
                break;
            case "PRICE":
                product.setPrice(Double.parseDouble(change));
                break;
            case "CATEGORY":
                product.setCategory(Category.valueOf(change.toUpperCase()));
                break;
            default:
                System.out.println("ERROR: Invalid input");
                return;
        }
        ProductHM.update(id, product);
    }

    public void ticketCommands(String[] commands) {
        switch (commands[1].toUpperCase()) {
            case "NEW":
                String inputCashId, inputUserId, inputTicketId = null;
                if (commands.length == 4) {
                    inputCashId = commands[2];
                    inputUserId = commands[3];
                } else {
                    inputTicketId = commands[2];
                    inputCashId = commands[3];
                    inputUserId = commands[4];
                }
                Cashier cashier = CashierDatabase.getCashierByUW(inputCashId);
                Client client = ClientDatabase.getClientByDNI(inputUserId);
                if (cashier == null) {
                    System.out.println("ERROR: Cashier with ID " + inputCashId + " not found");
                    return;
                }
                if (client == null) {
                    System.out.println("ERROR: Client with DNI " + inputUserId + " not found");
                    return;
                }
                Ticket newTicket;
                if (inputTicketId == null) { // cuando no pasan el id del ticket como parametro
                    newTicket = new Ticket();
                } else if (Ticket.isIdRegistered(inputTicketId)) { // cuando pasan el id del ticket como parametro
                    System.out.println("ERROR: Ticket ID " + inputTicketId + " already exists.");
                    return;
                } else {
                    newTicket = new Ticket(inputTicketId);
                }
                cashier.addTicket(newTicket);
                client.addTicket(newTicket);
                System.out.println("ticket new: ok");
                break;
            case "ADD":
                ticket.add(Integer.parseInt(commands[2]), Integer.parseInt(commands[3]));
                break;
            case "REMOVE":
                ticket.remove(Integer.parseInt(commands[2]));
                break;
            case "PRINT":
                ticket.print();
                break;
            default:
                System.out.println("ERROR: Invalid input");
        }
    }

    public void cashierCommands(String[] commands) {
        switch (commands[1].toUpperCase()) {
            case "ADD":
                String[] correctCommand = new String[commands.length];
                String correctCmdStringed = String.join(" ", commands);
                String name = correctCmdStringed.substring(correctCmdStringed.indexOf('"') + 1, correctCmdStringed.lastIndexOf('"'));
                correctCommand[0] = "CLIENT";
                correctCommand[1] = "ADD";
                if (InputValidator.isCashID(commands[2])) {
                    correctCommand[2] = commands[2];
                    correctCommand[3] = name;
                    correctCommand[4] = commands[commands.length-1];
                } else {
                    correctCommand[2] = name;
                    correctCommand[3] = commands[commands.length-1];
                }
                if (countNotNull(correctCommand) == 5) {
                    CashierDatabase.add(correctCommand[2], name, correctCommand[4]);
                } else if (countNotNull(correctCommand) == 4) {
                    CashierDatabase.add(correctCommand[2], correctCommand[3]);
                } else {
                    System.out.println("ERROR: Invalid input");
                }
                break;
            case "REMOVE":
                CashierDatabase.remove(commands[2]);
                break;
            case "LIST":
                CashierDatabase.list();
                break;
            case "TICKETS":
                CashierDatabase.tickets(commands[2]);
                break;
            default:
                System.out.println("ERROR: Not valid command");
                break;
        }
    }

    public void clientCommands(String[] commands) {
        switch (commands[1].toUpperCase()) {
            case "ADD":
                String stringedCommand = String.join(" ", commands);
                String name = stringedCommand.substring(stringedCommand.indexOf('"') + 1, stringedCommand.lastIndexOf('"'));
                String restOfCommand = stringedCommand.substring(stringedCommand.lastIndexOf('"')+1).trim();
                String[] afterName = restOfCommand.split(" ");
                ClientDatabase.add(name, afterName[0], afterName[1], afterName[2]);
                break;
            case "REMOVE":
                ClientDatabase.remove(commands[2]);
                break;
            case "LIST":
                ClientDatabase.list();
                break;
            default:
                System.out.println("ERROR: Invalid input");
        }
    }

    private void help() {
        System.out.println(("""
               Commands:
                 client add "<nombre>" <DNI> <email> <cashId>
                 client remove <DNI>
                 client list
                 cash add [<id>] "<nombre>"<email>
                 cash remove <id>
                 cash list
                 cash tickets <id>
                 ticket new [<id>] <cashId> <userId>
                 ticket add <ticketId><cashId> <prodId> <amount> [--p<txt> --p<txt>]
                 ticket remove <ticketId><cashId> <prodId>
                 ticket print <ticketId> <cashId>
                 ticket list
                 prod add <id> "<name>" <category> <price>
                 prod update <id> NAME|CATEGORY|PRICE <value>
                 prod addFood [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
                 prod addMeeting [<id>] "<name>" <price> <expiration:yyyy-MM-dd> <max_people>
                 prod list
                 prod remove <id>
                 help
                 echo “<text>”
                 exit
               
               Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
               Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.
               """));
    }

    private String typeCommand() {
        System.out.print("tUPM> ");
        try{
            return sc.nextLine();
        } catch(NoSuchElementException e){
            System.out.println("ERROR: Info not found");
            return "exit";
        }
    }

    private int countNotNull(String[] s) {
        int c=0;
        for (String str: s) {
            if (str != null) {
                c++;
            }
        }
        return c;
    }
}