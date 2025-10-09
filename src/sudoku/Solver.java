package sudoku;

import util.BoardUtils;

public class Solver {

    private static int numSolutions = 0;
    private static int searchCount = 0;
    private static final int SEARCH_LIMIT = 100000;
    private static boolean isSolvable = false;
    private static boolean hasMultiple = false;
    private static Board solvedBoard = null;

    private static boolean solveRec(Board board) {
        searchCount++;
        if (searchCount > SEARCH_LIMIT){
            return false;
        }
        int[] pos = findEmpty(board);
        if(pos == null){
            return true;
        }
        int r = pos[0];
        int c = pos[1];
        for (int v = 1; v <= 9; v++){
            if (board.isValidPlacement(r, c, v)){
                board.cell(r, c).setValue(v);
                if (solveRec(board)){
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

    private static int countRec(Board b, int limit){
        searchCount++;
        if (searchCount > SEARCH_LIMIT) return 0;
        int[] pos = findEmpty(b);
        if (pos == null) return 1; // one solution found
        int r = pos[0], c = pos[1];
        int solutions = 0;
        for (int v = 1; v <= 9; v++){
            if (b.isValidPlacement(r, c, v)){
                b.cell(r, c).setValue(v);
                solutions += countRec(b, limit - solutions);
                if (solutions >= limit){
                    b.cell(r, c).setValue(0);
                    return solutions; // short-circuit
                }
                b.cell(r, c).setValue(0);
            }
        }
        return solutions;
    }
    
    private static int countSolutions(Board original, int limit){
        if (limit < 1) limit = 1;
        searchCount = 0;
        Board copy = BoardUtils.copy(original);
        int count = countRec(copy, limit);
        numSolutions = count;
        isSolvable = (count > 0);
        hasMultiple = (count >= 2);
        return count;
    }

    public static int solvedValueAt(int r, int c){
        if (solvedBoard == null) throw new IllegalStateException("No solved board cached");
        return solvedBoard.cell(r, c).getValue();
    }


    public static boolean solve(Board board){
        searchCount = 0;
        return solveRec(board);
    }

    public static void solveBoard(Board original){
        int count = countSolutions(original, 2);
        if (count == 0){
            solvedBoard = null;
        }
        Board copy = BoardUtils.copy(original);
        boolean solved = solve(copy);
        if (solved){
            solvedBoard = BoardUtils.copy(copy);
        } else {
            solvedBoard = null;
            isSolvable = false;
        }
    }

    public static boolean isSolvable(){
        return isSolvable;
    }
    public static boolean hasMultipleSolutions(){
        return hasMultiple;
    }
    public static int getNumSolutions(){
        return numSolutions;
    }
    public static Board getSolvedBoardCopy(){
        return solvedBoard == null ? null : BoardUtils.copy(solvedBoard);
    }
}
