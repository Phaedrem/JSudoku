package ui;

import java.awt.Dimension;
import javax.swing.JPanel;

public class CellView extends JPanel{
    private final int row, col;

    public CellView(int row, int col) {
        this.row = row;
        this.col = col;
        setOpaque(true);
        setPreferredSize((new Dimension(60, 60)));
    }
    
    public int row() {return row; }
    public int col() {return col; }
}
