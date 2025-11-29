package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Utility class responsible for creating valid Sudoku puzzles.
 * <p>
 * A puzzle is generated in two main steps:
 * <ol>
 *   <li>First, a completely solved Board.SIZE×Board.SIZE grid is produced using a randomized
 *       backtracking algorithm.</li>
 *   <li>Then, values are selectively removed while checking with
 *       {@link sudoku.Solver} to ensure that the resulting puzzle still has
 *       a <strong>unique solution</strong>.</li>
 * </ol>
 * The amount of starting clues and the number of regeneration attempts can
 * be controlled through method parameters.
 * <p>
 * All methods are static since the generator maintains no state.
 */
public final class Generator {
    private static final Random RNG = new Random();

    /**
     * Generates a fully solved {@code SIZE × SIZE} Sudoku grid.
     * <p>
     * The grid is produced by a randomized backtracking search, so repeated calls
     * will usually yield different valid solutions.
     *
     * @return a new solved grid, where each entry is in {@code 1..9}
     */
    public static int[][] generateSolvedGrid(){
        int[][] g = new int[Board.SIZE][Board.SIZE];
        fill(g, 0);
        return g;
    }

    /**
     * Generates a Sudoku puzzle with a unique solution.
     * <p>
     * The method repeatedly:
     * <ol>
     *   <li>Generates a full solution grid.</li>
     *   <li>Encodes it as a value string and a mask of givens.</li>
     *   <li>Randomly removes givens (down to {@code minClues}) while
     *       checking that the resulting puzzle still has a unique solution.</li>
     * </ol>
     * If a unique puzzle cannot be produced within {@code maxAttempts},
     * an exception is thrown.
     *
     * @param minClues    minimum number of givens to preserve (clamped to {@code [17,81]})
     * @param maxAttempts maximum number of generation attempts before failing
     * @return a {@link Board} representing a puzzle with a unique solution
     * @throws IllegalStateException if a unique puzzle cannot be generated in time
     */
    public static Board generateUnique(int minClues, int maxAttempts){
        minClues = Math.max(17, Math.min(81, minClues));
        for (int attempt = 0; attempt < maxAttempts; attempt++){
            int[][] solved = generateSolvedGrid();

            char[] values = new char[Board.SIZE*Board.SIZE];
            char[] mask   = new char[Board.SIZE*Board.SIZE];
            for (int i = 0; i < values.length; i++){
                int r = i / Board.SIZE, c = i % Board.SIZE;
                values[i] = (char)('0' + solved[r][c]);
                mask[i]   = '1';
            }

            List<Integer> order = new ArrayList<>(values.length);
            for (int i = 0; i < values.length; i++) order.add(i);
            Collections.shuffle(order, RNG);

            int clues = 81;
            for (int pos : order){
                if (clues > minClues){
                    char savedVal = values[pos];
                    values[pos] = '0';
                    mask[pos]   = '0';

                    Board base = Board.fromString(new String(values), new String(mask));
                    for (int r = 0; r < Board.SIZE; r++){
                        for (int c = 0; c < Board.SIZE; c++){
                            if (!base.cell(r,c).isGiven()) base.cell(r,c).setValue(0);
                        }
                    }

                    Solver.solveBoard(base);
                    boolean unique = (Solver.getSolvedBoardCopy() != null);

                    if (unique){
                        clues--;
                    } else {
                        values[pos] = savedVal;
                        mask[pos]   = '1';
                    }
                }
            }

            Board finalBase = Board.fromString(new String(values), new String(mask));
            for (int r = 0; r < Board.SIZE; r++){
                for (int c = 0; c < Board.SIZE; c++){
                    if (!finalBase.cell(r,c).isGiven()) finalBase.cell(r,c).setValue(0);
                }
            }
            Solver.solveBoard(finalBase);
            if (Solver.getSolvedBoardCopy() != null){
                return Board.fromString(new String(values), new String(mask));
            }
        }
        throw new IllegalStateException("Could not generate a unique puzzle in time");
    }

    /**
     * Recursive backtracking helper that fills the grid with a complete solution.
     * <p>
     * Cells are filled in order. At each step it tries a shuffled list
     * of digits and recurses.
     *
     * @param g     the partially filled grid
     * @param index linear index in {@code 0..(SIZE*SIZE)} indicating which cell to fill
     * @return {@code true} if a complete solution was found, {@code false} otherwise
     */
    private static boolean fill(int[][] g, int index){
        boolean finished = false;
        if (index == Board.SIZE * Board.SIZE) {
            finished = true;   
        } else {
            int r = index / Board.SIZE, c = index % Board.SIZE;

            List<Integer> digits = new ArrayList<>(9);
            for (int d = 1; d <= Board.SIZE; d++) digits.add(d);
            Collections.shuffle(digits, RNG);

            for (int d : digits){
                if (verifyPlacement(g, r, c, d)){
                    g[r][c] = d;
                    if (fill(g, index+1)) {
                        finished = true;
                    } else {
                        g[r][c] = 0;
                    }   
                }
            }
        }
        return finished;
    }

    /**
     * Checks whether placing digit {@code d} at cell {@code (r, c)} is legal
     * under Sudoku rules for the current partial grid.
     *
     * @param g the grid to test against
     * @param r row index
     * @param c column index
     * @param d candidate digit to place
     * @return {@code true} if {@code d} does not already appear in the row,
     *         column, or box; {@code false} otherwise
     */
    private static boolean verifyPlacement(int[][] g, int r, int c, int d){
        boolean success = true;
        for (int i = 0; i < Board.SIZE; i++){
            if (g[r][i] == d) success = false;
            if (g[i][c] == d) success = false;
        }
        int br = (r/Board.BOX)*Board.BOX, bc = (c/Board.BOX)*Board.BOX;
        for (int rr = br; rr < br + Board.BOX; rr++){
            for (int cc = bc; cc < bc + Board.BOX; cc++){
                if (g[rr][cc] == d) success = false;
            }
        }
        return success;
    }
}
