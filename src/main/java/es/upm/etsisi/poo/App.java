package es.upm.etsisi.poo;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        App app = new App();
        app.init();
    }

    private void init() {
        System.out.println("Welcome to the ticket module App. Type 'help' to see commands");
        start();
    }

    private void start() {
        boolean keepGoing = true;
        Ticket ticket = new Ticket();
        while (keepGoing) {
            String command = typeCommand();
            if (!validCommand(command)) {
                System.out.println("Not a valid command");
            } else {
                String[] commandParts = command.split(" ");
                switch (commandParts[0]) {
                    case "prod":
                        switch (commandParts[1]) {
                            case "add":
                                String name = command.substring(command.indexOf('"') + 1, command.lastIndexOf('"'));
                                String restOfCommand = command.substring(command.lastIndexOf('"')+1).trim();
                                String[] partsRest = restOfCommand.split(" ");
                                Catalog.add(Integer.parseInt(commandParts[2]), name, Category.valueOf(partsRest[0].toUpperCase()), Double.parseDouble(partsRest[1]));
                                break;
                            case "list":
                                Catalog.list();
                                break;
                            case "update":
                                String field = commandParts[3].toUpperCase();
                                String update;
                                if (field.equals("NAME")) {
                                    int firstIndex = command.indexOf('"');
                                    int lastIndex = command.lastIndexOf('"');
                                    update = command.substring(firstIndex+1, lastIndex);
                                } else {
                                    update = commandParts[4];
                                }
                                Catalog.update(Integer.parseInt(commandParts[2]), field, update);
                                break;
                            case "remove":
                                Catalog.remove(Integer.parseInt(commandParts[2]));
                                break;
                        }
                        break;
                    case "ticket":
                        switch (commandParts[1]) {
                            case "new":
                                ticket.resetTicket();
                                break;
                            case "add":
                                ticket.add(Integer.parseInt(commandParts[2]), Integer.parseInt(commandParts[3]));
                                break;
                            case "remove":
                                ticket.remove(Integer.parseInt(commandParts[2]));
                                break;
                            case "print":
                                ticket.print();
                        }
                        break;
                    case "help":
                        help();
                        break;
                    case "echo":
                        System.out.println(command.substring(5));
                        break;
                    case "exit":
                        keepGoing = false;
                        end();
                        break;
                    default:
                        System.out.println("Not a valid command");
                }
                System.out.println();
                System.out.println();
            }
        }
    }

    private void end() {
        System.out.println("Closing application.");
        System.out.println("Goodbye!");
    }

    private void help() {
        System.out.println("Commands:");
        System.out.println(" prod add <id> \"<name>\" <category> <price>");
        System.out.println(" prod list");
        System.out.println(" prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println(" prod remove <id>");
        System.out.println(" ticket new");
        System.out.println(" ticket add <prodId> <quantity>");
        System.out.println(" ticket remove <prodId>");
        System.out.println(" ticket print");
        System.out.println(" echo \"<texto>\"");
        System.out.println(" help");
        System.out.println(" exit");
        System.out.println();
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println("Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%,");
        System.out.println("ELECTRONICS 3%.");
    }

    private String typeCommand() {  // To make easier the implementation of the shell
        Scanner sc = new Scanner(System.in);
        System.out.print("tUpm> ");
        String command = sc.nextLine();
        return command;
    }

    private boolean validCommand(String command) {
        if (command.equals("prod list") || command.equals("ticket new") || command.equals("ticket print") || command.equals("help") || command.equals("exit")) {
            return true;
        } else {
            String[] splittedCommand = command.split(" ");
            switch (splittedCommand[0]) {
                case "prod":
                    if (splittedCommand.length < 2) return false;
                    switch (splittedCommand[1]) {
                        case "add":
                            if (splittedCommand.length < 5) return false;
                            try {
                                int num = Integer.parseInt(splittedCommand[2]);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                            String name = command.substring(command.indexOf('"')+1, command.lastIndexOf('"'));
                            String rest = command.substring(command.lastIndexOf('"')+1).trim();
                            String[] partsRest = rest.split(" ");
                            if (partsRest.length != 2) {    // Ensure there is not extra parameters
                                return false;
                            }
                            if (!inCategory(partsRest[0].toUpperCase())) {
                                return false;
                            }
                            try {
                                double number = Double.parseDouble(partsRest[1]);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                            return true;
                        case "update":
                            if (splittedCommand.length < 4) return false;
                            try {
                                int num = Integer.parseInt(splittedCommand[2]);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                            if (!splittedCommand[3].toUpperCase().equals("NAME") && !splittedCommand[3].toUpperCase().equals("CATEGORY") && !splittedCommand[3].toUpperCase().equals("PRICE")) {
                                return false;
                            }
                            return true;
                        case "remove":
                            if (splittedCommand.length != 3) return false;
                            try {
                                double number = Double.parseDouble(splittedCommand[2]);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                            return true;
                        default:
                            return false;
                    }
                case "ticket":
                    if (splittedCommand.length < 2) return false;
                    switch (splittedCommand[1]) {
                        case "add":
                            if (splittedCommand.length != 4) return false;
                            try {
                                int number = Integer.parseInt(splittedCommand[2]);
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                return false;
                            }
                            try {
                                int number = Integer.parseInt(splittedCommand[3]);
                            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                return false;
                            }
                            return true;
                        case "remove":
                            if (splittedCommand.length != 3) return false;
                            try {
                                int number = Integer.parseInt(splittedCommand[2]);
                            } catch (NumberFormatException e) {
                                return false;
                            }
                            return true;
                        default:
                            return false;

                    }
                case "echo":
                    return true;
            }
        }
        return true;
    }

    private boolean inCategory(String category) {
        for (int i=0; i<Category.values().length; i++) {
            if (category.equals(Category.values()[i].name().toUpperCase())) {
                return true;
            }
        }
        return false;
    }

}
