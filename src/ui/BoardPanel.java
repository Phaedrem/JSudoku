// ui/BoardPanel.java
package ui;

import javax.swing.*;
import java.awt.*;
import sudoku.BoardView;

public class BoardPanel extends JPanel {
    private final BoardView board;

    public BoardPanel(BoardView board) {
        this.board = board;
        setLayout(new GridLayout(9, 9, 1, 1)); // 1-pixel gaps so color edges show

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                JPanel cell = new CellView(r,c);

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
}