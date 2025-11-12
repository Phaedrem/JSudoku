package sudoku;

/**
 * Mutable SIZExSIZE Sudoku board. Holds cell values, enforces in-bounds access,
 * and provides row/column/box legality checks.
 *
 * <p>Digits are 1–SIZE; 0 means empty. {@link #SIZE} is the single source of truth
 * for board dimensions.</p>
 */
public class Board {
    /** Board width/height (9 for a standard Sudoku). */
    public static final int SIZE = 9;
    public static final int BOX = 3;

    private final Cell[][] grid = new Cell[SIZE][SIZE];
    
    /**
     * Construct a SIZE×SIZE Sudoku board from an initial value matrix.
     * Digits {@code 1..SIZE} become givens; {@code 0} becomes an empty editable cell.
     *
     * <p><strong>Preconditions:</strong> {@code start} must be a SIZE×SIZE array whose
     * elements are in {@code 0..SIZE}. Values &gt; 0 are treated as fixed givens.</p>
     *
     * @param start initial values, row-major (size SIZE×SIZE; 0 for empty)
     */
    public Board(int[][] start) { // Initalize the board
        if (start == null) throw new NullPointerException("Passed 2D array is null");
        if (start.length != SIZE){
            throw new IllegalArgumentException("Passed array must have " + SIZE + " rows");
        }
        for (int r = 0; r < SIZE; r++) {
            int [] row = start[r];
            if (row == null || row.length != SIZE){
                throw new IllegalArgumentException("Row " + r + " must have " + SIZE + " columns");
            }
            for (int c = 0; c < SIZE; c++) {
                int v = start[r][c];
                if (v < 0 || v > SIZE){
                    throw new IllegalArgumentException(" Value at row " + r + " and column " + c + " is out of range for the size of board");
                }
                grid[r][c] = new Cell(v, v != 0); // given if nonzero
            }
        }
    }

    private Board(int[][] start, boolean[][] givens) {
        if (start == null) throw new NullPointerException("Passed 2D array is null");
        if (start.length != SIZE) throw new IllegalArgumentException("Passed array must have " + SIZE + " rows");
        for (int r = 0; r < SIZE; r++) {
            int[] row = start[r];
            if (row == null || row.length != SIZE) {
                throw new IllegalArgumentException("Row " + r + " must have " + SIZE + " columns");
            }
            for (int c = 0; c < SIZE; c++) {
                int v = row[c];
                if (v < 0 || v > SIZE) {
                    throw new IllegalArgumentException("Value out of range at (" + r + "," + c+ ")");
                }
                grid[r][c] = new Cell(v, givens[r][c]);
            }
        }
    }

    /**
     * Get the {@link Cell} at (r,c).
     *
     * @param r row index 0..SIZE-1
     * @param c column index 0..SIZE-1
     * @return the cell object at the given coordinates (never {@code null})
     * @throws ArrayIndexOutOfBoundsException if out of bounds
     */
    public Cell cell(int r, int c) {  // Getter method to pull cell without affecting the original grid
        if(!inBounds(r, c)) throw new ArrayIndexOutOfBoundsException("Row and/or Cell is out of bounds.");
        return grid[r][c];
    }

    /**
     * Check whether (r,c) lies within the board.
     *
     * @param r row index
     * @param c column index
     * @return {@code true} if 0 ≤ r,c &lt; SIZE
     */
    public boolean inBounds(int r, int c) {  // Verify bounds intergrity
        return r >= 0 && r < SIZE && c >= 0 && c < SIZE;
    }

    /**
     * Test whether placing {@code v} at (r,c) is legal under Sudoku rules.
     * Does not mutate the board.
     *
     * @param r row 0..SIZE-1
     * @param c col 0..SIZE-1
     * @param v digit 1..SIZE
     * @return {@code true} if no row, column, or BOXxBOX box conflict
     * @throws IllegalArgumentException if {@code v} is out of range
     */
    public boolean isValidPlacement(int r, int c, int v) { // Check if provided addition is valid
        boolean valid = true;
        if(!inBounds(r, c) || v < 1 || v > SIZE) { valid = false; } 
        if(valid) {
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
        }
        if(valid){
            int br = (r / BOX) * BOX, bc = (c/BOX) * BOX;
            for (int rr = br; rr < br + BOX; rr++){ // Check the BOXxBOX box for existing value
                for (int cc = bc; cc < bc + BOX; cc++){
                    if (grid[rr][cc].getValue() == v){
                        valid = false;
                        break;
                    }
                }
            }
        }
        return valid;
    }

    /**
     * Try to set {@code v} at (r,c) if the placement is legal and the cell is editable.
     *
     * @param r row 0..SIZE-1
     * @param c col 0..SIZE-1
     * @param v digit 1..SIZE
     * @return {@code true} if the value was set; {@code false} otherwise
     */
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

    /**
     * Try to clear the value at (r,c) if the cell is editable.
     *
     * @param r row 0..SIZE-1
     * @param c col 0..SIZE-1
     * @return {@code true} if the cell was cleared; {@code false} otherwise
     */
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

    /**
     * Check if puzzle has been solved.
     * 
     * @return {@code true} if all cells are nonzero and the grid is internally consistent
     *         (no duplicates in any row, column, or box)
     */
    public boolean isSolved() { // Check if board is solved
        boolean solved = true;
        for (int r = 0; r < SIZE; r++){
            for (int c = 0; c < SIZE; c++){
                if(!grid[r][c].isGiven()){
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
        }
        return solved;
    }

    /**
     * Build a board from an (SIZExSIZE)-character string (row-major).
     * Digits {@code 1..SIZE} set givens; {@code 0} or {@code '.'} mean empty.
     *
     * @param s (SIZExSIZE) characters of {@code [0-SIZE.]} in row-major order
     * @return a new board with those initial values
     * @throws IllegalArgumentException if the string length/content is invalid
     */
    public static Board fromString(String s){ // Testing method that accepts a string and sets that string as the intial values for the puzzle. 
        if(s.length() != (SIZE * SIZE)) throw new IllegalArgumentException("Puzzle string must be equal to the size of the board (typically 9x9)"); 
        int[][] arr = new int[SIZE][SIZE];
        for (int i = 0; i < (SIZE * SIZE); i++){
            char ch = s.charAt(i);
            arr[i/SIZE][i % SIZE] = (ch == '0' || ch == '.') ? 0 : (ch - '0');
        }
        return new Board(arr);
    }

    public static Board fromString(String values, String mask) {
        if (values == null || mask == null)
            throw new NullPointerException("values/mask");
        final int N = SIZE * SIZE;
        if (values.length() != N || mask.length() != N)
            throw new IllegalArgumentException("values/mask length must be SIZE*SIZE");

        int[][] start = new int[SIZE][SIZE];
        boolean[][] givens = new boolean[SIZE][SIZE];

        for (int i = 0; i < N; i++) {
            int r = i / SIZE, c = i % SIZE;
            char ch = values.charAt(i);
            start[r][c] = (ch == '0' || ch == '.') ? 0 : (ch - '0');
            givens[r][c] = (mask.charAt(i) == '1');
        }
        return new Board(start, givens);
    }
}
