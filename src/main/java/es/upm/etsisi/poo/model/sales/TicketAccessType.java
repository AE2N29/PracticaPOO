package es.upm.etsisi.poo.model.sales;


    public enum TicketAccessType {
        INDIVIDUAL("Ticket for individual clients only", "Individual", "-p"),
        CORPORATE("Ticket for corporate clients only", "Corporate", "-c"),
        COMBINED("Combined ticket (products + services, corporate only)", "Combined", "-s");

        private final String description;
        private final String shortName;
        private final String flag; //Tipo de ticket
        TicketAccessType(String description, String shortName, String flag) {
            this.description = description;
            this. shortName = shortName;
            this.flag = flag;
        }
        public static TicketAccessType fromFlag(String flag) {
            String cleanFlag = flag.startsWith("-") ? flag.substring(1) : flag;
            return switch (cleanFlag. toLowerCase()) {
                case "p", "personal" -> INDIVIDUAL;
                case "c", "corporate", "combined" -> CORPORATE;
                case "s", "service", "services" -> COMBINED;
                default -> null;
            };
        }
        public static TicketAccessType fromTypeName(String typeName) {
            for (TicketAccessType type :  TicketAccessType.values()) {
                if (type.name().equalsIgnoreCase(typeName) ||
                        type.shortName. equalsIgnoreCase(typeName)) {
                    return type;
                }
            }
            return null;
        }
        public String toString() {
            return String.format("[%s] %s (Flag: %s)", shortName, description, flag);
        }

        public String getDescription() { return description; }
        public String getShortName() { return shortName; }
    }

