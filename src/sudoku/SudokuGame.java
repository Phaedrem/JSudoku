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

        // 2. Print a welcome message
        System.out.println("Current board:");

        // 3. Render the board to terminal
        Renderer.print(board);

        // 4. Placeholder for looping input
        System.out.println("Type 'help' for commands");

    }
}