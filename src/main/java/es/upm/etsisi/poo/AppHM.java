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
                System.out.println("ERROR: Invalid input");
        }
    }

    private void help() {
        System.out.println(("""
                Commands:
                 prod add <id> "<name>" <category> <price>
                 prod list
                 prod update <id> NAME|CATEGORY|PRICE <value>
                 prod remove <id>
                 ticket new
                 ticket add <prodId> <quantity>
                 ticket remove <prodId>
                 ticket print
                 echo "<texto>"
                 help
                 exit
                
                Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
                Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%,
                ELECTRONICS 3%.
               """));
    }

    private String typeCommand() {
        System.out.print("tUPM> ");
        try{
            String command = sc.nextLine();
            return command;
        }catch(NoSuchElementException e){
            System.out.println("ERROR: Info not found");
            return "exit";
        }
    }
}