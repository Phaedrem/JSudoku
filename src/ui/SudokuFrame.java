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
        saveItem.addActionListener(e -> saveGame());
        JMenuItem loadItem = new JMenuItem("Load Game");
        loadItem.addActionListener(e -> loadGame());
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

    private void saveGame() {
        if (boardPanel == null) return;

        // Ask where to save
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Sudoku");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        // Create strings to save
        StringBuilder vals = new StringBuilder(81);
        StringBuilder mask = new StringBuilder(81);
        try {
            BoardView v = boardPanel.getView();
            
            for (int r = 0; r < 9; r++) {
                for (int c = 0; c < 9; c++) {
                    int val = v.get(r, c);
                    vals.append((char)('0' + Math.max(0, Math.min(val, 9))));
                    mask.append(v.isGiven(r, c) ? '1' : '0');
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not read current board:\n" + ex.getMessage(),
                    "Save Game", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Output strings into file
        java.io.File file = chooser.getSelectedFile();
        try (java.io.PrintWriter out = new java.io.PrintWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
            out.println("JSUDOKU v1");
            out.println(vals);
            out.println(mask);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save:\n" + ex.getMessage(),
                    "Save Game", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadGame(){
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load Sudoku");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        java.io.File file = chooser.getSelectedFile();
        try (java.io.BufferedReader br = new java.io.BufferedReader(
                new java.io.InputStreamReader(new java.io.FileInputStream(file), java.nio.charset.StandardCharsets.UTF_8))) {

            String header = br.readLine();
            String values  = br.readLine();
            String mask  = br.readLine();

            if (header == null || values == null || values.length() != 81) {
                throw new IllegalArgumentException("Invalid save file format");
            }

            sudoku.Board core = (mask != null && mask.length() == values.length())
                ? Board.fromString(values, mask)
                : Board.fromString(values);
            sudoku.BoardView view = new sudoku.BoardFacade(core);
            
            setBoardView(view);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load:\n" + ex.getMessage(),
                    "Load Game", JOptionPane.ERROR_MESSAGE);
        }
    }
}
