package util;

import sudoku.Board;

public class BoardUtils {
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
