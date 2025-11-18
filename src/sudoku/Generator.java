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

    public static Board generateUnique(int minClues, int maxAttempts){
        minClues = Math.max(17, Math.min(81, minClues));
        for (int attempt = 0; attempt < maxAttempts; attempt++){
            int[][] solved = generateSolvedGrid();

            char[] values = new char[Board.SIZE*Board.SIZE];
            char[] mask   = new char[Board.SIZE*Board.SIZE];
            for (int i = 0; i < values.length; i++){
                int r = i / Board.SIZE, c = i % Board.SIZE;
                values[i] = (char)('0' + solved[r][c]);
                mask[i]   = '1';
            }

            List<Integer> order = new ArrayList<>(values.length);
            for (int i = 0; i < values.length; i++) order.add(i);
            Collections.shuffle(order, RNG);

            int clues = 81;
            for (int pos : order){
                if (clues > minClues){
                    char savedVal = values[pos];
                    values[pos] = '0';
                    mask[pos]   = '0';

                    Board base = Board.fromString(new String(values), new String(mask));
                    for (int r = 0; r < Board.SIZE; r++){
                        for (int c = 0; c < Board.SIZE; c++){
                            if (!base.cell(r,c).isGiven()) base.cell(r,c).setValue(0);
                        }
                    }

                    Solver.solveBoard(base);
                    boolean unique = (Solver.getSolvedBoardCopy() != null);

                    if (unique){
                        clues--;
                    } else {
                        values[pos] = savedVal;
                        mask[pos]   = '1';
                    }
                }
            }

            Board finalBase = Board.fromString(new String(values), new String(mask));
            for (int r = 0; r < Board.SIZE; r++){
                for (int c = 0; c < Board.SIZE; c++){
                    if (!finalBase.cell(r,c).isGiven()) finalBase.cell(r,c).setValue(0);
                }
            }
            Solver.solveBoard(finalBase);
            if (Solver.getSolvedBoardCopy() != null){
                return Board.fromString(new String(values), new String(mask));
            }
        }
        throw new IllegalStateException("Could not generate a unique puzzle in time");
    }

}
