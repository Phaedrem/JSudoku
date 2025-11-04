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

    @Override public boolean trySet(int r, int c, int val) {
        boolean success = false;
        if (!isGiven(r, c)) {
            board.cell(r, c).setValue(val);
            success = true;
        }
        return success;
    }

    @Override
    public boolean isSolved(){
        for(int r = 0; r < Board.SIZE; r++){
            for(int c = 0; c < Board.SIZE; c++){
                if(get(r, c) == 0) return false;
            }
        }
        return true;
    }

    public Board getCore() { return board; }
}
