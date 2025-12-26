package es.upm.etsisi.poo.utils;

import es.upm.etsisi.poo.model.products.Category;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

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
        } catch (NoSuchElementException | NumberFormatException | IndexOutOfBoundsException e) {
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
                if(maxPeople <= 0  || maxPeople>= 0){return false;}
                return true;
            }
            return false;
        }catch (NoSuchElementException | NumberFormatException | IndexOutOfBoundsException e) {
            return false;
        }
    }

    private static boolean validateProdUpdate(String[] splittedCommand, String fullCommand) {
        if (splittedCommand.length < 4) {
            return false;
        }
        if (!validProductID(splittedCommand[2])) {
            return false;
        }

        switch (splittedCommand[3]) {
            case "NAME": {  //  Acepta nombres con espacios cuando están entre comillas: prod update <id> NAME "nuevo nombre"
                int first = fullCommand.indexOf('"');
                int last = fullCommand.lastIndexOf('"');
                if (first == -1 || last <= first) {
                    return false;
                }
                String nameValue = fullCommand.substring(first + 1, last);
                return isName(nameValue);
            }
            case "PRICE":
                if (splittedCommand.length != 5) {
                    return false;
                }
                return isDouble(splittedCommand[4]);
            case "CATEGORY":
                if (splittedCommand.length != 5) {
                    return false;
                }
                return isCategory(splittedCommand[4]);
            default:
                return false;
        }
    }

    private static boolean prodCommandVerification(String[] splittedCommand, String fullCommand) {
        switch (splittedCommand[1]) {
            case "ADD":
                return validateProdAdd(fullCommand);
            case "ADDFOOD":
                return validateProdAddEvent(fullCommand);
            case "ADDMEETING":
                return validateProdAddEvent(fullCommand);
            case "UPDATE":
                return validateProdUpdate(splittedCommand, fullCommand);
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
    }

    private static boolean clientCommandVerification(String[] splittedCommand) {
        switch (splittedCommand[1]) {
            case "ADD":
                if (splittedCommand.length < 6) {
                    return false;
                }
                String commandString = String.join(" ", splittedCommand);
                if (commandString.split("\"").length < 3) {
                    return false;
                }
                String name = commandString.substring(commandString.indexOf('"') + 1, commandString.lastIndexOf('"'));
                String substringAfterName = commandString.substring(commandString.lastIndexOf('"') + 1).trim();
                String[] subarray = substringAfterName.split(" ");
                if (subarray.length != 3) {
                    return false;
                }
                return isName(name) && isDNI(subarray[0]) && isEmail(subarray[1]) && isCashID(subarray[2]);
            case "REMOVE":
                if (splittedCommand.length != 3) {
                    return false;
                }
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
                if (splittedCommand.length == 4) {
                    return isCashID(splittedCommand[2]) && isDNI(splittedCommand[3]);
                }
                if (splittedCommand.length == 5) {
                    return isTicketID(splittedCommand[2]) && isCashID(splittedCommand[3]) && isDNI(splittedCommand[4]);
                }
                return false;
            case "ADD":
                if (splittedCommand.length < 6) return false;
                return isTicketID(splittedCommand[2])
                        && isCashID(splittedCommand[3])
                        && validProductID(splittedCommand[4])
                        && isInteger(splittedCommand[5]);
            case "REMOVE":
                if (splittedCommand.length == 5) {
                    return isTicketID(splittedCommand[2])
                            && isCashID(splittedCommand[3])
                            && validProductID(splittedCommand[4]);
                }
                return false;
            case "PRINT":
                if (splittedCommand.length == 4) {
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

    private static String[] localProcessProdAdd(String command) {
        String name = command.substring(command.indexOf('"') + 1, command.lastIndexOf('"'));
        String beforeName = command.substring(0, command.indexOf('"')).trim();
        String afterName = command.substring(command.lastIndexOf('"') + 1).trim();
        String[] beforeNameParts = beforeName.split(" ");
        String[] afterNameParts = afterName.split(" ");
        boolean hasId = (beforeNameParts.length == 3);
        boolean hasMaxPers = (afterNameParts.length == 3);
        int size = 0;
        if (!hasId && !hasMaxPers) {
            size = 6;
        }
        if (hasId && hasMaxPers) {
            size = 7;
        }
        if (!hasId && hasMaxPers) {
            size = 7;
        }
        if (hasId && !hasMaxPers) {
            size = 6;
        }

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
        result[5] = afterNameParts[1]; // price

        if (hasMaxPers) {
            result[6] = afterNameParts[2];
        } // maxPers
        return result;
    }


    // Combierte cualquier comando que tenga "" en un array con cada elemento
    private static String[] procesQuoteCommands(String command) {
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


    // public static boolean validCustomCommand (String[] customCommand) {
        // for (int i = 6; i < customCommand.length; i++) {
           // if (customCommand[i].equals("--p")) {
               // if (!(i + 1 < customCommand.length)) {
                   // return false;
               // }
               // i++;
           // } else if (!(customCommand[i].length() > 3)) {
               // return false;
           // } else if (!(customCommand[i].charAt(0) == '-' && customCommand[i].charAt(1) == '-'
                   // && customCommand[i].charAt(2) == 'p')) {
               // return false;
           // }
       // }
       // return true;
   // }