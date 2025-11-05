package ui;

import javax.swing.SwingUtilities;
import sudoku.*;

/**
 * Application entry point for JSudoku.
 * <p>
 * Initializes a starting board from a seed, wraps it in a {@code BoardFacade}
 * (to expose a read/write {@code BoardView}), creates the main {@link ui.SudokuFrame},
 * and shows the UI.
 */
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
