package sudoku;

public class Renderer {
    
    public static void print(Board b){
        String sep = " +-------+-------+-------+";
        System.out.println("   1 2 3   4 5 6   7 8 9");
        for(int r = 0; r < Board.SIZE; r++){
            if (r % 3 == 0) System.out.println(sep);
            System.out.print(r+1);
            for(int c = 0; c < Board.SIZE; c++){
                if (c % 3 == 0) System.out.print("| ");
                int v = b.cell(r,c).getValue();
                System.out.print((v == 0 ? ". " : (v + " ")));
            }
            System.out.println("|");
        }
        System.out.println(sep);
    }
}
