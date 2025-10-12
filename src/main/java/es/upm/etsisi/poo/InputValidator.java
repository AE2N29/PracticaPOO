package es.upm.etsisi.poo;

public class InputValidator {
    public static boolean validCommand(String command) {
        String upperCaseCommand = command.toUpperCase();
        if("HELP".equals(upperCaseCommand) || "EXIT".equals(upperCaseCommand)) { return true; }

        String[] splittedCommand = upperCaseCommand.split(" ");
        if (splittedCommand.length < 2) { return false; }
        switch (splittedCommand[0]) {
            case "PROD":
                return prodCommandVerification(splittedCommand);
            case "TICKET":
                return ticketCommandVerification(splittedCommand);
            case "ECHO":
                return true;
            default:
                return false;
        }
    }

    private static boolean prodCommandVerification(String[] splittedCommand) {
        switch (splittedCommand[1]) {
            case "ADD":
                if (splittedCommand.length < 6) {return false;}
                String fullCommand = String.join(" ", splittedCommand);  // Obtiene el comando original (String)
                if (fullCommand.split("\"").length < 3) { return false; }  // Verifica el uso correcto de las comillas "<name>" antes
                                                                                 // de llamar al metodo AppHM.processProdAdd() para evitar excepciones
                String[] commandPartsAdd = AppHM.processProdAdd(fullCommand);
                if (commandPartsAdd.length != 6 || Integer.parseInt(commandPartsAdd[5]) == 0) { return false; }
                return (isInteger(commandPartsAdd[2]) && isName(commandPartsAdd[3]) && isCategory(commandPartsAdd[4]) && isDouble(commandPartsAdd[5]));
            case "UPDATE":
                if (splittedCommand.length < 4) {
                    return false;
                }
                if (!isInteger(splittedCommand[2])) { return false; }
                String fullUpdate = String.join(" ", splittedCommand);
                switch (splittedCommand[3]) {
                    case "NAME": {
                        //  Acepta nombres con espacios cuando están entre comillas: prod update <id> NAME "nuevo nombre"
                        int first = fullUpdate.indexOf('"');
                        int last = fullUpdate.lastIndexOf('"');
                        if (first == -1 || last <= first) { return false; }
                        String nameValue = fullUpdate.substring(first + 1, last);
                        return isName(nameValue);
                    }
                    case "PRICE":
                        if (splittedCommand.length != 5) { return false; }
                        return isDouble(splittedCommand[4]);
                    case "CATEGORY":
                        if (splittedCommand.length != 5) { return false; }
                        return isCategory(splittedCommand[4]);
                    default:
                        return false;
                }


            case "REMOVE":
                if (splittedCommand.length != 3) { return false; }
                return isInteger(splittedCommand[2]);
            case "LIST":
                if (splittedCommand.length == 2){ return true; }
                return false;
            default:
                return false;
        }
    }

    private static boolean ticketCommandVerification(String[] splittedCommand) {
        switch (splittedCommand[1]) {
            case "NEW":
                if (splittedCommand.length == 2) { return true; }
                return false;
            case "ADD":
                if(splittedCommand.length != 4) { return false; }
                return isInteger(splittedCommand[2]) && isInteger(splittedCommand[3]);
            case "REMOVE":
                if(splittedCommand.length != 3) { return false; }
                return isInteger(splittedCommand[2]);
            case "PRINT":
                return splittedCommand.length == 2;
            default:
                return false;
        }
    }


    public static boolean isInteger(String possibleInteger) {
        try {
            Integer.parseInt(possibleInteger);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    public static boolean isDouble(String possibleDouble) {
        try {
            Double.parseDouble(possibleDouble);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
    public static boolean isCategory(String possibleCategory) {
        try {
            Category category = Category.valueOf(possibleCategory);
            return true;
        } catch(IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
    public static boolean isName(String possibleName) { // false en casos como: isName(null), isName("   "), is name ("")
        if(possibleName == null) {
            return false;
        }
        String trimmed = possibleName.trim();
        if (trimmed.length() == 0) { return false; }
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.length() > 2;
        }
        return trimmed.length() > 0;
    }
}
