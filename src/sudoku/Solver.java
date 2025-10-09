package sudoku;

public class Solver {

    public static boolean solve(Board board){
        return false;

    }
    
    public static int[] findEmpty(Board board){ // MAKE PRIVATE AFTER TEST
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
