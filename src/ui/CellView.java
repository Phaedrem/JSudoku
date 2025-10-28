package ui;

import java.awt.Dimension;
import javax.swing.*;
import javax.swing.border.*;

public class CellView extends JPanel{
    private final int row, col;

    public CellView(int row, int col) {
        this.row = row;
        this.col = col;
        setOpaque(true);
        setPreferredSize((new Dimension(60, 60)));
        applyBoxBoarders();
    }
    
    public int row() {return row; }
    public int col() {return col; }

    private void applyBoxBoarders() {
        int top = 1, left = 1, bottom = 1, right = 1;

        if (row % 3 == 0) top = 3;
        if (col % 3 == 0) left = 3;
        if (row == 8) bottom = 3;
        if (col == 8) right = 3;

        Border b = new MatteBorder(top, left, bottom, right, getForeground());
        setBorder(b);
    }
}
