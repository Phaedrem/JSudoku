package ui;

import sudoku.Board;
import sudoku.Solver;

/**
 * Facade that bridges the mutable {@link Board} model with the {@link BoardView} interface.
 * <p>
 * Exposes both the read-only board view used by the UI and limited write operations
 * through {@link #trySet(int, int, int)}. The facade delegates all reads and writes
 * directly to the underlying {@link Board}, which maintains both cell values and
 * their "given" status.
 */
public class BoardFacade implements BoardView {
    private final Board board;

    /**
     * Constructs a {@code BoardFacade} that wraps the provided {@link Board}.
     * The facade does not copy board data; all accessors and mutations
     * operate on the same underlying model instance.
     *
     * @param board the mutable Sudoku {@link Board} to expose
     */
    public BoardFacade(Board board) { this.board = board; }

    /**
     * Returns the digit currently stored at the given cell.
     * Delegates directly to the underlying {@link Board}.
     *
     * @param r row index
     * @param c column index
     * @return the cell's digit, or {@code 0} if empty
     */
    @Override public int get(int r, int c) {
        return board.cell(r, c).getValue();
    }

    /**
     * Reports whether the specified cell is a fixed clue in this puzzle.
     * Delegates directly to {@link Board#isGiven(int, int)}.
     *
     * @param r row index
     * @param c column index
     * @return {@code true} if the cell is marked as a given
     */
    @Override public boolean isGiven(int r, int c) {
        return board.cell(r, c).isGiven();
    }

    /**
     * Attempts to set a digit in the specified cell.
     * <p>
     * The operation succeeds only if the cell is not marked as a given
     * and the board accepts the placement. If successful, the board’s
     * state is updated and the method returns {@code true}.
     *
     * @param r row index
     * @param c column index
     * @param val the digit to place (0 clears the cell)
     * @return {@code true} if the cell was updated; {@code false} otherwise
     */
    @Override public boolean trySet(int r, int c, int val) {
        return board.trySet(r, c, val);
    }

    /**
     * Returns {@code true} if the wrapped board is completely filled
     * and meets the solved-state condition.
     *
     * @return whether the puzzle is solved
     */
    @Override
    public boolean isSolved(){
        return board.isSolved();
    }

    /**
     * Attempts to clear a cell at the given coordinates.
     * <p>
     * The operation fails if the cell is a given (immutable) clue.
     *
     * @param r row index
     * @param c column index
     * @return {@code true} if the cell was cleared; {@code false} if it was a given
     */
    @Override
    public boolean tryClear(int r, int c){
        return board.tryClear(r, c);
    }

    /**
     * Determines whether the current puzzle has exactly one valid solution.
     * <p>
     * This method queries the {@link sudoku.Solver} to check whether a solved
     * board has been computed and that the number of identified solutions is
     * exactly one. It performs no solving itself.
     *
     * @return {@code true} if a solved board exists and the solver reports
     *         exactly one solution; {@code false} otherwise
     */
    @Override
    public boolean hasUniqueSolution(){
        return Solver.getSolvedBoardCopy() != null && Solver.getNumSolutions() == 1;
    }

    /**
     * Returns the solved digit for the given cell from the {@link sudoku.Solver}.
     * <p>
     * This assumes the solver has already been run on a compatible board and that
     * the solved state is still cached.
     *
     * @param r row index
     * @param c column index
     * @return the solved digit for {@code (r, c)}
     */
    @Override
    public int solutionAt(int r, int c){
        return Solver.solvedValueAt(r,c);
    }

    /**
     * Sets a digit in the given cell without any legality checks.
     * <p>
     * This method respects givens (it will not overwrite them) but does not verify
     * row/column/box constraints. Intended for UI features such as hinting or
     * showing incorrect entries.
     *
     * @param r   row index
     * @param c   column index
     * @param val digit to store
     */
    @Override
    public void setUnsafe(int r, int c, int val){
        var cell = board.cell(r, c);
        if (!cell.isGiven()){
            cell.setValue(val);
        }
    }
}
