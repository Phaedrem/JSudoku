package ui;

import sudoku.Board;

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

    @Override
    public boolean tryClear(int r, int c){
        return board.tryClear(r, c);
    }
}
