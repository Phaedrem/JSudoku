package sudoku;

import java.util.Scanner;

public class SudokuGame {

    private static final String EASY =
        "003020600" +
        "900305001" +
        "001806400" +
        "008102900" +
        "700000008" +
        "006708200" +
        "002609500" +
        "800203009" +
        "005010300";

    private static final String MEDIUM =
        "200080300" +
        "060070084" +
        "030500209" +
        "000105408" +
        "000000000" +
        "402706000" +
        "301007040" +
        "720040060" +
        "004010003";

    private static final String HARD =
        "000000907" +
        "000420180" +
        "000705026" +
        "100904000" +
        "050000040" +
        "000507009" +
        "920108000" +
        "034059000" +
        "507000000";

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Welcome to Sudoku! Choose a difficulty (easy/medium/hard): ");
        String choice = in.nextLine().trim();

        // 1. Create a board from the string
        Board board;
        if (choice.isEmpty() || choice.equalsIgnoreCase("easy")){
            board = Board.fromString(EASY);
        } else if (choice.equalsIgnoreCase("medium")) {
            board = Board.fromString(MEDIUM);
        }else if (choice.equalsIgnoreCase("hard")) {
            board = Board.fromString(HARD);
        }else if (choice.length() == 81){
            try {
                board = Board.fromString(choice);
            } catch (IllegalArgumentException iae) {
                System.out.println("Invalid puzzle string. Defaulting to EASY.");
                board = Board.fromString(EASY);
            }
        }else {
            System.out.println("Unrecognized choice. Using EASY.");
            board = Board.fromString(EASY);
        }

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
                        help                     - show this help message
                        print                    - reprint the current board
                        set r c v                - place a value (1-9) at row r, col c
                        clear r c                - clear a non-given cell at row r, col c
                        check                    - check if the puzzle is solved
                        load easy|medium|hard    - load a preset puzzle
                        load <81-char-string>    - load a custom puzzle string
                        quit                     - exit the game
                        """);
                }
                case "load" -> {
                    if(tokens.length < 2){
                        System.out.println("Load chooses a difficulty (e.g., load easy | load medium | load hard | load <81-char-puzzle>)");
                        break;
                    }
                    String arg = tokens[1].toLowerCase();

                    Board newBoard = null;
                    switch (arg) {
                        case "easy" -> newBoard = Board.fromString(EASY);
                        case "medium" -> newBoard = Board.fromString(MEDIUM);
                        case "hard" -> newBoard = Board.fromString(HARD);
                        default -> {
                            if (arg.length() == 81){
                                try {
                                    newBoard = Board.fromString(arg);
                                } catch (IllegalArgumentException iae){
                                    System.out.println("Invalid puzzle string: " + iae.getMessage());
                                    break;
                                }
                            } else {
                                System.out.println("Load chooses a difficulty (e.g., load easy | load medium | load hard | load <81-char-puzzle>)");
                                break;
                            }
                        }
                    }
                    if (newBoard != null){
                        board = newBoard;
                        Renderer.print(board);
                    }
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
                case "clear" -> {
                    if(tokens.length < 3) {
                        System.out.println("Clear needs 2 parts: row, column (e.g., clear 1 2).");
                        break;
                    }
                    try {
                        int r = Integer.parseInt(tokens[1]) - 1;
                        int c = Integer.parseInt(tokens[2]) - 1;
                        if(!board.inBounds(r, c)){
                            System.out.println("Row/Col must be 1-9");
                            break;
                        }
                        
                        if (board.cell(r,c).isGiven()){
                            System.out.println("That cell is a given and can't be changed.");
                            break; 
                        } 
                        
                        if (board.cell(r,c).getValue() == 0) {
                            System.out.println("Cell already empty.");
                            break; 
                        }

                        board.cell(r,c).clearValue();;
                        Renderer.print(board);

                    } catch (NumberFormatException ex) {
                        System.out.println("Clear uses integers: clear row column (e.g., clear 1 2)");
                    }
                }
                case "check" -> {
                    System.out.println(board.isSolved() ? "Solved!" : "Not solved.");
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