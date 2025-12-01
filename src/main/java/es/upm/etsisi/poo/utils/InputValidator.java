package es.upm.etsisi.poo.utils;

import es.upm.etsisi.poo.model.products.Category;
import es.upm.etsisi.poo.main.StoreApp;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.NoSuchElementException;

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


    //prod add [<id>] "<name>" <category> <price> [<maxPers>]
            // si tiene <maxPers> se considerara que el producto es personalizable)
    private static boolean validateProdAdd(String[] splittedCommand) {
        if (splittedCommand.length < 6) {return false;}
        String fullCommand = String.join(" ", splittedCommand);
        if(fullCommand.split("\"").length < 3){return false;}
        try{
            String[] processedCommand = StoreApp.processProdAdd(fullCommand);
            if(!validProductID(processedCommand[2])){return false;}
            if(processedCommand.length < 6 || processedCommand.length > 7 ) {
                return false;
            }
            if(processedCommand.length == 7) //caso donde tiene MAX Personas
            {
                return isName(processedCommand[3])
                        && isCategory(processedCommand[4])
                        && isDouble(processedCommand[5])
                        && Double.parseDouble(processedCommand[5]) > 0
                        && isInteger(processedCommand[6])
                        && Integer.parseInt(processedCommand[6]) > 0;
            }
            if(processedCommand.length == 6)  //caso donde NO tiene MAX Personas
            {
                return isName(processedCommand[3])
                        && isCategory(processedCommand[4])
                        && isDouble(processedCommand[5])
                        && Double.parseDouble(processedCommand[5]) > 0;
            }
            return false;
        }
        catch (NoSuchElementException | NumberFormatException e){return false;}
    }

    private static boolean validateProdAddEvent(String[] splittedCommand) {
        //prod addFood [<id>] "< name>" <price> <expiration: yyyy-MM-dd> <max_people>
        //prod addMeeting [<id>] "<name>" <price> < expiration: yyyy-MM-dd> <max_people>
        if(splittedCommand.length < 6 || splittedCommand.length > 7){return false;}
        String fullCommand = String.join(" ", splittedCommand);
        if(fullCommand.split("\"").length < 3){return false;}
        try {
            int firstQuote = fullCommand.indexOf('"');
            int lastQuote = fullCommand.lastIndexOf('"');
            if(firstQuote == -1 || lastQuote <= firstQuote)
            {return false;}
            String name = fullCommand.substring(firstQuote + 1, lastQuote);

            String beforeName = fullCommand.substring(0, firstQuote).trim();
            String[] beforeNameParts = beforeName.split(" ");

            String afterName = fullCommand.substring(lastQuote + 1).trim();
            String[] afterNameParts = afterName.split(" ");

            boolean hasId = false;
            if(splittedCommand.length == 6){
                hasId = false;
                if(beforeNameParts.length != 2 || afterNameParts.length != 3){return false;}
            }
            else if(splittedCommand.length == 7){
                hasId = true;
                if(beforeNameParts.length != 3 || afterNameParts.length != 3){return false;}
            }
            if(hasId){return validProductID(beforeNameParts[2])
                    && isName(name)
                    && isDouble(afterNameParts[0])
                    && Double.parseDouble(afterNameParts[0]) > 0
                    && isDate(afterNameParts[1])
                    && isInteger(afterNameParts[2])
                    && Integer.parseInt(afterNameParts[2]) > 0
                    && Integer.parseInt(afterNameParts[2]) <= 100;}
            else {
                return isName(name)
                    && isDouble(afterNameParts[0])
                    && Double.parseDouble(afterNameParts[0]) > 0
                    && isDate(afterNameParts[1])
                    && isInteger(afterNameParts[2])
                    && Integer.parseInt(afterNameParts[2]) > 0
                    && Integer.parseInt(afterNameParts[2]) <= 100;}
            }catch (Exception e){return false;}
    }

    private static boolean validateProdUpdate(String[] splittedCommand) {

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
    }

    private static boolean prodCommandVerification(String[] splittedCommand) {
        switch (splittedCommand[1]) {
            case "ADD":
                return validateProdAdd(splittedCommand);
            case "ADDFOOD":
                return validateProdAddEvent(splittedCommand);
            case "ADDMEETING":
                return validateProdAddEvent(splittedCommand);
            case "UPDATE":
                return validateProdUpdate(splittedCommand);
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
            case "NEW":
                if(splittedCommand.length == 4){
                    return isCashID(splittedCommand[2]) && isDNI(splittedCommand[3]);
                }
                if(splittedCommand.length == 5){
                    return isTicketID(splittedCommand[2]) && isCashID(splittedCommand[3]) && isDNI(splittedCommand[4]);
                }
                return false;
            case "ADD":
                if (splittedCommand.length == 6) {
                    return isTicketID(splittedCommand[2])
                            && isCashID(splittedCommand[3])
                            && validProductID(splittedCommand[4])
                            && isInteger(splittedCommand[5]);
                }
                if (splittedCommand.length > 6) {
                    return isTicketID(splittedCommand[2])
                            && isCashID(splittedCommand[3])
                            && validProductID(splittedCommand[4])
                            && isInteger(splittedCommand[5])
                            && validCustomCommand(splittedCommand);
                }
                return false;
            case "REMOVE":
                if(splittedCommand.length == 5) {
                    return isTicketID(splittedCommand[2])
                            && isCashID(splittedCommand[3])
                            && validProductID(splittedCommand[4]);
                }
                return false;
            case "PRINT":
                if(splittedCommand.length == 4) {
                    return isTicketID(splittedCommand[2])
                            && isCashID(splittedCommand[3]);
                }
                return false;
            case "LIST":
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

    public static boolean isTicketID(String ticketId) {
        if (ticketId == null || ticketId.length() != 20) { return false; }
        boolean separators = (ticketId.charAt(2) == '-' &&
                ticketId.charAt(5) == '-' &&
                ticketId.charAt(8) == '-' &&
                ticketId.charAt(11) == ':' &&
                ticketId.charAt(14) == '-');
        boolean datesAreNumbers = (isInteger(ticketId.substring(0, 2)) &&
                isInteger(ticketId.substring(3, 5)) &&
                isInteger(ticketId.substring(6, 8)) &&
                isInteger(ticketId.substring(9, 11)) &&
                isInteger(ticketId.substring(12, 14)));
        return separators && datesAreNumbers && isInteger(ticketId.substring(15, 20));
    }
    public static boolean isDate(String date) {
        if(date == null){return false;}
        try{
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        }catch (DateTimeException e){return false;}
    }
    public static boolean validProductID(String id) {
        if(id == null || id.trim().isEmpty()){return false;}
        String upperId = id.toUpperCase();
        if(upperId.equals("GENERATE") ){return true;}
        if(id.matches("^\\d+$")){return true;} //Tambien debe de aceptar IDs solamente numericos
        String pattern = "^[A-Z]{2}\\d{7}$";  //admite formatos con dos letras mayusculas y que termine por una secuencia de 7 numeros
        return id.matches(pattern);  // acepta ( ids generados internamente)
    }

    public static boolean validCustomCommand (String[] customCommand) {
        for (int i = 6; i < customCommand.length; i++) {
            if (customCommand[i].equals("--p")) {
                if (!(i + 1 < customCommand.length)) {
                    return false;
                }
                i++;
            } else if (!(customCommand[i].length() > 3)) {
                return false;
            } else if (!(customCommand[i].charAt(0) == '-' && customCommand[i].charAt(1) == '-'
                    && customCommand[i].charAt(2) == 'p')) {
                return false;
            }
        }
        return true;
    }
}
