package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import sudoku.Board;
import sudoku.BoardFacade;
import sudoku.BoardView;

public class SudokuFrame extends JFrame {
    private BoardPanel boardPanel;

    public SudokuFrame(BoardView board) {
        super("JSudoku");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setBoardView(board);
        setJMenuBar(createMenuBar());

        setSize(600, 650);
        setLocationRelativeTo(null);
    }

    private JMenuBar createMenuBar(){
        JMenuBar bar = new JMenuBar();
        JMenu filMenu = new JMenu("File");
        JMenu newMenu = new JMenu("New Game");
        String[] diffs = {"Easy", "Medium", "Hard", "Multi", "Impossible"};
        for (String d : diffs){
            JMenuItem item = new JMenuItem(d);
            item.addActionListener(this::startNewPuzzle);
            newMenu.add(item);
        }
        filMenu.add(newMenu);
        JMenuItem saveItem = new JMenuItem("Save Game");
        JMenuItem loadItem = new JMenuItem("Load Game");
        JMenuItem exitItem = new JMenuItem("Exit");

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

    private void startNewPuzzle(ActionEvent e){
        String label = ((JMenuItem) e.getSource()).getText().toLowerCase();
        String seed = sudoku.Seeds.BY_NAME.get(label);
        Board core = Board.fromString(seed);
        BoardView view = new BoardFacade(core);
        setBoardView(view);
    }

    private void setBoardView(BoardView view){
        if (boardPanel != null){
            getContentPane().remove(boardPanel);
        }
        boardPanel = new BoardPanel(view);
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
