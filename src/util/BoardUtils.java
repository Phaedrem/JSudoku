package util;

import sudoku.Board;

/**
 * Utility helpers for {@link sudoku.Board}.
 * <p>Note: copies include values (and givens if encoded on the board),
 * and intentionally exclude any UI-only state.</p>
 */
public class BoardUtils {

    /**
     * Deep-copy a board's values into a new {@link Board}.
     *
     * @param src source board
     * @return an independent board with the same cell values
     */
    public static Board copy(Board src) {
    int[][] grid = new int[Board.SIZE][Board.SIZE];
    for (int r = 0; r < Board.SIZE; r++) {
        for (int c = 0; c < Board.SIZE; c++) {
            grid[r][c] = src.cell(r, c).getValue();
        }
    }
    return new Board(grid);
    }
}
