package sudoku;

import util.BoardUtils;

/**
 * Backtracking Sudoku solver with solution counting.
 *
 * <p>Usage pattern:
 * <ol>
 *   <li>Call {@link #countSolutions(Board, int)} with a limit of 2 to classify as
 *       unsolvable (0), unique (1), or multiple (&ge;2).</li>
 *   <li>If unique, call {@link #solveBoard(Board)} to cache a solved copy for hints/checks.</li>
 *   <li>Read status via {@link #getNumSolutions()} and consult the cached grid via
 *       {@link #getSolvedBoardCopy()} or a cell accessor.</li>
 * </ol>
 * All solving operates on copies; the caller's board is never mutated.</p>
 */
public class Solver {

    /* Global(s) */
    private static final int SEARCH_LIMIT = 1000000;
    private static final int SOLUTION_LIMIT = 2;
    
    /* Variables */
    private static int numSolutions = 0;
    private static int searchCount = 0;
    private static boolean isSolvable = false;
    private static Board solvedBoard = null;

    /* Private Methods */
    private static boolean solveRec(Board board) { // Helper to the solve Method so that searchCount can be reset to 0 on call and still allow recursion.
        searchCount++;
        if (searchCount > SEARCH_LIMIT){
            return false;
        }
        int[] pos = findEmpty(board);
        if(pos == null){
            return true;
        }
        int r = pos[0];
        int c = pos[1];
        for (int v = 1; v <= Board.SIZE; v++){
            if (board.isValidPlacement(r, c, v)){
                board.cell(r, c).setValue(v);
                if (solveRec(board)){
                    return true;
                }
                board.cell(r, c).setValue(0);
            }
        }
        return false;
    }
    
    private static int[] findEmpty(Board board){ // Searches for empty cells and returns it's location. 
        for (int r = 0; r < Board.SIZE; r++){
            for (int c = 0; c < Board.SIZE; c++){
                if(board.cell(r, c).getValue() == 0){
                    return new int[]{r,c};
                }
            }
        }
        return null;
    }

    private static int countRec(Board b, int limit){ // Helper method for countSolutions so that searchCount can be reset to 0 on call and still allow recursion.
        searchCount++;
        if (searchCount > SEARCH_LIMIT) return 0;
        int[] pos = findEmpty(b);
        if (pos == null) return 1; // one solution found
        int r = pos[0], c = pos[1];
        int solutions = 0;
        for (int v = 1; v <= Board.SIZE; v++){
            if (b.isValidPlacement(r, c, v)){
                b.cell(r, c).setValue(v);
                solutions += countRec(b, limit - solutions);
                if (solutions >= limit){
                    b.cell(r, c).setValue(0);
                    return solutions; // short-circuit
                }
                b.cell(r, c).setValue(0);
            }
        }
        return solutions;
    }
    
    private static int countSolutions(Board original, int limit){ // Counts the number of solutions up to a limit.
        if (limit < 1) limit = 1;
        searchCount = 0;
        Board copy = BoardUtils.copy(original);
        int count = countRec(copy, limit);
        numSolutions = count;
        isSolvable = (count > 0);
        return count;
    }

    /* Public Methods */

    /**
     * Get the solved value at (r,c) from the cached unique solution.
     *
     * @param r row 0..SIZE-1
     * @param c col 0..SIZE-1
     * @return digit 1..SIZE at (r,c) in the cached unique solution
     * @throws IllegalStateException if no unique solution is cached
     */
    public static int solvedValueAt(int r, int c){ // Returns value in a specific cell of the solution board.
        if (solvedBoard == null || numSolutions > 1) throw new IllegalStateException("No solved board cached");
        return solvedBoard.cell(r, c).getValue();
    }

    /**
     * Solve a board in place (backtracking).
     * <p>This method mutates its argument; callers should pass a copy.</p>
     *
     * @param board working board to solve
     * @return {@code true} if a solution was found
     */
    public static boolean solve(Board board){ // Solves the board if possible
        searchCount = 0;
        return solveRec(board);
    }

    /**
     * Decide uniqueness by counting up to a limit (usually 2),
     * and if exactly one solution exists, cache a solved copy internally.
     *
     * <p>This method does not mutate {@code original} and does not change
     * {@link #getNumSolutions()} itself; that value is set by {@link #countSolutions(Board, int)}.</p>
     *
     * @param original starting puzzle (not mutated)
     */
    public static void solveBoard(Board original){
        countSolutions(original, SOLUTION_LIMIT);
        if (numSolutions == 1){
            solvedBoard = BoardUtils.copy(original);
            solve(solvedBoard);
        } else {
            solvedBoard = null;
        }
    }

    /* Getter methods */

    /**
     * Returns whether or not the algorithm was able to solve the board.
     * 
     * @return true is a singular solution has been found and stored.
     */
    public static boolean isSolvable(){
        return isSolvable;
    }

    /**
     * Get's the number of solutions found.
     * 
     * @return the number of solutions found by the most recent analysis
     *         (0 = unsolvable, 1 = unique, ≥2 = multiple)
     */
    public static int getNumSolutions(){
        return numSolutions;
    }

    /**
     * Gets the solved board if one exists. 
     * 
    * @return a defensive copy of the cached solved board if one exists (unique case),
    *         otherwise {@code null}
    */ 
    public static Board getSolvedBoardCopy(){
        return solvedBoard == null ? null : BoardUtils.copy(solvedBoard);
    }
}
