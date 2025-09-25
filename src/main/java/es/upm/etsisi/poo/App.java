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

    private String typeCommand() {  // To make easier the implementation of the shell
        Scanner sc = new Scanner(System.in);
        System.out.print("tUpm> ");
        String command = sc.nextLine();
        sc.close();
        return command;

    }
}
