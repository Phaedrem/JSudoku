package sudoku;

import util.BoardUtils;

public class Solver {

    int numSolutions = 0;

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

    public static Board solvedBoard (Board orignal){
        Board copy = BoardUtils.copy(orignal);
        if(solve(copy)){
            return copy;
        }else {
            return null;
        }
    }

    private static int countRec(Board b, int limit){
        int[] pos = findEmpty(b);
        if (pos == null) return 1;
        int r = pos[0];
        int c = pos[1];
        int total = 0;
        for (int v = 1; v <= 9; v++){
            if (b.isValidPlacement(r, c, v)){
                b.cell(r,c).setValue(v);
                total += countSolutions(b, limit - total);
                if (total >= limit) {
                    b.cell(r, c).setValue(0);
                    return total;
                }b.cell(r, c).setValue(0);
            }
        }
        return total;
    }

    public static int countSolutions(Board original, int limit){
        if (limit < 1) limit = 1;
        Board copy = BoardUtils.copy(original);
        return countRec(copy, limit);
    }
}
