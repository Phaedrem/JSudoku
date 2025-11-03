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
        setJMenuBar(createMenuBar());

        setSize(600, 650);
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar(){
        JMenuBar bar = new JMenuBar();
        JMenu filMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New Game");
        JMenuItem saveItem = new JMenuItem("Save Game");
        JMenuItem loadItem = new JMenuItem("Load Game");
        JMenuItem exitItem = new JMenuItem("Exit");

        filMenu.add(newItem);
        filMenu.add(saveItem);
        filMenu.add(loadItem);
        filMenu.addSeparator();
        filMenu.add(exitItem);

        JMenu viewMenu = new JMenu("Settings");
        JMenuItem settingsItem = new JMenuItem("Colors");
        viewMenu.add(settingsItem);

        bar.add(filMenu);
        bar.add(viewMenu);

        return bar;
    }
}
