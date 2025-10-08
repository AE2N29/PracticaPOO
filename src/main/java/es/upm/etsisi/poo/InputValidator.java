package es.upm.etsisi.poo;

import java.util.Arrays;

public class InputValidator {

    public static boolean validCommand(String command) {
        if (command.equals("help") || command.equals("exit")) {
            return true;
        }

        String lower = command.toLowerCase();

        if (lower.startsWith("prod add ")) {
            String[] parts = AppHM.processProdAdd(command);
            if (parts.length != 6) return false;
            return (isInteger(parts[2]) &&
                    isName(parts[3]) &&
                    isCategory(parts[4]) &&
                    isDouble(parts[5]));
        }


        String[] splittedCommand = command.split(" ");
        if(splittedCommand.length < 1){return false;}

        for(int i = 0; i < splittedCommand.length; i++) {  // bucle que recorre el array con palabras, si hay una palabra nula, el copmando es inválido
            if(splittedCommand[i] == null) {return false;}
        }

        switch (splittedCommand[0].toUpperCase()) {
            case "PROD":
                if (splittedCommand.length < 2) {return false;}
                return prodCommandVerification(splittedCommand);
            case "TICKET":
                if (splittedCommand.length < 2) {return false;}
                return ticketCommandVerification(splittedCommand);
            case "ECHO":
                if (splittedCommand.length < 2) {return false;}
                return true;
            default:
                return false;
        }
    }

    public static boolean ticketCommandVerification(String[] commands) {
        switch (commands[1].toUpperCase()) {
            case "NEW":
                if (commands.length == 2) {return true;}
                return false;
            case "ADD":
                if(commands.length != 4) {return false;}
                return isInteger(commands[2]) && isInteger(commands[3]);
            case "REMOVE":
                if(commands.length != 3) {return false;}
                return isInteger(commands[2]);
            case "PRINT":
                return commands.length == 2;
            default:
                return false;
        }
    }

    public static boolean prodCommandVerification(String[] commands) {
        switch (commands[1].toUpperCase()) {
            case "ADD":
                String fullCommand = String.join(" ", commands); // conseguimos el comando original(String)
                String[] commandPartsAdd = AppHM.processProdAdd(fullCommand);
                if (commandPartsAdd.length != 6) {return false;}
                return (isInteger(commandPartsAdd[2]) && isName(commandPartsAdd[3]) && isCategory(commandPartsAdd[4]) && isDouble(commandPartsAdd[5]));
            case "UPDATE":
                if (!isInteger(commands[2])) { return false; }
                String fieldToUpdate = commands[3].toUpperCase();
                String fullUpdate = String.join(" ", commands);
                switch (fieldToUpdate) {
                    case "NAME": {
                        // Acepta nombres con espacios entre comillas: prod update <id> NAME "nuevo nombre"
                        int first = fullUpdate.indexOf('"');
                        int last = fullUpdate.lastIndexOf('"');
                        if (first == -1 || last == -1 || last <= first) { return false; }
                        String nameValue = fullUpdate.substring(first + 1, last);
                        return isName(nameValue);
                    }
                    case "PRICE":
                        if (commands.length != 5) { return false; }
                        return isDouble(commands[4]);
                    case "CATEGORY":
                        if (commands.length != 5) { return false; }
                        return isCategory(commands[4]);
                    default:
                        return false;
                }
            case "REMOVE":
                if (commands.length != 3) {return false;}
                return isInteger(commands[2]);
            case "LIST":
                if(commands.length == 2){
                    return true;
                }
                return false;
            default:
                return false;
        }
    }

    public static boolean isInteger(String possibleInteger) {
        try {
            Integer.parseInt(possibleInteger);
            return true;
        }catch(NumberFormatException e) {
            return false;
        }
    }
    public static boolean isDouble(String possibleDouble) {
        try {
            Double.parseDouble(possibleDouble);
            return true;
        }catch(NumberFormatException e) {
            return false;
        }
    }
    public static boolean isCategory(String possibleCategory) {
        try {
            Category category = Category.valueOf(possibleCategory);
            return true;
        }catch(IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
    public static boolean isName(String possibleName) {
        if(possibleName == null) {
            return false;
        }
        String trimmed = possibleName.trim();
        // Accept names either quoted or already unquoted (processProdAdd strips quotes)
        if (trimmed.length() == 0) { return false; }
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.length() > 2; // at least one char between quotes
        }
        return trimmed.length() > 0;
    }
}
