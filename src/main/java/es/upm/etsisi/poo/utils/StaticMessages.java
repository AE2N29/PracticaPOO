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
    public static final String ADD_PROD_ERROR = "Error adding product";
    public static final String WRONG_TICKET_TYPE = "ERROR: You can´t add this to this type of ticket";

    // ENTITY ERRORS
    public static final String USER_ALREADY_EXISTS = "Error: User with ID %s already exists.";
    public static final String USER_NOT_FOUND = "Error: User with ID %s not found";
    public static final String CASHIER_NOT_FOUND = "ERROR: Cashier with given ID doesn't exist";
    public static final String CLIENT_NOT_FOUND = "ERROR: Client with the given ID doesn't exist";
    public static final String CLIENT_LIST_EMPTY = "ERROR: Client list is empty";
    public static final String CASHIER_LIST_EMPTY = "ERROR: Cashier list is empty";
    public static final String PROD_ALREADY_EXISTS = "Error: Product with ID %s already exists.";
    public static final String PROD_NO_EXIST = "ERROR: Product with the given ID doesn't exist";
    public static final String MAX_PRODS_EXCEED = "ERROR: Max products allowed per ticket exceeded!";
    public static final String PROD_UNAVAILABLE = "ERROR: Product %s is not available (expired or time restrictions).";
    public static final String PROD_NOT_IN_TICKET = "ERROR: the product with %s as ID wasn't in the current ticket";
    public static final String CAT_FULL = "ERROR: The list is full";
    public static final String TICKET_ALREADY_EXISTS = "ERROR: Ticket with the given ID already exists.";
    public static final String TICKET_NOT_FOUND = "ERROR: Ticket with the given ID doesn't exist.";
    public static final String CLOSED_TICKET = "ERROR: Ticket is already closed";
    public static final String EVENT_ALREADY_EXISTS = "ERROR: Event/Food product with given ID is already in the ticket.";
    public static final String INVALID_FOOD_TIME = "A food must be created at least 3 days in advance of its scheduled time.";
    public static final String INVALID_MEETING_TIME = "A meeting must be created at least 12 hours in advance of its scheduled time.";

    // WARNINGS
    public static final String WARN_CUSTOM_LIMIT = "WARNING: Could not add text '%s'. Max limit reached.";
    public static final String WARN_NOT_CUSTOMIZABLE = "WARNING: Product %s is not customizable. Ignoring texts.";

    // SYSTEM MESSAGES
    public static final String WELCOME = "Welcome to the ticket module App.\nTicket module. Type 'help' to see commands.";
    public static final String CLOSING_APP = "Closing application.";
    public static final String GOODBYE = "Goodbye!";
    public static final String CLIENT_ADD_OK = "client add: ok";
    public static final String CASH_ADD_OK = "cash add: ok";
    public static final String CLIENT_REMOVE_OK = "client remove: ok";
    public static final String CASH_REMOVE_OK = "cash remove: ok";
    public static final String CLIENT_HEADER = "Client:";
    public static final String CLIENT_LIST_OK = "client list: ok";
    public static final String CASH_HEADER = "Cash:";
    public static final String CASH_LIST_OK = "cash list: ok";
    public static final String PROD_ADD_OK = "prod add: ok";
    public static final String PROD_REMOVE_OK = "prod remove: ok";
    public static final String PROD_UPDATE_OK = "prod update: ok";
    public static final String PROD_LIST_OK = "prod list: ok";
    public static final String PROD_LIST_EMPTY = "There are no products in the list";
    public static final String CATALOG_HEADER = "Catalog:";
    public static final String TICKET_HEADER = "Ticket : ";
    public static final String TICKETS_HEADER = "Tickets: ";
    public static final String CASH_TICKETS_OK = "cash tickets: ok";
    public static final String TICKET_PRINT_OK = "ticket print: ok";
    public static final String TICKET_ADD_OK = "ticket add: ok";
    public static final String TICKET_REMOVE_OK = "ticket remove: ok";
    public static final String TICKET_NEW_OK = "ticket new: ok";
    public static final String TICKET_LIST = "Ticket List:";
    public static final String TICKET_LIST_OK = "ticket list: ok";
    public static final String DISCOUNT_SUFFIX = " **discount -";
    public static final String TOTAL_PRICE_LABEL = " Total price: ";
    public static final String TOTAL_DISCOUNT_LABEL = " Total discount: ";
    public static final String FINAL_PRICE_LABEL = " Final Price: ";
    public static final String INITIAL_STATE_BLOCK = """
                  Total price: 0.0
                  Total discount: 0.0
                  Final Price: 0.0
                """;

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

    // OTROS
    public static final String ERROR_PROCESSING = "Error processing";
    public static final String CORPORATE_TICKET_HEADER = "--- CORPORATE TICKET ---";
    public static final String INDIVIDUAL_TICKET_HEADER = "--- INDIVIDUAL TICKET ---";
}
