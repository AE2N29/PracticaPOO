package es.upm.etsisi.poo.Command;

import es.upm.etsisi.poo.model.sales.TicketAccessType;

public enum CommandType {
    // Comandos de producto
    PROD_ADD("PROD", "ADD", "Add a new product to catalog"),
    PROD_ADDFOOD("PROD", "ADDFOOD", "Add a food event product"),
    PROD_ADDMEETING("PROD", "ADDMEETING", "Add a meeting event product"),
    PROD_UPDATE("PROD", "UPDATE", "Update product details"),
    PROD_REMOVE("PROD", "REMOVE", "Remove product from catalog"),
    PROD_LIST("PROD", "LIST", "List all products"),

    // Comandos de ticket
    TICKET_NEW_INDIVIDUAL("TICKET", "NEW", "Create individual ticket", TicketAccessType.INDIVIDUAL),
    TICKET_NEW_CORPORATE("TICKET", "NEW", "Create corporate ticket", TicketAccessType.CORPORATE),
    TICKET_NEW_COMBINED("TICKET", "NEW", "Create combined ticket", TicketAccessType.COMBINED),
    TICKET_ADD("TICKET", "ADD", "Add product to ticket", null),
    TICKET_REMOVE("TICKET", "REMOVE", "Remove product from ticket", null),
    TICKET_PRINT("TICKET", "PRINT", "Print ticket receipt", null),
    TICKET_LIST("TICKET", "LIST", "List all tickets", null),

    // Comandos de cashier
    CASH_ADD("CASH", "ADD", "Register new cashier"),
    CASH_REMOVE("CASH", "REMOVE", "Remove cashier"),
    CASH_LIST("CASH", "LIST", "List all cashiers"),
    CASH_TICKETS("CASH", "TICKETS", "Show cashier tickets"),

    // Comandos de cliente
    CLIENT_ADD("CLIENT", "ADD", "Register new client"),
    CLIENT_REMOVE("CLIENT", "REMOVE", "Remove client"),
    CLIENT_LIST("CLIENT", "LIST", "List all clients"),

    // Comandos globales
    HELP("HELP", "", "Display help information"),
    ECHO("ECHO", "", "Display text message"),
    EXIT("EXIT", "", "Exit application");

    private final String type;
    private final String subType;
    private final String description;
    private final TicketAccessType accessType;


     // Constructor para comandos sin tipo de acceso a ticket

    CommandType(String type, String subType, String description) {
        this(type, subType, description, null);
    }

    //Contructor para comandos con tipo de acceso
    CommandType(String type, String subType, String description, TicketAccessType accessType) {
        this.type = type;
        this.subType = subType;
        this.description = description;
        this.accessType = accessType;
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getDescription() {
        return description;
    }

    public TicketAccessType getAccessType() {
        return accessType;
    }

    /**
     * Obtiene un CommandType basado en tipo y subtipo
     * @param type Tipo de comando principal (ej: "PROD", "TICKET")
     * @param subType Tipo de subcomando (ej: "ADD", "REMOVE")
     * @return CommandType si se encuentra, null en caso contrario
     */
    public static CommandType getCommand(String type, String subType) {
        for (CommandType cmd : CommandType.values()) {
            if (cmd.type.equalsIgnoreCase(type) &&
                    cmd.subType.equalsIgnoreCase(subType)) {
                return cmd;
            }
        }
        return null;
    }


      //Verifica si un comando existe en el sistema
      //param type Tipo de comando principal
      //param subType Tipo de subcomando
      //return true si el comando existe, false en caso contrario

    public static boolean exists(String type, String subType) {
        return getCommand(type, subType) != null;
    }


     // Obtiene todos los comandos de un tipo específico
      //@param type Tipo de comando principal
     // @return Array de CommandType para ese tipo

    public static CommandType[] getCommandsByType(String type) {
        return java.util.Arrays.stream(CommandType.values())
                .filter(cmd -> cmd.type.equalsIgnoreCase(type))
                .toArray(CommandType[]::new);
    }


       //Retorna una cadena de ayuda formateada para un comando

    public String getHelpString() {
        if (subType. isEmpty()) {
            return String.format("%-15s - %s", type, description);
        }
        return String.format("%-15s %-12s - %s", type, subType, description);
    }

    @Override
    public String toString() {
        return String.format("[%s %s] %s", type, subType, description);
    }
}