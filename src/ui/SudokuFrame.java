package ui;

import javax.swing.*;
import java.awt.*;
import sudoku.BoardView;

public class SudokuFrame extends JFrame {
    public SudokuFrame(BoardView board) {
        super("JSudoku");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new BoardPanel(board), BorderLayout.CENTER);

        setSize(600, 650);
        setLocationRelativeTo(null);
    }
}
