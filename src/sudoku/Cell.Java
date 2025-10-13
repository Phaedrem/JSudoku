package sudoku;

public class Cell {
    private int value; // Value of cell
    private final boolean given; // Whether cell was filled on creation

    /* Constructor */
    public Cell(int value, boolean given){
        this.value = value;
        this.given = given;
    }

    /* Getter Methods */
    public int getValue() { return value; }

    public boolean isGiven() { return given; }
    
    /* Setter Method */
    public void setValue(int value){
        if(!given) this.value = value;
    }

    /* Clear Methods */
    public void clearValue() {
        if(!given){
            this.value = 0;
        }
    }

    @Override
    public String toString() {
        return (value == 0) ? "." : Integer.toString(value);
    }
}
