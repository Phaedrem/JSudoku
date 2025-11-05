package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import sudoku.Board;
import sudoku.BoardFacade;
import sudoku.BoardView;

/**
 * Top-level application window for JSudoku.
 * <p>
 * Owns the {@link BoardPanel} currently displayed, the menu bar (New/Save/Load/Exit
 * and Settings → Colors), and the logic to swap in a new puzzle or persist/load a game.
 */
public class SudokuFrame extends JFrame {
    private BoardPanel boardPanel;
    private boolean pencilMode = false;

    /**
     * Creates a frame showing the given Sudoku board.
     * @param board initial {@link sudoku.BoardView} to display
     */
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

    /**
     * Builds the application menu bar.
     * <ul>
     *   <li><b>File → New Game</b>: choose difficulty; starts a new board from {@code Seeds}.</li>
     *   <li><b>File → Save/Load</b>: write/read current grid and given-mask.</li>
     *   <li><b>Settings → Colors</b>: switch active {@link ui.ColorTheme} preset at runtime.</li>
     * </ul>
     * @return a configured {@link JMenuBar}
     */
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
            if (boardPanel != null) boardPanel.setPencilMode(pencilMode);
        });
        settingsMenu.add(colors);
        bar.add(filMenu);
        bar.add(settingsMenu);
        bar.add(pencilItem);

        return bar;
    }

    /**
     * Starts a new puzzle from the selected difficulty menu item by looking up
     * a seed string in {@code sudoku.Seeds.BY_NAME}, constructing a {@link sudoku.Board},
     * wrapping it in a {@link sudoku.BoardFacade}, and installing it in the UI.
     * @param e action event from a difficulty menu item
     */
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

    /**
     * Replaces the center {@link BoardPanel} with a new one for the provided view.
     * Ensures proper removal/addition in the content pane and triggers layout/paint.
     * @param view the {@link sudoku.BoardView} to display
     */
    private void setBoardView(BoardView view){
        if (boardPanel != null){
            getContentPane().remove(boardPanel);
        }
        boardPanel = new BoardPanel(view);
        getContentPane().add(boardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Opens a file chooser and writes the current board state to disk:
     * a simple header, a line of cell values, and a line of the given-mask.
     * Shows an error dialog if persistence fails.
     */
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

    /**
     * Opens a file chooser and reads a saved board from disk.
     * Accepts files saved with or without a given-mask line; validates lengths,
     * reconstructs a {@link sudoku.Board} accordingly, wraps in a {@link sudoku.BoardFacade},
     * and replaces the current view. Shows an error dialog on failure.
     */
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

    /**
     * Prompts the user before exiting the application.
     * <p>
     * Displays a confirmation dialog asking whether to save the current game
     * before quitting. If the user chooses to save, {@link #saveGame()} is invoked.
     * Choosing "Don't Save" immediately closes the window, and "Cancel" aborts
     * the exit operation. This provides the same safety behavior used for
     * {@code New Game}, preventing accidental data loss.
     */
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
