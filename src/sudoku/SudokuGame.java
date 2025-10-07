package sudoku;

import java.util.Scanner;

public class SudokuGame {

    public static final String EASY = 
        "003020600" +
        "900305001" +
        "001806400" +
        "008102900" +
        "700000008" +
        "006708200" +
        "002609500" +
        "800203009" +
        "005010300";

    public static void main(String[] args) {
        // 1. Create a board from the string
        Board board = Board.fromString(EASY);
        Scanner in = new Scanner(System.in);

        // 2. Print a welcome message
        System.out.println("Current board:");

        // 3. Render the board to terminal
        Renderer.print(board);

        // 4. Placeholder for looping input
        System.out.println("Type 'help' for commands");

        while (true) {
            System.out.print("> ");
            String cmd = in.nextLine().trim().toLowerCase();

            switch (cmd) {
                case "help" -> {
                    System.out.println("""
                        Commands:
                            help - Show this help message
                            quit - exit the game
                            (more commands otw)
                    """);
                }
                case "print" -> {
                    Renderer.print(board);
                }
                case "quit" -> {
                    System.out.println("Goodbye!");
                    in.close();
                    return;
                }
                case "" -> {}
                default -> System.out.println("Unknown command. Type 'help' for accepted commands.");
            }
        }

    }
}