package sudoku;

/**
 * A single cell on the Sudoku board.
 * Holds the current value and whether it was a given clue.
 *
 * <p>Value {@code 0} means empty. Givens should not be changed by the UI.</p>
 */
public class Cell {
    private int value; // Value of cell
    private final boolean given; // Whether cell was filled on creation

    /* Constructor */

    /**
     * Construct a cell with an initial value and given flag.
     * <p>Value {@code 0} means empty; {@code 1..Board.SIZE} are digits. This constructor
     * does not validate, the range—callers should ensure the value is appropriate.</p>
     *
     * @param value the starting value (0 for empty, 1..Board.SIZE for a digit)
     * @param given whether this cell is a fixed clue (true) or editable (false)
     */
    public Cell(int value, boolean given){
        this.value = value;
        this.given = given;
    }

    /* Getter Methods */

    /**
     * Get a cell's value.
     * 
     * @return the cell's current value (0 for empty, otherwise 1...Board.SIZE)
     */
    public int getValue() { return value; }

    /**
     * Returns whether a cell's value was given in the initial puzzle or not.
     * 
     * @return {@code true} if this cell was part of the original puzzle (a given)
     */
    public boolean isGiven() { return given; }
    
    /* Setter Method */

    /**
     * Set the cell's value.
     *
     * @param v new value (0 to clear, or 1...Board.SIZE)
     */
    public void setValue(int value){
        if(!given) this.value = value;
    }

    /* Clear Methods */

    /**
     * Clear a cell's value.
     * 
     * @return {@code true} if this cell was part of the original puzzle (a given)
     */
    public void clearValue() {
        if(!given){
            this.value = 0;
        }
    }

    /**
     * String form of the cell suitable for board rendering.
     * Returns a dot {@code "."} for empty cells, otherwise the digit.
     *
     * @return {@code "."} if the value is 0; otherwise the decimal string of the value
     */
    @Override
    public String toString() {
        return (value == 0) ? "." : Integer.toString(value);
    }
}
