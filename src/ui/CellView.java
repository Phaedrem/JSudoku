package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.Arrays;

import sudoku.Board;

public class CellView extends JPanel{
    private final int row, col;
    private final JLabel label = new JLabel("");
    private boolean selected = false;
    private boolean peer = false;
    private boolean sameValue = false;
    private final boolean[] pencil = new boolean[Board.SIZE];
    private ColorTheme theme = ColorTheme.Preset.CLASSIC.theme();
    private int digit = 0;

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
    
    public int row() {return row; }
    public int col() {return col; }

    private void applyBoxBoarders() {
        int top = 1, left = 1, bottom = 1, right = 1;

        if (row % 3 == 0) top = 3;
        if (col % 3 == 0) left = 3;
        if (row == 8) bottom = 3;
        if (col == 8) right = 3;

        Border b = new MatteBorder(top, left, bottom, right, theme.gridLine());
        setBorder(b);
    }

    public void setDigit(int value) {
        clearPencils();
        digit = value;
        label.setText(value == 0 ? "" : Integer.toString(value));
    }

    public void setGiven(boolean given){
        if (given) {
            label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
            label.setForeground(theme.textGiven());
        } else {
            label.setFont(label.getFont().deriveFont(Font.PLAIN, 22f));
            label.setForeground(theme.textEditable());
        }
    }

    public void setSelected(boolean s){
        selected = s;
        repaint();
    }

    public void setPeerHighlighted(boolean p){
        peer = p;
        repaint();
    }

    public void setSameValueHighlight(boolean on){
        sameValue = on;
        repaint();
    }

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

    public void setTheme(ColorTheme t){
        this.theme = t;
        setForeground(theme.gridLine());
        repaint();
    }

    public void togglePencil(int d) {
        if (digit == 0 && d > 0 && d <= 9){
            pencil[d-1] = !pencil[d-1];
            repaint();
        }
    }

    private void clearPencils() {
        Arrays.fill(pencil, false);
        repaint();
    }

    public void removePencil(int n) {
        if (n > 0 && n <= Board.SIZE){
            if(pencil[n-1]){
                pencil[n-1] = false;
                repaint();
            }
        }
    }
}
