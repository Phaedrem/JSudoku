package ui;

import java.awt.Color;

/**
 * Immutable palette for the Sudoku UI.
 * <ul>
 *   <li>{@code cellBackground}: cell background color</li>
 *   <li>{@code textGiven}: color for given (clue) digits</li>
 *   <li>{@code textEditable}: color for user-entered digits</li>
 *   <li>{@code textPencil}: color for pencil marks in empty cells</li>
 *   <li>{@code selectedFill}: overlay fill for the selected cell</li>
 *   <li>{@code peerFill}: overlay fill for peers (same row/col/box)</li>
 *   <li>{@code valueFill}: overlay fill for same-value highlight</li>
 *   <li>{@code gridLine}: grid/border color</li>
 * </ul>
 */
public record ColorTheme(
    Color cellBackground,
    Color textGiven,
    Color textEditable,
    Color incorrectText,
    Color textPencil,
    Color selectedFill,
    Color peerFill,
    Color valueFill,
    Color incorrectFill,
    Color gridLine
) {
    /**
     * Built-in, named color presets.
     * Each enum constant wraps a {@link ColorTheme} instance and provides
     * a human-friendly display name for menus.
     */
    public enum Preset {
        CLASSIC(new ColorTheme(
            new Color(255,255,255),  // cellBackground
            new Color(52,72,97),     // textGiven
            new Color(50,90,175),    // textEditable
            new Color(229,92,108),   // incorrectText
            new Color(110,124,140),  // textPencil
            new Color(187,222,251),  // selectedFill
            new Color(226,235,243),  // peerFill
            new Color(195,215,234),  // valueFill
            new Color(247,207,214),   // incorrectFill
            new Color(52,72,97)      // gridLine
        )),
        MIDNIGHT(new ColorTheme(
            new Color(33,37,43),
            new Color(210,215,220),
            new Color(140,190,255),
            new Color(229,92,108),
            new Color(50,90,175),
            new Color(70,75,90),
            new Color(50,55,70),
            new Color(100,150,255),
            new Color(229,92,108),
            new Color(90,95,105)
        )),
        HIGH_CONTRAST(new ColorTheme(
            Color.WHITE,
            Color.BLACK,
            Color.BLACK,
            new Color(229,92,108),
            Color.BLACK,
            Color.YELLOW,
            new Color(210,210,210),
            new Color(255,128,0),
            Color.RED,
            Color.BLACK
        ));

        private final ColorTheme theme;
        Preset(ColorTheme t){ this.theme = t;}
        
        /** @return the {@link ColorTheme} instance associated with this preset. */
        public ColorTheme theme(){ return theme; } 
        
        /**
         * Formats the enum name as a user-facing label
         * (e.g., "HIGH_CONTRAST" → "High contrast").
         * @return display name for menus
         */
        public String displayName() {
            String formatted = name().replace("_", " ").toLowerCase();
            return formatted.substring(0, 1).toUpperCase() + formatted.substring(1);
        }
    }
}