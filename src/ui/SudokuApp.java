package ui;

import javax.swing.SwingUtilities;
import sudoku.*;

/**
 * Application entry point for JSudoku.
 * <p>
 * Generates a new puzzle at the default difficulty using
 * {@link sudoku.Generator#generateUnique(int, int)}, solves it once to cache
 * the solution in {@link sudoku.Solver}, wraps the board in a {@link BoardFacade},
 * and shows the main {@link ui.SudokuFrame}.
 */
public class SudokuApp {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            Board start = Generator.generateUnique(SudokuFrame.EASY, SudokuFrame.MAXATTEMPTS);
            Solver.solveBoard(start);
            BoardFacade view = new BoardFacade(start);
            SudokuFrame frame = new SudokuFrame(view);
            frame.setVisible(true);
        });
    }
}
