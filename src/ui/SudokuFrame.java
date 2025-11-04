package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import sudoku.Board;
import sudoku.BoardFacade;
import sudoku.BoardView;

public class SudokuFrame extends JFrame {
    private BoardPanel boardPanel;
    private boolean pencilMode = false;

    public SudokuFrame(BoardView board) {
        super("JSudoku");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                promptExit();
            }
        });
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
        exitItem.addActionListener(e -> promptExit());

        filMenu.add(saveItem);
        filMenu.add(loadItem);
        filMenu.addSeparator();
        filMenu.add(exitItem);

        JMenu settingsMenu = new JMenu("Settings");
        JMenu colors = new JMenu("Colors");
        for (ui.ColorTheme.Preset p : ui.ColorTheme.Preset.values()){
            JMenuItem item = new JMenuItem(p.displayName());
            item.addActionListener(e -> {
                if (boardPanel != null) boardPanel.setTheme(p.theme());
            });
            colors.add(item);
        }

        JCheckBoxMenuItem pencilItem = new JCheckBoxMenuItem("Pencil Mode");
        pencilItem.setState(pencilMode);
        pencilItem.addActionListener(e -> {
            pencilMode = pencilItem.getState();
        });
        settingsMenu.add(colors);
        bar.add(filMenu);
        bar.add(settingsMenu);
        bar.add(pencilItem);

        return bar;
    }

    private void startNewPuzzle(ActionEvent e){
        String label = ((JMenuItem) e.getSource()).getText().toLowerCase();

        Object[] options = {"Save", "Don't Save", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Save your game before starting a new one?",
                "New Game",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == JOptionPane.CANCEL_OPTION || choice == -1) return;
        if (choice == JOptionPane.YES_OPTION){
            boolean saved = saveGame();
            if (!saved)  return;
        }

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

    private boolean saveGame() {
        if (boardPanel == null) return false;

        // Ask where to save
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Sudoku");
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return false;
        // Create strings to save
        StringBuilder vals = new StringBuilder(Board.SIZE*Board.SIZE);
        StringBuilder mask = new StringBuilder(Board.SIZE*Board.SIZE);
        try {
            BoardView v = boardPanel.getView();
            
            for (int r = 0; r < Board.SIZE; r++) {
                for (int c = 0; c < Board.SIZE; c++) {
                    int val = v.get(r, c);
                    vals.append((char)('0' + Math.max(0, Math.min(val, Board.SIZE))));
                    mask.append(v.isGiven(r, c) ? '1' : '0');
                }
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Could not read current board:\n" + ex.getMessage(),
                    "Save Game", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // Output strings into file
        java.io.File file = chooser.getSelectedFile();
        try (java.io.PrintWriter out = new java.io.PrintWriter(file, java.nio.charset.StandardCharsets.UTF_8)) {
            out.println("JSUDOKU v1");
            out.println(vals);
            out.println(mask);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to save:\n" + ex.getMessage(),
                    "Save Game", JOptionPane.ERROR_MESSAGE);
            return false;
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

            if (header == null || values == null || values.length() != Board.SIZE*Board.SIZE) {
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

    private void promptExit(){
        Object[] options = {"Save", "Don't Save", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Save game before exiting?",
            "Exit",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
             null,
             options,
             options[0]);

        if (choice == JOptionPane.CANCEL_OPTION || choice == -1) return;
        if (choice == JOptionPane.NO_OPTION) {
            dispose();
            return;
        }
        boolean saved = saveGame();
        if (saved) {
            dispose();
        }
    }
}
