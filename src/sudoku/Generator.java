package sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class Generator {
    private static final Random RNG = new Random();

    public static int[][] generateSolvedGrid(){
        int[][] g = new int[Board.SIZE][Board.SIZE];
        fill(g, 0);
        return g;
    }

    private static boolean fill(int[][] g, int index){
        boolean finished = false;
        if (index == Board.SIZE * Board.SIZE) {
            finished = true;   
        } else {
            int r = index / Board.SIZE, c = index % Board.SIZE;

            List<Integer> digits = new ArrayList<>(9);
            for (int d = 1; d <= Board.SIZE; d++) digits.add(d);
            Collections.shuffle(digits, RNG);

            for (int d : digits){
                if (verifyPlacement(g, r, c, d)){
                    g[r][c] = d;
                    if (fill(g, index+1)) {
                        finished = true;
                    } else {
                        g[r][c] = 0;
                    }   
                }
            }
        }
        return finished;
    }

    private static boolean verifyPlacement(int[][] g, int r, int c, int d){
        boolean success = true;
        for (int i = 0; i < Board.SIZE; i++){
            if (g[r][i] == d) success = false;
            if (g[i][c] == d) success = false;
        }
        int br = (r/Board.BOX)*Board.BOX, bc = (c/Board.BOX)*Board.BOX;
        for (int rr = br; rr < br + Board.BOX; rr++){
            for (int cc = bc; cc < bc + Board.BOX; cc++){
                if (g[rr][cc] == d) success = false;
            }
        }
        return success;
    }
}
