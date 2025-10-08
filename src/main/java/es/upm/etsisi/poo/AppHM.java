package es.upm.etsisi.poo;
import java.nio.file.Files;
import java.nio.file.Paths; //Para leer path donde se encuentra help.txt
import java.sql.Array;
import java.util.Arrays;
import java.util.Scanner;

public class AppHM {
    private final Scanner sc = new Scanner(System.in);
    private final TicketHM ticket = new TicketHM();


    public static void main( String[] args ) {
        AppHM app = new AppHM();
        app.init();
    }

    private void init() {
        System.out.println("Welcome to the ticket module App.");
        System.out.println("Ticket module. Type 'help' to see commands.");
        start();
    }

    private void end() {
        System.out.println("Closing application.");
        System.out.println("Goodbye!");
    }

    public static String[] processProdAdd(String command) {
        String[]commandSplitted = command.split(" ");
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

    private void start() {
        boolean keepGoing = true;
        while (keepGoing) {
            String command = typeCommand();
            if (!InputValidator.validCommand(command)) {
                System.out.println("Not a valid command");
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
                        System.out.println(command.substring(5));
                        break;
                    case "EXIT":
                        keepGoing = false;
                        end();
                        break;
                    default:
                        System.out.println("Not a valid command");
                }
                System.out.println();
            }
        }
    }

    public void prodCommands(String[] commands) {
        String prodCommand = commands[1].toUpperCase();
        switch (prodCommand) {
            case "ADD":
                String fullCommand = String.join(" ", commands); // conseguimos el comando original(String)
                String[] commandPartsAdd = processProdAdd(fullCommand);
                int id1 = Integer.parseInt(commandPartsAdd[2]);
                String name  = commandPartsAdd[3];
                Category category = Category.valueOf(commandPartsAdd[4].toUpperCase());
                Double price = Double.parseDouble(commandPartsAdd[5]);
                Catalog.add(id1, name, category, price);
                break;
            case "LIST":
                Catalog.list();
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
                Catalog.update(id2, categoryToChange, change);
                break;
            case "REMOVE":
                int id3 = Integer.parseInt(commands[2]);
                Catalog.remove(id3);
                break;
            default:
                System.out.println("Invalid input");
                break;
        }
    }

    public void prodUpdateManage(int id, String CategoryToChange, String change) {
        Product product = ProductHM.getProduct(id);
        switch (CategoryToChange) {
            case "NAME":
                product.setName(change);
                break;
            case "PRICE":
                product.setPrice(Float.parseFloat(change));
                break;
            case "CATEGORY":
                product.setCategory(Category.valueOf(change));
                break;
            default:
                System.out.println("Invalid input");
                return;
        }
        ProductHM.update(id, product);
    }

    public void ticketCommands(String[] commands) {
        switch (commands[1].toUpperCase()) {
            case "NEW":
                ticket.resetTicket();
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
                System.out.println("Invalid input");
        }
    }

    private void help() {
        try {
            System.out.println();
            String line = Files.readString(Paths.get("src/main/java/resources/help.txt"));
            System.out.println(line);
        } catch (Exception e) {
            System.out.println("ERROR: while reading help.txt files");
        }
    }

    private String typeCommand() {  // To make easier the implementation of the shell
        System.out.print("tUPM> ");
        return sc.nextLine();
    }
}