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
            String line = in.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            String cmd = tokens[0].toLowerCase();

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
                    if(tokens.length < 4) {
                        System.out.println("Set needs 3 parts: row, column, and value (e.g., set 1 3 5).");
                        break;
                    }
                    try {
                        int r = Integer.parseInt(tokens[1]) - 1;
                        int c = Integer.parseInt(tokens[2]) - 1;
                        int v = Integer.parseInt(tokens[3]);
                        if(!board.inBounds(r, c)){
                            System.out.println("Row/Col must be 1-9");
                            break;
                        }
                        
                        if (board.cell(r,c).isGiven()){
                            System.out.println("That cell is a given.");
                            break; 
                        } 
                        
                        if (v < 1 || v > 9) {
                            System.out.println("Value must be 1-9");
                            break;
                        }
                        
                        if (board.cell(r,c).getValue() != 0) {
                            System.out.println("Cell not empty. Use: Clear " + (r+1) + " " + (c+1));
                            break; 
                        }
                        
                        if (!board.isValidPlacement(r, c, v)) {
                            System.out.println("Invalid Placement (row/column/box conflict).");
                            break;
                        }

                        board.cell(r,c).setValue(v);
                        Renderer.print(board);

                    } catch (NumberFormatException ex) {
                        System.out.println("Set uses integers: set row column value (e.g., set 1 3 5)");
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