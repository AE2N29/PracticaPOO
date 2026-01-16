package es.upm.etsisi.poo.utils;

import es.upm.etsisi.poo.model.products.Category;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InputValidator {
    private static final String LETRAS_DNI = "TRWAGMYFPDXBNJZSQVHLCKE";

    public static boolean validCommand(String command) {
        String upperCaseCommand = command.toUpperCase();
        if ("HELP".equals(upperCaseCommand) || "EXIT".equals(upperCaseCommand)) {
            return true;
        }
        String[] splittedCommand = upperCaseCommand.split(" ");
        if (splittedCommand.length < 2) {
            return false;
        }
        switch (splittedCommand[0]) {
            case "PROD":
                return prodCommandVerification(splittedCommand, command);
            case "TICKET":
                return ticketCommandVerification(command);
            case "ECHO":
                return true;
            case "CLIENT":
                return clientCommandVerification(command);
            case "CASH":
                return cashierCommandVerification(command);
            default:
                return false;
        }
    }

    //prod add ....
    //prod update ...
    //prod remove ...
    //prod list
    private static boolean prodCommandVerification(String[] splittedCommand, String fullCommand) {
        switch (splittedCommand[1]) {
            case "ADD":
                return validateProdAdd(fullCommand);
            case "ADDFOOD":
                return validateProdAddEvent(fullCommand);
            case "ADDMEETING":
                return validateProdAddEvent(fullCommand);
            case "UPDATE":
                return validateProdUpdate(fullCommand);
            case "REMOVE":
                if (splittedCommand.length != 3) {
                    return false;
                }
                return validProductID(splittedCommand[2]);
            case "LIST":
                return splittedCommand.length == 2;
            default:
                return false;
        }
    }   //PRODUCTS

    //prod add 2 "Camiseta talla:M UPM" CLOTHES 15
    //prod add "Camiseta talla:M UPM" CLOTHES 15
    //prod add 3 "Libro POO repetido Error" BOOK 25
    //prod add 5 "Camiseta talla:M UPM" CLOTHES 15 3
    //prod add 6 "Camiseta talla:L UPM" CLOTHES 20 4
    private static boolean validateProdAdd(String fullCommand) {
        if (fullCommand.split("\"").length < 3) {return false;}
        try {
            String[] processedCommand = procesQuoteCommands(fullCommand);
            if(processedCommand.length < 5 || processedCommand.length > 7) {return false;}
            int idIndex = -1;
            int nameIndex, categoryIndex, priceIndex, maxCustomTextsIndex;
            if(isCategory(processedCommand[4])) { // tiene forma: [prod, add, ID, Name, Cat, Price, (maxPers)?]
                idIndex = 2;
                nameIndex = 3;
                categoryIndex = 4;
                priceIndex = 5;
                maxCustomTextsIndex = 6;
            }
            else if(isCategory(processedCommand[3])) {  // tiene forma: [prod, add, Name, Cat, Price, (maxPers)?]
                nameIndex = 2;
                categoryIndex = 3;
                priceIndex = 4;
                maxCustomTextsIndex = 5;
            }
            else{return false;} // La categoria solo puede estar en la posicion 3 o 4
            if(idIndex != -1 && !validProductID(processedCommand[idIndex])){return false;}
            boolean validBasicData = isName(processedCommand[nameIndex])
                    && isCategory(processedCommand[categoryIndex])
                    && isDouble(processedCommand[priceIndex])
                    && Double.parseDouble(processedCommand[priceIndex]) > 0;
            if(!validBasicData){return false;}
            if(processedCommand.length == maxCustomTextsIndex+1) {
                return isInteger(processedCommand[maxCustomTextsIndex]) && Integer.parseInt(processedCommand[maxCustomTextsIndex]) > 0;}
            else {
                return processedCommand.length == priceIndex + 1;
            }
        } catch (Exception e) {
            return false;
        }
    }

    //prod addMeeting 23456 "Reunion Rotonda" 12 2025-12-21 100
    //prod addFood 23458 "Cafeteria ETSISI" 5 2025-12-21 300
    //prod addFood "Cafeteria ETSISI" 5 2025-12-21 300
    private static boolean validateProdAddEvent(String fullCommand) {
        if (fullCommand.split("\"").length < 3) {return false;}
        try {
            String[] processedCommand = procesQuoteCommands(fullCommand);
            if(processedCommand.length < 6 || processedCommand.length > 7) {return false;}
            int idIndex = -1;
            int nameIndex, priceIndex, expirationDateIndex, maxPeopleIndex;
            if(isDouble(processedCommand[4])) { // es con id: [prod][addEvent][id][name][price][expirationDate][maxPeople]
                idIndex = 2;
                nameIndex = 3;
                priceIndex = 4;
                expirationDateIndex = 5;
                maxPeopleIndex = 6;
            }
            else if(isDouble(processedCommand[3])) { // es sin id: [prod][addEvent][name][price][expirationDate][maxPeople]
                nameIndex = 2;
                priceIndex = 3;
                expirationDateIndex = 4;
                maxPeopleIndex = 5;
            }
            else{return false;}
            if(idIndex != -1 && !validProductID(processedCommand[idIndex])){return false;}
            boolean validBasicData = isName(processedCommand[nameIndex]) && isDouble(processedCommand[priceIndex])
                    && isDate(processedCommand[expirationDateIndex]) && isInteger(processedCommand[maxPeopleIndex]);
            if(!validBasicData){return false;}
            if(processedCommand.length == maxPeopleIndex+1) {
                double price = Double.parseDouble(processedCommand[priceIndex]);
                if(price < 0) {return false;}
                int maxPeople = Integer.parseInt(processedCommand[maxPeopleIndex]);
                if(maxPeople <= 0  || maxPeople> 100){return false;}
                return true;
            }
            return false;
        }catch (Exception e) {
            return false;
        }
    }

    //prod update 1 NAME "Libro POO V2"
    //prod update 1 PRICE 30
    //prod update 1 PRICE 30
    //prod update 1 NAME "Libro POO V2"
    //prod update 1 PRICE 30
    private static boolean validateProdUpdate(String fullCommand) {
        String[] processedCommand = procesQuoteCommands(fullCommand);
        if(processedCommand.length != 5 ) {return false;}
        if (!validProductID(processedCommand[2])) {return false;}
        switch (processedCommand[3]) {
            case "NAME":  //  Acepta nombres con espacios cuando están entre comillas: prod update <id> NAME "nuevo nombre"
                return isName(processedCommand[4]);
            case "PRICE":
                return isDouble(processedCommand[4]) && Double.parseDouble(processedCommand[4]) > 0;
            case "CATEGORY":
                return isCategory(processedCommand[4]);
            default:
                return false;
        }
    }

    //cash add UW1234567 "pepecurro3" pepe0@upm.es
    //cash add "pepecurro2" pepe0@upm.es
    //cash list
    //cash remove UW1234569
    //cash tickets UW1234567
    private static boolean cashierCommandVerification(String fullCommand) {
        String[] processedCommand = procesQuoteCommands(fullCommand);
        if(processedCommand.length < 2){return false;}
        switch (processedCommand[1].toUpperCase()) {
            case "ADD":
                if(processedCommand.length == 5) {   //Con ID -> [cash, add, ID, Nombre, Email]
                    return isCashID(processedCommand[2]) &&
                            isName(processedCommand[3]) &&
                            isEmail(processedCommand[4]);
                }
                else if(processedCommand.length == 4) {  //Sin ID -> [cash, add, Nombre, Email]
                    return isName(processedCommand[2]) &&
                            isEmail(processedCommand[3]);
                }
                return false;
            case "REMOVE", "TICKETS":
                if (processedCommand.length != 3) return false;
                return isCashID(processedCommand[2]);
            case "LIST":
                return processedCommand.length == 2;
            default:
                return false;
        }
    }       //CASHIER

    //client add "Pepe3" 55630667S pepe1@upm.es UW1234567
    //client list
    //client remove Y8682724P
    private static boolean clientCommandVerification(String fullCommand) {
        String[] processedCommand = procesQuoteCommands(fullCommand);
        if(processedCommand.length < 2){return false;}
        switch (processedCommand[1].toUpperCase()) {
            case "ADD":         //[client, add, Name, DNI/NIF, Email, CashID]
                if (processedCommand.length != 6) {return false;}
                return isName(processedCommand[2])
                        && (isDNI(processedCommand[3])|| isNIF(processedCommand[3]))
                        && isEmail(processedCommand[4])
                        && isCashID(processedCommand[5]);
            case "REMOVE":      //[client, remove, DNI/NIF]
                if (processedCommand.length != 3) {return false;}
                return isDNI(processedCommand[2]) || isNIF(processedCommand[2]);
            case "LIST":
                return processedCommand.length == 2;
            default:
                return false;
        }
    }   //CLIENTS

    //ticket new UW1234567 55630667S
    //ticket new 212121 UW1234567 55630667S
    //ticket new 212129 UW1234567 B12345674 -s
    //ticket add 212121 UW1234567 1 20
    //ticket add 212129 UW1234567 1S
    //ticket add 212128 UW1234567 5 3 --pred --pblue --pgreen
    //ticket remove 212123 UW1234567 2
    //ticket list
    //ticket print 212121 UW1234567
    private static boolean ticketCommandVerification(String fullCommand) {
        String[] processedCommand = procesQuoteCommands(fullCommand);
        if(processedCommand.length < 2){return false;}
        switch (processedCommand[1].toUpperCase()) {
            case "NEW":
                if (processedCommand.length == 4) { //[ticket, new, CashID, ClientID]
                    return isCashID(processedCommand[2])
                            && (isDNI(processedCommand[3]) || isNIF(processedCommand[3]));
                }
                if (processedCommand.length == 5) { //[ticket, new, TicketID, CashID, ClientID]
                    return isTicketID(processedCommand[2])
                            && isCashID(processedCommand[3])
                            && (isDNI(processedCommand[4]) || isNIF(processedCommand[4]));
                }
                return false;
            case "ADD":      //[ticket, add, TicketID, CashID, ProdID, Cantidad, (Opcionales...)]
                if (processedCommand.length < 6) return false;
                return isTicketID(processedCommand[2])
                        && isCashID(processedCommand[3])
                        && validProductID(processedCommand[4])
                        && isInteger(processedCommand[5]) && Integer.parseInt(processedCommand[5]) > 0;
            case "REMOVE":      //[ticket, remove, TicketID, CashID, ProdID]
                if (processedCommand.length == 5) {
                    return isTicketID(processedCommand[2])
                            && isCashID(processedCommand[3])
                            && validProductID(processedCommand[4]);
                }
                return false;

            case "PRINT":        // [ticket, print, TicketID, CashID]
                if (processedCommand.length == 4) {
                    return isTicketID(processedCommand[2])
                            && isCashID(processedCommand[3]);
                }
                return false;

            case "LIST":            //[ticket, list]
                return processedCommand.length == 2;
            default:
                return false;
        }
    }   //TICKETS

    public static boolean isInteger(String possibleInteger) {
        try {
            Integer.parseInt(possibleInteger);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDouble(String possibleDouble) {
        try {
            Double.parseDouble(possibleDouble);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isCategory(String possibleCategory) {
        try {
            Category.valueOf(possibleCategory);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public static boolean isName(String possibleName) { // false en casos como: isName(null), isName("   "), is name ("")
        if (possibleName == null) {
            return false;
        }
        String trimmed = possibleName.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.length() > 2;
        }
        return !trimmed.isEmpty();
    }

    public static boolean isDNI(String dni) {
        if (dni == null || dni.length() != 9) {
            return false;
        }
        char letter = dni.charAt(8);
        if (!Character.isLetter(letter)) {
            return false;
        }

        String numbers = dni.substring(0, 8);
        char firstChar = numbers.charAt(0);

        if (firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z') { // Si es NIE:sustituimos la letra inicial por su número correspondiente
            char sustitute = '0'; // Lo inicializamos al primer valor para evitar errores

            switch (firstChar) {
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

        } else if (Character.isDigit(firstChar)) {
            // Dejamos pasar a la validacion al posible DNI
        } else {
            return false;
        }
        if (!isInteger(numbers)) {
            return false;
        }
        int number = Integer.parseInt(numbers);
        int rest = number % 23;

        return LETRAS_DNI.charAt(rest) == letter;  // Se verifica si la letra final es la correspondiente (si el DNI/NIE es valido)
    }

    public static boolean isNIF(String nif) {
        if (nif == null || nif.length() != 9) {
            return false;
        }
        char firstChar = nif.charAt(0);
        if (firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z') { // Letras reservadas a NIE
            return false;
        }

        return nif.matches("^[A-HJ-NP-SUVW]\\d{7}[0-9A-J]$"); // letra + 7 numeros + letra/numero
    }

    public static boolean isEmail(String email) {
        return email.toLowerCase().endsWith("@upm.es");
    }

    public static boolean isCashID(String cashId) {
        if (cashId == null) {
            return false;
        }
        if (cashId.length() != 9) {
            return false;
        }
        return cashId.startsWith("UW") && isInteger(cashId.substring(2));
    }

    public static boolean isTicketID(String ticketId) {
        return ticketId != null && !ticketId.isBlank();
    }

    public static boolean isDate(String date) {
        if (date == null) {
            return false;
        }
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeException e) {
            return false;
        }
    }

    public static boolean validProductID(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        String upperId = id.toUpperCase();
        if (upperId.equals("GENERATE")) {
            return true;
        }
        if (id.matches("^\\d+$")) {
            return true;
        } //Tambien debe de aceptar IDs solamente numericos
        String pattern = "^[A-Z]{2}\\d{7}$";  //admite formatos con dos letras mayusculas y que termine por una secuencia de 7 numeros
        return id.matches(pattern);  // acepta ( ids generados internamente)
    }

    // Combierte cualquier comando que tenga "" en un array con cada elemento
    private static String[] procesQuoteCommands(String command) {
        if(!command.contains("\"")) {
            String[] splitCommand = command.split("\\s+");
            List<String> result = new ArrayList<>();
            for (String s : splitCommand) {
                result.add(s.trim());
            }
            return result.toArray(new String[0]);
        }
        else {
            String beforeQuote = command.substring(0, command.indexOf('"')).trim();
            String afterQuote = command.substring(command.lastIndexOf('"') + 1).trim();
            String quote = command.substring(command.indexOf('"') + 1, command.lastIndexOf('"'));
            List<String> result = new ArrayList<>();
            if (!beforeQuote.isEmpty()) {
                String[] beforeQuoteParts = beforeQuote.split("\\s+"); // Cualquier espacio o más de un espacio
                for (String beforeQuotePart : beforeQuoteParts) {
                    result.add(beforeQuotePart);
                }
            }
            result.add(quote);
            if (!afterQuote.isEmpty()) {
                String[] afterQuoteParts = afterQuote.split("\\s+");
                for (String afterQuotePart : afterQuoteParts) {
                    result.add(afterQuotePart);
                }
            }
            return result.toArray(new String[0]);
        }
    }
}