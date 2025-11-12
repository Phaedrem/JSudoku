package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Arrays;

import sudoku.Board;

/**
 * Visual component for a single Sudoku cell.
 * <p>
 * Renders the placed digit (if any), given vs. editable text styling,
 * and translucent overlays for selection, peer cells, and same-value cells.
 * Supports optional pencil marks rendering in a 3×3 micro-grid.
 */
public class CellView extends JPanel{
    private final int row, col;
    private final JLabel label = new JLabel("");
    private boolean selected = false;
    private boolean peer = false;
    private boolean sameValue = false;
    private boolean isIncorrect = false;
    private final boolean[] pencil = new boolean[Board.SIZE];
    private ColorTheme theme = ColorTheme.Preset.CLASSIC.theme();
    private int digit = 0;

    /**
     * Creates a cell at a fixed board coordinate and wires mouse selection.
     * Sets default sizing, layout, font, and 3×3 box borders.
     * @param row 0-based row index
     * @param col 0-based column index
     */
    public CellView(int row, int col) {
        this.row = row;
        this.col = col;
        setOpaque(true);
        setLayout(new GridBagLayout());
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 22f)); // Gets systems font and changes its size
        add(label);
        setPreferredSize((new Dimension(60, 60)));
        applyBoxBoarders();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e){
                if (getParent() instanceof BoardPanel bp){
                    bp.setSelectedCell(row, col);
                }
            }
        });

    }

    /**
     * Clears all pencil marks from this cell and repaints the view.
     * 
     * <p>This is called when a definitive digit is set via {@link #setDigit(int)} 
     * or when the board is reset, ensuring that only valid markings remain.</p>
     */
    public void clearPencils() {
        Arrays.fill(pencil, false);
        repaint();
    }

    /**
     * Applies a thick border around every third row and column to visually
     * separate the 3×3 Sudoku subgrids.
     * 
     * <p>The top and left borders are made thicker at the start of each box,
     * and the bottom/right edges are thickened for the final row/column.
     * The resulting border pattern mimics standard Sudoku grid lines.</p>
     */
    private void applyBoxBoarders() {
        int top = 1, left = 1, bottom = 1, right = 1;

        if (row % 3 == 0) top = 3;
        if (col % 3 == 0) left = 3;
        if (row == 8) bottom = 3;
        if (col == 8) right = 3;

        Border b = new MatteBorder(top, left, bottom, right, theme.gridLine());
        setBorder(b);
    }

    /** @return this cell's row index */
    public int row() {return row; }
    /** @return this cell's column index */
    public int col() {return col; }

    /**
     * Checks whether the given pencil mark (1–9) is currently displayed in this cell.
     *
     * @param d the digit (1–9) to check
     * @return {@code true} if the pencil mark for that digit is present, {@code false} otherwise
     */
    public boolean hasPencil(int d){
        boolean success = false;
        if (d > 0 && d <= Board.SIZE){
            success = pencil[d-1];
        }
        return success; 
    }

    /**
     * Ensures that the pencil mark for the given digit (1–9) is visible in this cell.
     * <p>
     * Unlike {@link #togglePencil(int)}, this method never removes a mark — it only
     * adds one if it is not already present. Used primarily for restoring marks
     * when undoing a move.
     *
     * @param d the digit (1–9) whose pencil mark should be added
     */
    public void addPencil(int d){
        if(d > 0 && d <= Board.SIZE && !pencil[d-1]){
            pencil[d-1] = true;
            repaint();
        }
    }

    /**
     * Sets the displayed digit (0 clears the cell). Clears any pencil marks
     * to keep the view consistent with a committed value.
     * @param value digit in [0..SIZE]
     */
    public void setDigit(int value) {
        clearPencils();
        digit = value;
        label.setText(value == 0 ? "" : Integer.toString(value));
    }

    /**
     * Applies given/editable text styling. Givens render bold
     * with {@link ColorTheme#textGiven()}, editables render plain
     * with {@link ColorTheme#textEditable()}.
     * @param given whether this cell is a fixed clue
     */
    public void setGiven(boolean given){
        if (given) {
            label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
            label.setForeground(theme.textGiven());
        } else {
            label.setFont(label.getFont().deriveFont(Font.PLAIN, 22f));
            label.setForeground(theme.textEditable());
        }
    }

    /** Marks this cell as selected and repaints the overlay. */
    public void setSelected(boolean s){
        selected = s;
        repaint();
    }

    /** Marks this cell as a peer (same row/col/box) and repaints the overlay. */
    public void setPeerHighlighted(boolean p){
        peer = p;
        repaint();
    }

    /** Enables or disables “same value” highlight for this cell. */
    public void setSameValueHighlight(boolean on){
        sameValue = on;
        repaint();
    }

    /**
     * Applies a new {@link ColorTheme} to this view and repaints.
     * Also re-derives borders that depend on the theme’s grid color.
     * @param t theme to apply
     */
    public void setTheme(ColorTheme t){
        this.theme = t;
        setForeground(theme.gridLine());
        repaint();
    }

    /**
     * Toggles the pencil mark for the given digit {@code d} in this cell.
     * 
     * <p>If the cell currently has no digit (i.e., {@code digit == 0}) and {@code d} is within
     * the valid Sudoku range (1–9), this method flips the boolean state of the corresponding
     * pencil mark and repaints the cell to visually reflect the change.</p>
     *
     * @param d the digit to toggle (1–SIZE)
     */
    public void togglePencil(int d) {
        if (digit == 0 && d > 0 && d <= Board.SIZE){
            pencil[d-1] = !pencil[d-1];
            repaint();
        }
    }

    /**
     * Removes the pencil mark for the specified digit {@code n}, if present.
     * 
     * <p>Used by the board when a digit is successfully placed in a peer cell
     * to clear conflicting pencil marks automatically.</p>
     *
     * @param n the digit whose pencil mark should be removed (1–9)
     */
    public void removePencil(int n) {
        if (n > 0 && n <= Board.SIZE){
            if(pencil[n-1]){
                pencil[n-1] = false;
                repaint();
            }
        }
    }

    /**
     * Custom painting of translucent overlays for peer/same-value/selected states.
     * Creates and disposes a {@link java.awt.Graphics2D} context to avoid side effects.
     */
    @Override
    protected void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create(); // Builds a visual overlay to prevent editting the original background color
        try {
            if(peer){
                g2.setColor(theme.peerFill());
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            if(sameValue){
                g2.setColor(theme.valueFill());
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            if(selected){
                g2.setColor(theme.selectedFill());
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            if (digit == 0) {
                Graphics2D g3 = (Graphics2D) g2.create();
                try {
                    g3.setColor(theme.textPencil());
                    int w = getWidth(), h = getHeight();
                    int subW = w / 3, subH = h / 3;
                    Font base = getFont();
                    Font small = base.deriveFont(base.getSize2D());
                    g3.setFont(small);
                    FontMetrics fm = g3.getFontMetrics();

                    for (int n = 1; n <= Board.SIZE; n++) {
                        if (pencil[n-1]){
                            int r = (n - 1) / 3;
                            int c = (n - 1) % 3;
                            int cx = c * subW + subW / 2;
                            int cy = r * subH + subH / 2;
                            String s = Integer.toString(n);
                            int tw = fm.stringWidth(s), th = fm.getAscent();
                            int x = cx - (tw / 2);
                            int y = cy + (th / 2);
                            g3.drawString(s, x, y);
                        }
                    }
                } finally {
                    g3.dispose();
                }
            }
        } finally {
            g2.dispose();
        }
    }

    public void setIncorrect(boolean incorrect){
        this.isIncorrect = incorrect;
        if(!this.isIncorrect){
            label.setForeground(theme.textEditable());
        } else {
            label.setForeground(theme.incorrectText());
        }
        repaint();
    }
}
