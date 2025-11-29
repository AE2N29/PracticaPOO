package es.upm.etsisi.poo;

import es.upm.etsisi.poo.products.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
                String id1 = (commandPartsAdd[2]);
                if(id1.equals("GENERATE")) {
                    id1 = AbstractProduct.generateID();
                }
                String name = commandPartsAdd[3];
                Category category = Category.valueOf(commandPartsAdd[4].toUpperCase());
                double price = Double.parseDouble(commandPartsAdd[5]);
                if(commandPartsAdd.length == 7) {  // si el comando tiene length 7, es del tipo personalizable
                    int maxPers =  Integer.parseInt(commandPartsAdd[6]);
                    WrapProduct product = new WrapProduct(id1, name, category, price, maxPers);
                    ProductHM.add(id1, product);
                }
                else {
                    StockProducts product = new StockProducts(id1, name, category, price);
                    ProductHM.add(id1, product);
                }
                break;

            case "ADDFOOD":

                String commandsStringFood = String.join(" ", commands);
                String nameFood = commandsStringFood.substring(commandsStringFood.indexOf('"') + 1, commandsStringFood.lastIndexOf('"'));
                String beforeNameFood = commandsStringFood.substring(0, commandsStringFood.indexOf('"')).trim();
                String afterName = commandsStringFood.substring(commandsStringFood.lastIndexOf('"') + 1).trim();
                String[] beforeNameParts = beforeNameFood.split(" ");
                String[] afterNameParts = afterName.split(" ");
                String dateStringFood = afterNameParts[1];

                LocalDate dateFood = LocalDate.parse(dateStringFood);
                LocalDateTime eventTimeFood = dateFood.atStartOfDay();
                double priceFood = Double.parseDouble(afterNameParts[0]);
                int personNumberFood = Integer.parseInt(afterNameParts[2]);

                if(beforeNameParts[2] == "GENERATE") {
                    EventFood product = new EventFood(nameFood,eventTimeFood,priceFood,personNumberFood);
                    String idFood = product.getId();
                    ProductHM.add(idFood,product);
                }
                else {
                    String idFood = beforeNameParts[2];
                    EventFood product = new EventFood(idFood,nameFood,eventTimeFood,priceFood,personNumberFood);
                    ProductHM.add(idFood,product);
                }
                break;

            case "ADDMEETING":
                
                String commandsStringMeeting = String.join(" ", commands);
                String nameMeeting = commandsStringMeeting.substring(commandsStringMeeting.indexOf('"') + 1, commandsStringMeeting.lastIndexOf('"'));
                String beforeName = commandsStringMeeting.substring(0, commandsStringMeeting.indexOf('"')).trim();
                String afterNameMeeting = commandsStringMeeting.substring(commandsStringMeeting.lastIndexOf('"') + 1).trim();
                String[] beforeNamePartsMeeting = beforeName.split(" ");
                String[] afterNamePartsMeeting = afterNameMeeting.split(" ");
                String dateStringMeeting = afterNamePartsMeeting[1];

                LocalDate dateMeeting = LocalDate.parse(dateStringMeeting);
                LocalDateTime eventTimeMeeting = dateMeeting.atStartOfDay();
                double priceMeeting = Double.parseDouble(afterNamePartsMeeting[0]);
                int personNumberMeeting = Integer.parseInt(afterNamePartsMeeting[2]);

                if(beforeNamePartsMeeting[2] == "GENERATE") {
                    EventReunion product = new EventReunion(nameMeeting,eventTimeMeeting,priceMeeting,personNumberMeeting);
                    String idFood = product.getId();
                    ProductHM.add(idFood,product);
                }
                else {
                    String idFood = beforeNamePartsMeeting[2];
                    EventReunion product = new EventReunion(idFood,nameMeeting,eventTimeMeeting,priceMeeting,personNumberMeeting);
                    ProductHM.add(idFood,product);
                }
                break;

            case "LIST":
                ProductHM.list();
                break;
            case "UPDATE":
                String id2 = (commands[2]);
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
                String id3 =(commands[2]);
                ProductHM.remove(id3);
                break;
            default:
                System.out.println("ERROR: Invalid input");
                break;
        }
    }

    public static String[] processProdAdd(String command) {
        String name = command.substring(command.indexOf('"') + 1, command.lastIndexOf('"'));
        String beforeName = command.substring(0, command.indexOf('"')).trim();
        String afterName = command.substring(command.lastIndexOf('"') + 1).trim();
        String[] beforeNameParts = beforeName.split(" ");
        String[] afterNameParts = afterName.split(" ");
        boolean hasId = (beforeNameParts.length == 3);
        boolean hasMaxPers = (afterNameParts.length == 3);
        int size = 0;
        if (!hasId && !hasMaxPers) {
            size = 6;
        }
        if(hasId && hasMaxPers) {
            size = 7;
        }
        if(!hasId && hasMaxPers)
        {
            size = 7;
        }
        if(hasId && !hasMaxPers){
            size = 6;
        }

        String[] result = new String[size];
        result[0] = "Prodd";
        result[1] = "add";
        if(hasId){result[2] = beforeNameParts[2];}
        else{result[2] = "GENERATE";
        }
        result[3] = name;
        result[4] = afterNameParts[0]; // category
        result[5] = afterNameParts[1]; //price

        if(hasMaxPers){result[6] = afterNameParts[2];} //maxPers si tiene
        return result;
    }

    public void prodUpdateManage(String id, String CategoryToChange, String change) {
        AbstractProduct product = ProductHM.getProduct(id);
        if (product == null) {
            System.out.println("ERROR: Product with id " + id + " does not exist!");
            return;
        }
        switch (CategoryToChange) {
            case "NAME":
                product.setName(change);
                ProductHM.update(id, product);
                System.out.println(product);
                System.out.println("product update: ok");
                break;
            case "PRICE":
                try{
                    double newPrice = Double.parseDouble(change);
                    if(newPrice <= 0) {
                        System.out.println("ERROR: Invalid input");
                        return;
                    }
                    if(product.setPrice(newPrice)) { //setPrice booleanos, da positivo si la clase permite el set(tiene precio)
                        ProductHM.update(id, product);
                        System.out.println(product);
                        System.out.println("product update: ok");
                    }else{
                        System.out.println("ERROR: Invalid input");
                    }
                }catch(NumberFormatException e){
                    System.out.println("ERROR: Invalid input");
                }
                break;
            case "CATEGORY":
                if(product.hasCategory()){   //No todos los productos guardables tiene Category ( por ello este if)
                    try {
                        Category newCategory = Category.valueOf(change.toUpperCase());
                        if(product.setCategory(newCategory)) {
                            ProductHM.update(id, product);
                            System.out.println(product);
                            System.out.println("product update: ok");
                        }
                    }catch(IllegalArgumentException e){
                        System.out.println("ERROR: Invalid input");
                    }
                }else{
                    System.out.println("ERROR: Invalid input");
                }
                break;
            default:
                System.out.println("ERROR: Invalid input");
                return;
        }
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