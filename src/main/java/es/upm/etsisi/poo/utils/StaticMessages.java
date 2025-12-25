package es.upm.etsisi.poo.utils;

public class StaticMessages {
    // GENERAL ERRORS
    public static final String ARGS_EXCEED = "ERROR: More arguments than expected";
    public static final String FILE_NOT_FOUND = "ERROR: File not found";
    public static final String NOT_VALID_CMD = "ERROR: Not a valid command";
    public static final String INVALID_INPUT = "ERROR: Invalid input";
    public static final String INFO_NOT_FOUND = "ERROR: Info not found";
    public static final String INVALID_NAME = "ERROR: Name is not valid";
    public static final String NEGATIVE_AMOUNT = "ERROR: Amount must be greater than 0";

    // ENTITY ERRORS
    public static final String PROD_NO_EXIST = "ERROR: Product with the given ID doesn't exist";
    public static final String CASHIER_NOT_FOUND = "ERROR: Cashier with given ID doesn't exist";
    public static final String CLIENT_NOT_FOUND = "ERROR: Client with the given ID doesn't exist";
    public static final String TICKET_ALREADY_EXISTS = "ERROR: Ticket with the given ID already exists.";
    public static final String TICKET_NOT_FOUND = "ERROR: Ticket with the given ID doesn't exist.";
    public static final String CLOSED_TICKET = "ERROR: Ticket is already closed";
    public static final String EVENT_ALREADY_EXISTS = "ERROR: Event/Food product with given ID is already in the ticket.";

    // SYSTEM MESSAGES
    public static final String WELCOME = "Welcome to the ticket module App.\nTicket module. Type 'help' to see commands.";
    public static final String CLOSING_APP = "Closing application.";
    public static final String GOODBYE = "Goodbye!";
    public static final String TICKET_NEW_OK = "ticket new: ok";
    public static final String TICKET_LIST = "Ticket List:";
    public static final String TICKET_LIST_OK = "ticket list: ok";

    // HELP BLOCK
    public static final String HELP_TEXT = """
           Commands:
             client add "<nombre>" <DNI/NIF> <email> <cashId>
             client remove <DNI>
             client list
             cash add [<id>] "<nombre>"<email>
             cash remove <id>
             cash list
             cash tickets <id>
             ticket new [<id>] <cashId> <userId> -[c/p/s]
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
             echo "<text>"
             exit
           
           Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS
           Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%, ELECTRONICS 3%.
           """;

    // ERROR PROCESSING PREFIX

    public static final String ERROR_PROCESSING = "Error processing";
}
