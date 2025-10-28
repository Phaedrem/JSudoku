package sudoku;

public interface BoardView {
    int get(int r, int c);
    boolean isGiven(int r, int c);
    void set(int r, int c, int val);
}
