package ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class CellView extends JPanel{
    private final int row, col;
    private final JLabel label = new JLabel("");
    private boolean selected = false;
    private boolean peer = false;
    private boolean sameValue = false;
    private ColorTheme theme = ColorTheme.Preset.CLASSIC.theme();

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
        } finally {
            g2.dispose();
        }
    }

    public void setTheme(ColorTheme t){
        this.theme = t;
        setForeground(theme.gridLine());
        repaint();
    }
}
