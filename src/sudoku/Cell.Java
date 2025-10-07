package sudoku;

import java.util.HashSet; // To use for allowing pencil marks for possible answers
import java.util.Set;

public class Cell {
    private int value; // Value of cell
    private final boolean given; // Whether cell was filled on creation
    private final Set<Integer> notes = new HashSet<>(); // Section for pencil marks in each cell


    /* Constructor */
    public Cell(int value, boolean given){
        this.value = value;
        this.given = given;
    }

    /* Getter Methods */
    public int getValue() { return value; }

    public boolean isGiven() { return given; }

    public Set<Integer> getNotes() { return notes; }
    
    /* Setter Method */
    public void setValue(int value){
        if(!given) this.value = value;
    }

    /* Clear Methods */
    public void clearNotes() { notes.clear(); }

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
