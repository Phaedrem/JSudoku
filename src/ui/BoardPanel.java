package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import sudoku.Board;
import sudoku.BoardView;

public class BoardPanel extends JPanel {
    private final BoardView board;
    private final List<CellView> cells = new ArrayList<>(Board.SIZE); // Keeps track of cell values
    private int selRow = -1, selCol = -1;
    private ColorTheme theme = ColorTheme.Preset.CLASSIC.theme();

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

    private int compIndex(int r, int c) { return r * Board.SIZE + c; }

    public void setSelectedCell(int r, int c){ 
        if (selRow >= 0){
            cells.get(compIndex(selRow, selCol)).setSelected(false); // Clear old highlight
        }
        selRow = r;
        selCol = c;
        updateHighlights();
        requestFocusInWindow();
    }

    private void updateHighlights() {
        for (CellView cv : cells){
            cv.setPeerHighlighted(false);
            cv.setSelected(false);
        }
        if (selRow >= 0){
            cells.get(compIndex(selRow, selCol)).setSelected(true);
            int boxR = selRow/3;
            int boxC = selCol/3;
            for (int r = 0; r < Board.SIZE; r++){
                for(int c = 0; c < Board.SIZE; c++){
                    if (r == selRow && c == selCol) continue; // Skip the "selected" cell and move on to the next loop
                    boolean sameRow = (r == selRow);
                    boolean sameCol = (c == selCol);
                    boolean sameBox = ((r/3) == boxR) && ((c/3) == boxC);
                    if (sameRow || sameCol || sameBox){
                        cells.get(compIndex(r, c)).setPeerHighlighted(true);
                    }
                }
            }
        }
        repaint();
    }

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

    private void placeDigit(int val) {
        if (selRow >= 0){
            boolean ok = board.trySet(selRow, selCol, val);
            if(ok){
                CellView cv = cells.get(compIndex(selRow, selCol));
                cv.setDigit(board.get(selRow, selCol));
                cv.repaint();
                if(board.isSolved()){
                   JOptionPane.showMessageDialog(this,"Puzzle Complete!");
            }
            }
        }
    }

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

    public BoardView getView(){
        return this.board;
    }

    public void setTheme(ColorTheme t){
        this.theme = t;
        for (CellView cv : cells){
            cv.setTheme(t);
            cv.setBackground(t.cellBackground());
            cv.setGiven(board.isGiven(cv.row(), cv.col()));
        }
        repaint();
    }

}