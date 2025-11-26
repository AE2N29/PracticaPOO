package es.upm.etsisi.poo;

import java.util.ArrayList;

public class InputValidator {
    private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";

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
            case "CLIENT":
                return clientCommandVerification(splittedCommand);
            case "CASH":
                return cashierCommandVerification(splittedCommand);
            default:
                return false;
        }
    }

    private static boolean cashierCommandVerification(String[] splittedCommand) {
        switch (splittedCommand[1].toUpperCase()) {
            case "ADD":
                ArrayList<String> cmndWithNameIncluded = new ArrayList<>();
                String stringedCommand = String.join(" ", splittedCommand);
                int firstQuote = stringedCommand.indexOf('"');
                int lastQuote = stringedCommand.lastIndexOf('"');
                if (firstQuote == -1 || lastQuote == -1 || firstQuote == lastQuote) return false;
                String name = stringedCommand.substring(firstQuote + 1, lastQuote).trim();
                String email = stringedCommand.substring(lastQuote + 1).trim();
                cmndWithNameIncluded.add("CASH");
                cmndWithNameIncluded.add("ADD");
                String possibleID = splittedCommand[2];
                if (isCashID(possibleID)) {
                    cmndWithNameIncluded.add(possibleID);
                }
                cmndWithNameIncluded.add(name);
                cmndWithNameIncluded.add(email);
                if (cmndWithNameIncluded.size() == 5) {
                    return isCashID(cmndWithNameIncluded.get(2)) && isName(cmndWithNameIncluded.get(3)) && isEmail(cmndWithNameIncluded.get(4));
                } else if (cmndWithNameIncluded.size() == 4) {
                    return isName(cmndWithNameIncluded.get(2)) && isEmail(cmndWithNameIncluded.get(3));
                }
            case "REMOVE", "TICKETS":
                if (splittedCommand.length != 3) return false;
                return isCashID(splittedCommand[2]);
            case "LIST":
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
                if (commandPartsAdd[5].contains(",") || !isDouble(commandPartsAdd[5]) ||commandPartsAdd.length != 6 || Double.parseDouble(commandPartsAdd[5]) == 0) { return false; }
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
                        if (first == -1 || last <= first) { return false; } //tira -1 cuando no lo encuentra
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
                return splittedCommand.length == 2;
            default:
                return false;
        }
    }

    private static boolean clientCommandVerification(String[] splittedCommand) {
        switch (splittedCommand[1]) {
            case "ADD":
                if (splittedCommand.length < 6) {return false;}
                String commandString = String.join(" ", splittedCommand);
                if (commandString.split("\"").length < 3) {return false;}
                String name = commandString.substring(commandString.indexOf('"') + 1, commandString.lastIndexOf('"'));
                // Obtener lo que viene después del nombre
                String substringAfterName = commandString.substring(commandString.lastIndexOf('"') + 1).trim();
                String[] subarray = substringAfterName.split(" ");
                if (subarray.length != 3) {
                    return false;
                }
                return isName(name) && isDNI(subarray[0]) && isEmail(subarray[1]) && isCashID(subarray[2]);
            case "REMOVE":
                if (splittedCommand.length != 3) {return false;}
                return isDNI(splittedCommand[2]);
            case "LIST":
                return splittedCommand.length == 2;
            default:
                return false;
        }
    }

    private static boolean ticketCommandVerification(String[] splittedCommand) {
        switch (splittedCommand[1]) {
            case "NEW", "PRINT":
                return splittedCommand.length == 2;
            case "ADD":
                if(splittedCommand.length != 4) { return false; }
                return isInteger(splittedCommand[2]) && isInteger(splittedCommand[3]);
            case "REMOVE":
                if(splittedCommand.length != 3) { return false; }
                return isInteger(splittedCommand[2]);
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
            Category.valueOf(possibleCategory);
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
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.length() > 2;
        }
        return !trimmed.isEmpty();
    }

    public static boolean isDNI(String dni) {
        if (dni == null || dni.length() != 9) { return false; }
        char letter = dni.charAt(8);
        if (!Character.isLetter(letter)) { return false; }

        String numbers = dni.substring(0, 8);
        char firstChar = numbers.charAt(0);

        if (firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z') { // Si es NIE:sustituimos la letra inicial por su número correspondiente
            char sustitute = '0'; // Lo inicializamos al primer valor para evitar errores

            switch(firstChar) {
                case 'X':
                    sustitute = '0';
                    break;
                case 'Y':
                    sustitute = '1';
                    break;
                case 'Z':
                    sustitute = '2';
                    break;
            }

            numbers = sustitute + numbers.substring(1);

        } else if(Character.isDigit(firstChar)){
            // Dejamos pasar a la validacion al posible DNI
        } else {
          return false;
        }

        if(!isInteger(numbers)){ return false; }
        int number = Integer.parseInt(numbers);
        int rest = number % 23;

        return LETRAS_DNI.charAt(rest) == letter;  // Se verifica si la letra final es la correspondiente (si el DNI/NIE es valido)
    }

    public static boolean isEmail(String email) {
        return email.toLowerCase().endsWith("@upm.es");   // lo que viene despues de la arroba debe estar siempre en minusculas?
    }

    public static boolean isCashID(String cashId) {
        if (cashId == null) {return false;}
        if (cashId.length() != 9) {return false;}
        return cashId.startsWith("UW") && isInteger(cashId.substring(2));
    }
}
