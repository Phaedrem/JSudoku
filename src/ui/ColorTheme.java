package ui;

import java.awt.Color;

public record ColorTheme(
    Color cellBackground,
    Color textGiven,
    Color textEditable,
    Color selectedFill,
    Color peerFill,
    Color valueFill,
    Color gridLine
) {
    public enum Preset {
        CLASSIC(new ColorTheme(
            new Color(255,255,255),  // cellBackground
            new Color(52,72,97),     // textGiven
            new Color(50,90,175),    // textEditable
            new Color(187,222,251),  // selectedFill
            new Color(226,235,243),  // peerFill
            new Color(195,215,234),  // valFill
            new Color(52,72,97)   // gridLine
        )),
        MIDNIGHT(new ColorTheme(
            new Color(33,37,43),
            new Color(210,215,220),
            new Color(140,190,255),
            new Color(70,75,90),
            new Color(50,55,70),
            new Color(100,150,255),
            new Color(90,95,105)
        )),
        HIGH_CONTRAST(new ColorTheme(
            Color.WHITE,
            Color.BLACK,
            Color.BLACK,
            Color.YELLOW,
            new Color(210,210,210),
            new Color(255,128,0),
            Color.BLACK
        ));

        private final ColorTheme theme;
        Preset(ColorTheme t){ this.theme = t;}
        public ColorTheme theme(){ return theme; } 
        public String displayName() {
            String formatted = name().replace("_", " ").toLowerCase();
            return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        }
    }
}