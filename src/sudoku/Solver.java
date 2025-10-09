package sudoku;

public class Solver {

    public static boolean solve(Board board){
        int[] pos = findEmpty(board);
        if(pos == null){
            return true;
        }
        int r = pos[0];
        int c = pos[1];

        for (int v = 1; v <= 9; v++){
            if (board.isValidPlacement(r, c, v)){
                board.cell(r, c).setValue(v);
                if (solve(board)){
                    return true;
                }
                board.cell(r, c).setValue(0);
            }
        }
        return false;

    }
    
    private static int[] findEmpty(Board board){
        for (int r = 0; r < 9; r++){
            for (int c = 0; c < 9; c++){
                if(board.cell(r, c).getValue() == 0){
                    return new int[]{r,c};
                }
            }
        }
        return null;
    }

}
