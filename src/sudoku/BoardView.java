package sudoku;

public interface BoardView {
    int get(int r, int c);
    boolean isGiven(int r, int c);
    boolean trySet(int r, int c, int val);
}
