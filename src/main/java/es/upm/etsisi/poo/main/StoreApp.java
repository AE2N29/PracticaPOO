package es.upm.etsisi.poo.main;

import es.upm.etsisi.poo.model.products.*;
import es.upm.etsisi.poo.model.sales.Ticket;
import es.upm.etsisi.poo.model.users.Cashier;
import es.upm.etsisi.poo.model.users.Client;
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

    public static void main( String[] args ) {
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
                            prodCommands(commandParts);
                            break;
                        case "TICKET":
                            ticketCommands(commandParts, command); // Pasamos command completo para parseo
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
                        case "CLIENT":
                            clientCommands(commandParts);
                            break;
                        case "CASH":
                            cashierCommands(commandParts);
                            break;
                        default:
                            System.out.println(StaticMessages.NOT_VALID_CMD);
                    }
                } catch (Exception e) {
                    System.out.println(StaticMessages.ERROR_PROCESSING + " ->" + commandParts[0].toLowerCase() + " " + commandParts[1].toLowerCase() + " ->" + e.getMessage());
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
                    PersonalizedProduct product = new PersonalizedProduct(id1, name, category, price, maxPers);
                    ProductCatalog.add(id1, product);
                }
                else {
                    StockProduct product = new StockProduct(id1, name, category, price);
                    ProductCatalog.add(id1, product);
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

                if("GENERATE".equals(beforeNameParts[2])) {
                    Event product = new Event(nameFood,eventTimeFood,priceFood,personNumberFood,EventType.FOOD);
                    String idFood = product.getId();
                    ProductCatalog.add(idFood,product);
                }
                else {
                    String idFood = beforeNameParts[2];
                    Event product = new Event(idFood,nameFood,eventTimeFood,priceFood,personNumberFood, EventType.FOOD);
                    ProductCatalog.add(idFood,product);
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

                if("GENERATE".equals(beforeNamePartsMeeting[2])) {
                    Event product = new Event(nameMeeting,eventTimeMeeting,priceMeeting,personNumberMeeting, EventType.MEETING);
                    String idFood = product.getId();
                    ProductCatalog.add(idFood,product);
                }
                else {
                    String idFood = beforeNamePartsMeeting[2];
                    Event product = new Event(idFood,nameMeeting,eventTimeMeeting,priceMeeting,personNumberMeeting,  EventType.MEETING);
                    ProductCatalog.add(idFood,product);
                }
                break;

            case "LIST":
                ProductCatalog.list();
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
                ProductCatalog.remove(id3);
                break;
            default:
                System.out.println(StaticMessages.INVALID_INPUT);
                break;
        }
    }

    public String[] processProdAdd(String command) {
        String name = command.substring(command.indexOf('"') + 1, command.lastIndexOf('"'));
        String beforeName = command.substring(0, command.indexOf('"')).trim();
        String afterName = command.substring(command.lastIndexOf('"') + 1).trim();
        String[] beforeNameParts = beforeName.split(" ");
        String[] afterNameParts = afterName.split(" ");
        boolean hasId = (beforeNameParts.length == 3);
        boolean hasMaxPers = (afterNameParts.length == 3);
        int size = 0;
        if (!hasId && !hasMaxPers) size = 6;
        if (hasId && hasMaxPers) size = 7;
        if (!hasId && hasMaxPers) size = 7;
        if (hasId && !hasMaxPers) size = 6;

        String[] result = new String[size];
        result[0] = "Prodd";
        result[1] = "add";
        if (hasId) {
            result[2] = beforeNameParts[2];
        } else {
            result[2] = "GENERATE";
        }
        result[3] = name;
        result[4] = afterNameParts[0]; // category
        result[5] = afterNameParts[1]; //price

        if (hasMaxPers) { //maxPers si tiene
            result[6] = afterNameParts[2];
        }
        return result;
    }

    public void prodUpdateManage(String id, String CategoryToChange, String change) {
        AbstractProduct product = ProductCatalog.getProduct(id);
        if (product == null) {
            System.out.println(StaticMessages.PROD_NO_EXIST);
            return;
        }
        switch (CategoryToChange) {
            case "NAME":
                product.setName(change);
                ProductCatalog.update(id, product);
                break;
            case "PRICE":
                try {
                    double newPrice = Double.parseDouble(change);
                    if (newPrice <= 0) {
                        System.out.println(StaticMessages.INVALID_INPUT);
                        return;
                    }
                    if (product.setPrice(newPrice)) {//setPrice booleanos, da positivo si la clase permite el set(tiene precio)
                        ProductCatalog.update(id, product);
                    } else {
                        System.out.println(StaticMessages.INVALID_INPUT);
                    }
                } catch (NumberFormatException e) {
                    System.out.println(StaticMessages.INVALID_INPUT);
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
                        System.out.println(StaticMessages.INVALID_INPUT);
                    }
                } else {
                    System.out.println(StaticMessages.INVALID_INPUT);
                }
                break;
            default:
                System.out.println(StaticMessages.INVALID_INPUT);
                break;
        }
    }

    public void ticketCommands(String[] commands, String fullCommand) {
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
                Cashier cashier = UserDatabase.getInstance().getById(inputCashId, Cashier.class);
                Client client = UserDatabase.getInstance().getById(inputUserId, Client.class);
                if (cashier == null) {
                    System.out.println(StaticMessages.CASHIER_NOT_FOUND);
                    return;
                }
                if (client == null) {
                    System.out.println(StaticMessages.CLIENT_NOT_FOUND);
                    return;
                }
                Ticket newTicket;
                if (inputTicketId == null) { // cuando no pasan el id del ticket como parametro
                    newTicket = new Ticket();
                } else if (Ticket.isIdRegistered(inputTicketId)) { // cuando pasan el id del ticket como parametro
                    System.out.println(StaticMessages.TICKET_ALREADY_EXISTS);
                    return;
                } else {
                    newTicket = new Ticket(inputTicketId);
                }
                cashier.addTicket(newTicket);
                client.addTicket(newTicket);
                newTicket.printInitialState();
                System.out.println(StaticMessages.TICKET_NEW_OK);
                break;
            case "ADD":
                String ticketId = commands[2];
                String cashId = commands[3];
                String prodId = commands[4];
                int amount = Integer.parseInt(commands[5]);
                ArrayList<String> customs = new ArrayList<>();

                Cashier cash = UserDatabase.getInstance().getById(cashId, Cashier.class);
                if (cash == null) {
                    System.out.println(StaticMessages.CASHIER_NOT_FOUND);
                    return;
                }
                Ticket t = cash.getTicketById(ticketId);
                if (t == null) {
                    System.out.println(StaticMessages.TICKET_NOT_FOUND);
                    return;
                }

                if (fullCommand.toLowerCase().contains("--p")) {
                    String tail = fullCommand.substring(fullCommand.toLowerCase().indexOf("--p"));
                    String[] parts = tail.split("(?i)--p"); // Split insensible a mayúsculas
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
                t.add(prodId, amount, customs);
                break;
            case "REMOVE":
                String tId = commands[2];
                String cId = commands[3];
                String pId = commands[4];
                Cashier c = UserDatabase.getInstance().getById(cId, Cashier.class);
                if (c == null) {
                    System.out.println(StaticMessages.CASHIER_NOT_FOUND);
                    return;
                }
                Ticket ticket = c.getTicketById(tId);
                if (ticket == null) {
                    System.out.println(StaticMessages.TICKET_NOT_FOUND);
                    return;
                }
                ticket.remove(pId);
                break;
            case "PRINT":
                String idTicket = commands[2];
                String idCash = commands[3];
                Cashier cashierC = UserDatabase.getInstance().getById(idCash, Cashier.class);
                if (cashierC == null) {
                    System.out.println(StaticMessages.CASHIER_NOT_FOUND);
                    return;
                }
                Ticket ticketT = cashierC.getTicketById(idTicket);
                if (ticketT == null) {
                    System.out.println(StaticMessages.TICKET_NOT_FOUND);
                    return;
                }
                ticketT.print();
                break;
            case "LIST":
                System.out.println(StaticMessages.TICKET_LIST);
                ArrayList<Cashier> sortedCashiers = UserDatabase.getInstance().getAll(Cashier.class);
                for (Cashier cashierc : sortedCashiers) {
                    ArrayList<Ticket> tickets = cashierc.getTicketsSortedById();
                    for (Ticket tickett : tickets) {
                        System.out.println("  " + tickett.getId() + " - " + tickett.getState());
                    }
                }
                System.out.println(StaticMessages.TICKET_LIST_OK);
                break;
            default:
                System.out.println(StaticMessages.INVALID_INPUT);
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
                    correctCommand[4] = commands[commands.length - 1];
                } else {
                    correctCommand[2] = name;
                    correctCommand[3] = commands[commands.length - 1];
                }
                if (countNotNull(correctCommand) == 5) {
                    UserDatabase.getInstance().add(new Cashier(correctCommand[2], name, correctCommand[4]));
                } else if (countNotNull(correctCommand) == 4) {
                    UserDatabase.getInstance().add(new Cashier(UserDatabase.getInstance().generateCashId(), correctCommand[2], correctCommand[3]));
                } else {
                    System.out.println(StaticMessages.INVALID_INPUT);
                }
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
                System.out.println(StaticMessages.NOT_VALID_CMD);
                break;
        }
    }

    public void clientCommands(String[] commands) {
        switch (commands[1].toUpperCase()) {
            case "ADD":
                String stringedCommand = String.join(" ", commands);
                String name = stringedCommand.substring(stringedCommand.indexOf('"') + 1, stringedCommand.lastIndexOf('"'));
                String restOfCommand = stringedCommand.substring(stringedCommand.lastIndexOf('"') + 1).trim();
                String[] afterName = restOfCommand.split(" ");

                Cashier c = UserDatabase.getInstance().getById(afterName[2], Cashier.class);
                Client nuevoCliente = ClientFactory.createClient(name, afterName[0], afterName[1], c); // uso de Factory Method
                UserDatabase.getInstance().add(nuevoCliente);
                break;
            case "REMOVE":
                UserDatabase.getInstance().remove(commands[2]);
                break;
            case "LIST":
                UserDatabase.getInstance().listClients();
                break;
            default:
                System.out.println(StaticMessages.INVALID_INPUT);
        }
    }

    private void help() {
        System.out.print(StaticMessages.HELP_TEXT);
    }

    private String typeCommand() {
        String command;
        try {
            if (fromKeyboard) {
                System.out.print("tUPM> ");
                command = sc.nextLine();
                System.out.println(command);
            } else {
                if (!sc.hasNextLine()) {
                    return "EXIT";
                }
                command = sc.nextLine();
                System.out.println("tUPM> " + command);
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