package ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import sudoku.BoardView;

public class BoardPanel extends JPanel {
    private final BoardView board;
    private final List<CellView> cells = new ArrayList<>(81); // Keeps track of cell values
    private int selRow = -1, selCol = -1;

    public BoardPanel(BoardView board) {
        this.board = board;
        setLayout(new GridLayout(9, 9, 0, 0));

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                CellView cell = new CellView(r,c);
                cell.setDigit(board.get(r, c));
                cell.setGiven(board.isGiven(r, c));
                cells.add(cell);
                boolean shadedBox = ((r / 3) + (c / 3)) % 2 == 0;
                if (shadedBox) {
                    cell.setBackground(new Color(230, 230, 240));
                } else {
                    cell.setBackground(new Color(250, 250, 255));
                }
                add(cell);
            }
        }
    }

    private int compIndex(int r, int c) { return r * 9 + c; }

    public void setSelectedCell(int r, int c){ 
        if (selRow >= 0){
            cells.get(compIndex(selRow, selCol)).setSelected(false); // Clear old highlight
        }
        selRow = r;
        selCol = c;
        cells.get(compIndex(r, c)).setSelected(true);
        requestFocusInWindow();
    }
}