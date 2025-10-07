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
                case "print" -> { Renderer.print(board); }
                case "set" -> {
                    String[] parts = in.nextLine().trim().split("\\s+");
                    if(parts.length < 3) {
                        System.out.println("Set needs 3 parts: row, column, and value (e.g., set 1 3 5).");
                        break;
                    }
                    try {
                        int r = Integer.parseInt(parts[0]) - 1;
                        int c = Integer.parseInt(parts[1]) - 1;
                        int v = Integer.parseInt(parts[2]);
                        if(board.isValidPlacement(r, c, v)){
                            board.cell(r, c).setValue(v);
                        }else{
                            System.out.println("Invalid Placement");
                        }
                        Renderer.print(board);
                    } catch (NumberFormatException ex) {
                        System.out.println("Set uses intergers: set row column value (e.g., set 1 3 5)");
                    }
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