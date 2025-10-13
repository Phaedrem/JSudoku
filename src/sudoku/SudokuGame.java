package sudoku;

import java.util.Scanner;

public class SudokuGame {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Welcome to Sudoku! Choose a difficulty (easy/medium/hard): ");
        String choice = in.nextLine().trim();
        String seed = Seeds.BY_NAME.get(choice.toLowerCase());

        Board board;
        if (seed == null){
            try {
                board = Board.fromString(choice);
            } catch (IllegalArgumentException iae) {
                System.out.println("Invalid puzzle string. Defaulting to EASY.");
                board = Board.fromString(Seeds.EASY);
            }
        } else {
            try {
                board = Board.fromString(seed);
            } catch (IllegalArgumentException iae) {
                System.out.println("Invalid puzzle string. Defaulting to EASY.");
                board = Board.fromString(Seeds.EASY);
            }
        }

        Solver.solveBoard(board);

        if(Solver.getNumSolutions() == 0){
            System.out.println("Warning: puzzle appears unsolvable. Disabling unique solution checks");
        }else if (Solver.getNumSolutions() > 1){
            System.out.println("Warning: puzzle appears to have multiple solutions. Disabling unique solution checks");
        }

        System.out.println("Current board:");
        Renderer.print(board);
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

                    System.out.print("This will replace your current board. Continue? (y/n): ");
                    String confirm = in.nextLine().trim().toLowerCase();
                    if (!confirm.equals("y") && !confirm.equals("yes")){
                        System.out.println("Load cancelled");
                        break;
                    }

                    choice = tokens[1].toLowerCase();
                    seed = Seeds.BY_NAME.get(choice.toLowerCase());
                    
                    if (seed == null){
                        try {
                            board = Board.fromString(choice);
                        } catch (IllegalArgumentException iae) {
                            System.out.println("Invalid puzzle string. Defaulting to EASY.");
                            board = Board.fromString(Seeds.EASY);
                        }
                    } else {
                        try {
                            board = Board.fromString(seed);
                        } catch (IllegalArgumentException iae) {
                            System.out.println("Invalid puzzle string. Defaulting to EASY.");
                            board = Board.fromString(Seeds.EASY);
                        }
                    }
                    if (board != null){
                        Solver.solveBoard(board);
                        if(Solver.getNumSolutions() == 0){
                            System.out.println("Warning: puzzle appears unsolvable. Disabling unique solution checks");
                        }else if (Solver.getNumSolutions() > 1){
                            System.out.println("Warning: puzzle appears to have multiple solutions. Disabling unique solution checks");
                        }
                        System.out.println("Current board:");
                        Renderer.print(board);
                    }
                }
                case "print" -> { Renderer.print(board); }
                case "solve" -> {
                    if(Solver.solve(board)){
                        System.out.println("Sudoku Solved!");
                        Renderer.print(board);
                    }else {
                        System.out.println("No solution found");
                    }
                }
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

                        if (Solver.getNumSolutions() == 1 && v != Solver.solvedValueAt(r, c)){
                            System.out.println("This value doesn't match the unique solution");
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