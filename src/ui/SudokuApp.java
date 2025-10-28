package ui;

import javax.swing.SwingUtilities;
import sudoku.*;

public class SudokuApp {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            Board start = Board.fromString(Seeds.EASY);
            BoardFacade view = new BoardFacade(start);
            SudokuFrame frame = new SudokuFrame(view);
            frame.setVisible(true);
        });
    }
}
