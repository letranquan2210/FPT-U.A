import java.io.*;

class Queens {
    private final boolean available = true;
    private final int squares = 5, norm = squares - 1;
    private int[] positionInRow = new int[squares];
    private boolean[] column = new boolean[squares];
    private boolean[] leftDiagonal  = new boolean[squares*2 - 1];
    private boolean[] rightDiagonal = new boolean[squares*2 - 1];
    private int howMany = 0;
    public Queens() {
        for (int i = 0; i < squares; i++) {
            positionInRow[i] = -1;
            column[i] = available;
        }
        for (int i = 0; i < squares*2 - 1; i++)
            leftDiagonal[i] = rightDiagonal[i] = available;
    }
    public void PrintBoard(PrintStream out) {
        // left for exercise;
    }
    public void PutQueen(int row) {
        for (int col = 0; col < squares; col++) 
            if (column[col] == available &&
                leftDiagonal [row+col] == available &&
                rightDiagonal[row-col+norm] == available) {
                positionInRow[row] = col;
                column[col] = !available;
                leftDiagonal[row+col] = !available;
                rightDiagonal[row-col+norm] = !available;
                if (row < squares-1)
                     PutQueen(row+1);
                else PrintBoard(System.out);
                column[col] = available;
                leftDiagonal[row+col] = available;
                rightDiagonal[row-col+norm] = available;
            }
    }
    public static void main(String args[]) {
        Queens queens = new Queens();
        queens.PutQueen(0);
        System.out.println(queens.howMany + " solutions found.");
    }
}

