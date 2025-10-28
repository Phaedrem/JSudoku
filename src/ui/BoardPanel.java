// ui/BoardPanel.java
package ui;

import javax.swing.*;
import java.awt.*;
import sudoku.BoardView;

public class BoardPanel extends JPanel {
    private final BoardView board;

    public BoardPanel(BoardView board) {
        this.board = board;
        setPreferredSize(new Dimension(600, 600));
    }
}
