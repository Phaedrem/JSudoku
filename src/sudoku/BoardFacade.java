package sudoku;

public class BoardFacade implements BoardView {
    private final Board board;

    public BoardFacade(Board board) { this.board = board; }

    @Override public int get(int r, int c) {
        return board.cell(r, c).getValue();
    }

    @Override public boolean isGiven(int r, int c) {
        return board.cell(r, c).isGiven();
    }

    @Override public void set(int r, int c, int val) {
        if (!isGiven(r, c)) board.cell(r, c).setValue(val);
    }

    public Board getCore() { return board; }
}
