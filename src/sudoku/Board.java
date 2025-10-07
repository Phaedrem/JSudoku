package sudoku;

public class Board {
    private final Cell[][] grid = new Cell[9][9];

    public Board(int[][] start) { // Initalize board
        for (int r = 0; r < 9; r++){
            for (int c = 0; c < 9; r++){
                int v = start[r][c];
                grid[r][c] = new Cell(v, v != 0);
            }
        }
    }

    public Cell cell(int r, int c) { return grid[r][c]; } // Getter method to pull cell without affecting the original grid

    public boolean inBounds(int r, int c) {  // Verify bounds intergrity
        return r >= 0 && r < 9 && c >= 0 && c < 9;
    }

    public boolean isValidPlacement(int r, int c, int v) { // Check if provided addition is valid
        boolean valid = true;
        if(!inBounds(r, c) || v < 1 || v > 9) { return false; } 
        return valid;
    }

    public boolean trySet(int r, int c, int v){ // Try to place a number
        boolean success = false;
        return success; 
    }

    public boolean isSolved() { // Check if board is solved
        boolean solved = false;
        return solved;
    }

    public static Board fromString(String s){ // Testing method that accepts a string and sets that string as the intial values for the puzzle. 
        if(s.length() != 81) throw new IllegalArgumentException("Puzzle string must be 81 chars"); 
        int[][] arr = new int[9][9];
        for (int i = 0; i < 81; i++){
            char ch = s.charAt(i);
            arr[i/9][i % 9] = (ch == '0' || ch == '.') ? 0 : (ch - '0');
        }
        return new Board(arr);
    }
}
