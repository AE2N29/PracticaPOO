package es.upm.etsisi.poo;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
    public static void main( String[] args ) {
        App app = new App();
        app.init();

    }

    private void init() {
        System.out.println("Welcome to the ticket module App. Type 'help' to see commands");
    }

    private void start() {
        while (true) {
            String command = typeCommand();
        }
    }

    private void end() {
        System.out.println("Closing application.");
        System.out.println("Goodbye!");
    }

    public void help() {
        System.out.println("Commands:");
        System.out.println(" prod add <id> \"<name>\" <category> <price>");
        System.out.println(" prod list");
        System.out.println(" prod update <id> NAME|CATEGORY|PRICE <value>");
        System.out.println(" prod remove <id>");
        System.out.println(" ticket new");
        System.out.println(" ticket add <prodId> <quantity>");
        System.out.println(" ticket remove <prodId>");
        System.out.println(" ticket print");
        System.out.println(" echo \"<texto>\"");
        System.out.println(" help");
        System.out.println(" exit");
        System.out.println("");
        System.out.println("Categories: MERCH, STATIONERY, CLOTHES, BOOK, ELECTRONICS");
        System.out.println("Discounts if there are ≥2 units in the category: MERCH 0%, STATIONERY 5%, CLOTHES 7%, BOOK 10%,");
        System.out.println("ELECTRONICS 3%.");
    }

    private String typeCommand() {  // To make easier the implementation of the shell
        Scanner sc = new Scanner(System.in);
        System.out.print("tUpm> ");
        String command = sc.nextLine();
        sc.close();
        return command;

    }
}
