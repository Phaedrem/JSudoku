package ui;

import sudoku.Board;

/**
 * Read-only interface exposing the state of a Sudoku board.
 * <p>
 * A {@code BoardView} provides methods to query cell values and properties
 * (for example, whether a cell is given) without allowing direct modification.
 * This enforces separation between the model (the {@link Board}) and
 * presentation layers ({@link ui.BoardPanel}, {@link ui.CellView}).
 */
public interface BoardView {
    
    /**
     * Returns the digit stored in the specified cell.
     *
     * @param r the row index
     * @param c the column index
     * @return the cell's digit in [0..SIZE], where 0 means empty
     */
    int get(int r, int c);
    
     /**
     * Indicates whether the specified cell is a fixed "given"
     * (i.e., part of the initial puzzle and not user-editable).
     *
     * @param r the row index
     * @param c the column index
     * @return {@code true} if this cell is a given; {@code false} otherwise
     */
    boolean isGiven(int r, int c);

    /**
     * Attempts to set a digit value in the given cell.
     * <p>
     * Implementations validate whether the target cell is editable and, if so,
     * update the underlying model. Returning {@code false} indicates that
     * the operation was rejected (for example, because the cell was marked
     * as a given or failed internal validation).
     *
     * @param r   the row index
     * @param c   the column index
     * @param val the digit to place, or 0 to clear the cell
     * @return {@code true} if the value was successfully applied;
     *         {@code false} otherwise
     */
    boolean trySet(int r, int c, int val);
    
     /**
     * Returns {@code true} if all cells of the board are filled with
     * nonzero digits satisfying the puzzle's solved condition.
     *
     * @return whether the board is fully solved
     */
    boolean isSolved();

    /**
     * Attempts to clear a cell at the given coordinates.
     * <p>
     * The operation fails if the cell is a given (immutable) clue.
     *
     * @param r row index
     * @param c column index
     * @return {@code true} if the cell was cleared; {@code false} if it was a given
     */
    boolean tryClear(int r, int c);

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
    boolean hasUniqueSolution();

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
    int solutionAt(int r, int c);

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
    void setUnsafe(int r, int c, int val);
}
