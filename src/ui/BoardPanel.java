package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Deque;
import java.util.ArrayDeque;

import sudoku.Board;

/**
 * Main board component that lays out a SIZE×SIZE grid of {@link CellView}s,
 * owns selection state, handles keyboard input (digits, clear, arrows),
 * updates cell highlights (selected/peers/same-value), and reports solved state.
 */
public class BoardPanel extends JPanel {
    private final BoardView board;
    private final List<CellView> cells = new ArrayList<>(Board.SIZE); // Keeps track of cell values
    private int selRow = -1, selCol = -1;
    private ColorTheme theme = ColorTheme.Preset.CLASSIC.theme();
    private boolean pencilMode = false;

     /**
     * Stores a single peer pencil mark that was removed as a result of placing a final value.
     * <p>
     * When a number is entered into a cell, any matching pencil marks in the same row,
     * column, or box are cleared. Each {@code PencilRestore} represents one of those cleared
     * pencil marks, so it can be reinstated if the move is undone.
     */
    private static final class PencilRestore {
        final int r, c, digit;
        PencilRestore(int r, int c, int d){
            this.r = r;
            this.c=c;
            this.digit=d;
        }
    }

    /**
     * Represents a reversible game action stored on the undo stack.
     * <p>
     * Each {@code UndoAction} captures the state needed to reverse a player move.
     * This includes both final number placements and pencil mark toggles. When a
     * placement is undone, the previous cell value, its pencil marks, and all affected
     * peer pencil marks are restored. When a pencil mark toggle is undone, the
     * previous mark state (on/off) is restored.
     */
    private static final class UndoAction {
        enum Type { PLACE_VALUE, TOGGLE_PENCIL }
        final Type type;
        final int r, c;

        // For PLACE_VALUE
        final int oldVal;
        final boolean[] cellPencilsBefore;
        final List<PencilRestore> peerPencilsRemoved;

        // For TOGGLE_PENCIL
        final int digit;
        final boolean pencilWasOn;

        UndoAction(Type t, int r, int c, int oldVal, int newVal,
                boolean[] cellPencilsBefore,
                List<PencilRestore> peers){
            this.type = t; this.r = r; this.c = c;
            this.oldVal = oldVal;
            this.cellPencilsBefore = cellPencilsBefore;
            this.peerPencilsRemoved = peers;
            this.digit = 0; this.pencilWasOn = false;
        }

        UndoAction(Type t, int r, int c, int digit, boolean wasOn){
            this.type = t; this.r = r; this.c = c;
            this.digit = digit; this.pencilWasOn = wasOn;
            this.oldVal = 0;
            this.cellPencilsBefore = null; this.peerPencilsRemoved = null;
        }
    }

    /**
     * Constructs a panel for the supplied {@link ui.BoardView} model.
     * Populates the grid with {@link CellView} instances, applying the current theme,
     * initial values, and given state.
     * @param board the backing view model
     */
    public BoardPanel(BoardView board) {
        this.board = board;
        setLayout(new GridLayout(Board.SIZE, Board.SIZE, 0, 0));

        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                CellView cell = new CellView(r,c);
                cell.setTheme(theme);
                cell.setBackground(theme.cellBackground());
                cell.setDigit(board.get(r, c));
                cell.setGiven(board.isGiven(r, c));
                cells.add(cell);
                add(cell);
                setupKeyBindings();
            }
        }
    }

    /** Converts (row, col) to the linear index into {@code cells}. */
    private int compIndex(int r, int c) { return r * Board.SIZE + c; }

    /**
     * Recomputes visual state for all cells based on the current selection:
     * clears previous flags, marks the selected cell, highlights peers
     * (same row/column/box), and optionally same-value cells.
     * Triggers a repaint at the end.
     */
    private void updateHighlights() {
        for (CellView cv : cells){
            cv.setPeerHighlighted(false);
            cv.setSameValueHighlight(false);
            cv.setSelected(false);
        }
        if (selRow >= 0){
            cells.get(compIndex(selRow, selCol)).setSelected(true);
            int boxR = selRow/3;
            int boxC = selCol/3;
            int selVal = board.get(selRow, selCol);
            for (int r = 0; r < Board.SIZE; r++){
                for(int c = 0; c < Board.SIZE; c++){
                    if (r == selRow && c == selCol) continue; // Skip the "selected" cell and move on to the next loop
                    boolean sameRow = (r == selRow);
                    boolean sameCol = (c == selCol);
                    boolean sameBox = ((r/3) == boxR) && ((c/3) == boxC);
                    if (sameRow || sameCol || sameBox){
                        cells.get(compIndex(r, c)).setPeerHighlighted(true);
                    }
                    if (selVal != 0 && board.get(r,c) == selVal){
                        cells.get(compIndex(r, c)).setSameValueHighlight(true);
                    }
                }
            }
        }
        repaint();
    }

    /**
     * Installs key bindings for digits (1..SIZE), clear (Backspace/Delete/0),
     * and arrow-key navigation. Actions dispatch to {@link #placeDigit(int)}
     * and {@link #bindArrow(String, int, int)}.
     */
    private void setupKeyBindings(){
        setFocusable(true);
        var im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        var am = getActionMap();

        for (int d = 1; d <= Board.SIZE; d++) { // Unqiuely identify each 1-SIZE digit input as seperate action via a loop
            final int digit = d;
            String key = "digit_" + d;
            im.put(KeyStroke.getKeyStroke(Integer.toString(digit)), key); // Number Row
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0 + digit, 0), key);  // Numpad
            am.put(key, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    placeDigit(digit);
                }
            });
        }
        
        im.put(KeyStroke.getKeyStroke("BACK_SPACE"), "clear"); // Clear inputs
        im.put(KeyStroke.getKeyStroke("DELETE"), "clear");
        im.put(KeyStroke.getKeyStroke("0"), "clear");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_NUMPAD0, 0), "clear");
        am.put("clear", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { placeDigit(0); }
        });

        bindArrow("LEFT", 0, -1); // Allow navigation via arrow keys
        bindArrow("RIGHT", 0, 1);
        bindArrow("UP", -1, 0);
        bindArrow("DOWN", 1, 0);
    }
    
     /**
     * Handles player input when entering a digit into the selected cell.
     * <p>
     * This method performs two main actions depending on the current mode:
     * <ul>
     *   <li><b>Pencil mode ON:</b> Toggles the given digit as a small pencil mark in the selected cell.
     *       The previous on/off state of the pencil mark is recorded for undo.</li>
     *   <li><b>Pencil mode OFF:</b> Attempts to place the given digit as a final value in the cell
     *       through {@link BoardView#trySet(int, int, int)}. If successful, the cell’s previous
     *       pencils are cleared, matching peer pencil marks (same row, column, and box) are removed,
     *       and the entire change is recorded for undo.</li>
     * </ul>
     * The undo system stores all information necessary to restore both the cell and any
     * affected peers to their prior state.
     * <p>
     * This method ignores invalid entries (e.g. digits outside 1–9, attempts to modify
     * given cells, or actions when no cell is selected).
     *
     * @param val the numeric value input by the player (1–9)
     */
    private void placeDigit(int val) {
        if (selRow >= 0 && val >= 0 && val <= Board.SIZE){
            CellView cv = cells.get(compIndex(selRow, selCol));
            if (pencilMode){
                if (board.get(selRow, selCol) == 0){
                    boolean wasOn = cv.hasPencil(val);
                    cv.togglePencil(val);
                    undoStack.push(new UndoAction(UndoAction.Type.TOGGLE_PENCIL, selRow, selCol, val, wasOn));
                    repaint();
                }
            } else {
                int oldVal = board.get(selRow, selCol);
                boolean ok;
                if(val == 0){
                    ok = board.tryClear(selRow, selCol);
                    System.out.println("Val was 0");
                } else {
                    ok = board.trySet(selRow, selCol, val);
                }
                if(ok){
                    boolean[] cellPencilsBefore = copyPencils(cv);
                    cv.setDigit(board.get(selRow, selCol));
                    List<PencilRestore> peersRemoved = null;
                    if(val != 0){
                        cv.clearPencils();
                        peersRemoved = removePeerPencilsAndRecord(selRow, selCol, val);
                    }

                    undoStack.push(new UndoAction(
                        UndoAction.Type.PLACE_VALUE,
                        selRow, selCol,
                        oldVal, val,
                        cellPencilsBefore,
                        peersRemoved
                        ));

                    if(board.isSolved()){
                        JOptionPane.showMessageDialog(this,"Puzzle Complete!");
                    }
                }
            }
        }
        updateHighlights();
    }

    /**
     * Binds one arrow key to move the selection by (dr, dc), clamped to bounds.
     * If nothing is selected yet, selects the first editable cell.
     */
    private void bindArrow(String name, int dr, int dc) {
        InputMap im = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke(name), "move_" + name);
        am.put("move_" + name, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selRow >= 0){
                    int r = Math.max(0, Math.min(8, selRow + dr));
                    int c = Math.max(0, Math.min(8, selCol + dc));
                    setSelectedCell(r, c);
                } else {
                    selectFirstEditable();
                }
            }
        });
    }

    /**
     * Selects the first cell that is not a given; falls back to (0,0) if none.
     * Used on initial navigation when no selection exists.
     */
    private void selectFirstEditable(){
        for (int r = 0; r < Board.SIZE; r++){
            for (int c = 0; c < Board.SIZE; c++){
                if(!board.isGiven(r, c)){
                    setSelectedCell(r, c);
                    return;
                }
            }
        }
        setSelectedCell(0,0);
    }

     /**
     * Returns the view model currently displayed by this panel.
     * @return the {@link ui.BoardView}
     */
    public BoardView getView(){
        return this.board;
    }

    /**
     * Changes the active {@link ui.ColorTheme} and reapplies it to all cells
     * (background, given/editable text colors, grid lines), then repaints.
     * @param t the theme to apply
     */
    public void setTheme(ColorTheme t){
        this.theme = t;
        for (CellView cv : cells){
            cv.setTheme(t);
            cv.setBackground(t.cellBackground());
            cv.setGiven(board.isGiven(cv.row(), cv.col()));
        }
        repaint();
    }

    /**
     * Enables/disables pencil input mode.
     * When enabled, digit keys toggle pencil marks for the selected empty cell.
     * @param on true to enable pencil mode; false to disable
     */
    public void setPencilMode(boolean on){
        this.pencilMode = on;
        repaint();
    }

    /**
     * Programmatically selects a cell and updates highlight overlays.
     * Requests focus for keyboard input.
     * @param r row index
     * @param c column index
     */
    public void setSelectedCell(int r, int c){ 
        if (selRow >= 0){
            cells.get(compIndex(selRow, selCol)).setSelected(false); // Clear old highlight
        }
        selRow = r;
        selCol = c;
        updateHighlights();
        requestFocusInWindow();
    }

    /**
     * Stack of reversible player actions.
     * <p>
     * Each element is an {@link UndoAction} describing either a final number placement
     * or a pencil toggle. The most recent action is popped when performing an undo,
     * allowing multiple sequential undos.
     */
    private final Deque<UndoAction> undoStack = new ArrayDeque<>();

    /**
     * Creates a snapshot of all pencil marks currently visible in a cell.
     * <p>
     * Used primarily by the undo system to capture a cell's pencil state before it is changed.
     * Each index (0–8) corresponds to digits 1–9. A value of {@code true} means the
     * pencil mark for that digit is present.
     *
     * @param cv the {@link CellView} to copy pencil data from
     * @return a new boolean array of length SIZE representing all pencil marks in the cell
     */
    private static boolean[] copyPencils(CellView cv){
        boolean[] out = new boolean[Board.SIZE];
        for (int d = 1; d <= Board.SIZE; d++) out[d-1] = cv.hasPencil(d);
        return out;
    }

    /**
     * Removes pencil marks matching the given value from all peers (same row, column, and box)
     * of the selected cell, and records which peers were changed.
     *
     * @param selRow the row index of the cell where the final number was placed
     * @param selCol the column index of the cell where the final number was placed
     * @param val    the value that was entered
     * @return a list of {@link PencilRestore} entries for each peer whose pencil mark was cleared
     */
    private List<PencilRestore> removePeerPencilsAndRecord(int selRow, int selCol, int val){
        List<PencilRestore> removed = new ArrayList<>();
        if (val > 0 && val <= Board.SIZE) {
            for (int c = 0; c < Board.SIZE; c++){
                if (c != selCol && board.get(selRow, c) == 0){
                    CellView cv = cells.get(compIndex(selRow, c));
                    if (cv.hasPencil(val)) { cv.removePencil(val); removed.add(new PencilRestore(selRow, c, val)); }
                }
                if (c != selRow && board.get(c, selCol) == 0){
                    CellView cv = cells.get(compIndex(c, selCol));
                    if (cv.hasPencil(val)) { cv.removePencil(val); removed.add(new PencilRestore(c, selCol, val)); }
                }
            }
            int r0 = (selRow / Board.BOX) * Board.BOX;
            int c0 = (selCol / Board.BOX) * Board.BOX;
            for (int r = r0; r < r0 + Board.BOX; r++){
                for (int c = c0; c < c0 + Board.BOX; c++){
                    if (r == selRow && c == selCol) continue;
                    if (board.get(r, c) == 0){
                        CellView cv = cells.get(compIndex(r, c));
                        if (cv.hasPencil(val)) { cv.removePencil(val); removed.add(new PencilRestore(r, c, val)); }
                    }
                }
            }
        }
        return removed;
    }

    /**
     * Reverts the most recent player action recorded on the undo stack.
     * <p>
     * For a final number placement, this method clears the number, restores the cell's
     * prior pencil marks, and re-adds any peer pencil marks that were automatically removed.
     * For a pencil toggle, the specific mark is restored to its previous state.
     * <p>
     * If no actions are available to undo, this method does nothing.
     */
    public void undoLast(){
        if (!undoStack.isEmpty()){
            UndoAction a = undoStack.pop();
            switch (a.type){
                case PLACE_VALUE -> {
                    boolean ok;
                    if (a.oldVal == 0){
                        ok = board.tryClear(a.r, a.c);
                    } else {
                        ok = board.trySet(a.r, a.c, a.oldVal);
                    }
                    if (ok) {
                        CellView cv = cells.get(compIndex(a.r, a.c));
                        cv.setDigit(a.oldVal);
                        if (a.cellPencilsBefore != null){
                            cv.clearPencils();
                            for (int d=1; d <= Board.SIZE; d++){
                                if (a.cellPencilsBefore[d-1]) cv.addPencil(d);
                            }
                        }
                        if (a.peerPencilsRemoved != null){
                            for (PencilRestore pr : a.peerPencilsRemoved){
                                if (board.get(pr.r, pr.c) == 0) {
                                    cells.get(compIndex(pr.r, pr.c)).addPencil(pr.digit);
                                }
                            }
                        }
                    }
                }
                case TOGGLE_PENCIL -> {
                    CellView cv = cells.get(compIndex(a.r, a.c));
                    if (a.pencilWasOn) {
                        if (!cv.hasPencil(a.digit)) cv.addPencil(a.digit);
                    } else {
                        if (cv.hasPencil(a.digit)) cv.removePencil(a.digit);
                    }
                }
            }
        }
        updateHighlights();
        repaint();
    }
}