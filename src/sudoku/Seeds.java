package sudoku;

/**
 * Legacy CLI support class used for Phase 1 (terminal version) of the project.
 * The final integrated GUI uses {@link ui.SudokuApp} as its entry point.
 */

import java.util.Map;

/**
 * Built-in Sudoku seed strings for quick loading in the CLI.
 * <p>Each seed is an 81-character row-major string where digits {@code 1..9}
 * are givens and {@code '0'} or {@code '.'} represent empty cells.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * String s = Seeds.BY_NAME.get("easy");
 * Board b = Board.fromString(s);
 * }</pre>
 *
 * @see sudoku.Board#fromString(String)
 */
public final class Seeds {
    private Seeds() {}

    public static final String EASY =
        "003020600" +
        "900305001" +
        "001806400" +
        "008102900" +
        "700000008" +
        "006708200" +
        "002609500" +
        "800203009" +
        "005010300";

    public static final String MEDIUM =
        "200080300" +
        "060070084" +
        "030500209" +
        "000105408" +
        "000000000" +
        "402706000" +
        "301007040" +
        "720040060" +
        "004010003";

    public static final String HARD =
        "000000907" +
        "000420180" +
        "000705026" +
        "100904000" +
        "050000040" +
        "000507009" +
        "920108000" +
        "034059000" +
        "507000000";

    public static final String MULTI =
        "100000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000";

    public static final String IMPOSSIBLE =
        "550000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000" +
        "000000000";

    public static final Map<String, String> BY_NAME = Map.of(
        "easy", EASY,
        "medium", MEDIUM,
        "hard", HARD,
        "multi", MULTI,
        "impossible", IMPOSSIBLE
    );
}
