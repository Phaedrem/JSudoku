package util;

import sudoku.Board;

public class BoardUtils {
    public static Board copy(Board src) {
    int[][] grid = new int[9][9];
    for (int r = 0; r < 9; r++) {
        for (int c = 0; c < 9; c++) {
            grid[r][c] = src.cell(r, c).getValue();
        }
    }
    return new Board(grid);
    }
}
