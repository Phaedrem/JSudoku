package sudoku;

public class Renderer {
    
    public static void print(Board b){
        String sep = "+-------+-------+-------+";
        for(int r = 0; r < 9; r++){
            if (r % 3 == 0) System.out.println(sep);
            for(int c = 0; c < 9; c++){
                if (c % 3 == 0) System.out.print("| ");
                int v = b.cell(r,c).getValue();
                System.out.print((v == 0 ? ". " : (v + " ")));
            }
            System.out.println("|");
        }
        System.out.println(sep);
    }
}
