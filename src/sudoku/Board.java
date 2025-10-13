package sudoku;

public class Board {
    public static final int SIZE = 9;
    private final Cell[][] grid = new Cell[SIZE][SIZE];
    

    public Board(int[][] start) { // Initalize the board
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int v = start[r][c];
                grid[r][c] = new Cell(v, v != 0); // given if nonzero
            }
        }
    }

    public Cell cell(int r, int c) { return grid[r][c]; } // Getter method to pull cell without affecting the original grid

    public boolean inBounds(int r, int c) {  // Verify bounds intergrity
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    public boolean isValidPlacement(int r, int c, int v) { // Check if provided addition is valid
        boolean valid = true;
        if(!inBounds(r, c) || v < 1 || v > SIZE) { valid = false; } 
        for(int i = 0; i < SIZE; i++){ // Checks if provided number is already in the row or column it's attemping to be added to
            if (grid[r][i].getValue() == v) {
                valid = false;
                break;
            }
            if (grid[i][c].getValue() == v) {
                valid = false;
                break;
            }
        }
        int br = (r / 3) * 3, bc = (c/3) * 3;
        for (int rr = br; rr < br + 3; rr++){ // Check the 3x3 box for existing value
            for (int cc = bc; cc < bc + 3; cc++){
                if (grid[rr][cc].getValue() == v){
                    valid = false;
                    break;
                }
            }
        }
        return valid;
    }

    public boolean trySet(int r, int c, int v){ // Sets value if that cell isn't already given and the placement is valid
        boolean success = false;
        if (inBounds(r, c)){
            var cell = grid[r][c];
            if(!cell.isGiven()){
                if(isValidPlacement(r, c, v)){
                    cell.setValue(v);
                    success = true;
                }
            }
        }
        return success; 
    }

    public boolean tryClear(int r, int c){ // Tries to clear a specific cell
        boolean success = false;
        if(inBounds(r, c)){
            var cell = grid[r][c];
            if(!cell.isGiven()){
                cell.clearValue();
                success = true;
            }
        }
        return success;
    }

    public boolean isSolved() { // Check if board is solved
        boolean solved = true;
        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                int v = grid[r][c].getValue();
                if (v == 0){
                    solved = false;
                    break;
                }
                grid[r][c].setValue(0); // Setting every point to 0 and then cylce through each cell to verify correct solution
                boolean ok = isValidPlacement(r, c, v);
                grid[r][c].setValue(v);
                if(!ok) {
                    solved = false;
                    break;
                }
            }
        }
        return solved;
    }

    public static Board fromString(String s){ // Testing method that accepts a string and sets that string as the intial values for the puzzle. 
        if(s.length() != (SIZE * SIZE)) throw new IllegalArgumentException("Puzzle string must be equal to the size of the board (typically 9x9)"); 
        int[][] arr = new int[SIZE][SIZE];
        for (int i = 0; i < (SIZE * SIZE); i++){
            char ch = s.charAt(i);
            arr[i/SIZE][i % SIZE] = (ch == '0' || ch == '.') ? 0 : (ch - '0');
        }
        return new Board(arr);
    }
}
