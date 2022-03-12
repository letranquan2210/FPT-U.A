//****************************  Maze.java  ******************************
import java.io.*;

class MazeCell {
    public int x, y;
    public MazeCell() {
    }
    public MazeCell(int i, int j) {
        x = i; y = j;
    }
    public boolean equals(MazeCell cell) {
        return x == cell.x && y == cell.y;
    }
}

class Maze {
    private int rows = 0, cols = 0;
    private char[][] store;
    private MazeCell currentCell, exitCell = new MazeCell(), entryCell = new MazeCell();
    private final char exitMarker = 'e', entryMarker = 'm', visited = '.';
    private final char passage = '0', wall = '1';
    private Stack<MazeCell> mazeStack = new Stack<MazeCell>();
    public Maze() {
        int row = 0, col = 0;
        Stack<String> mazeRows = new Stack<String>();
        BufferedReader buffer = new BufferedReader(
                                new InputStreamReader(System.in));
        System.out.println("Enter a rectangular maze using the following "
                + "characters:\nm - entry\ne - exit\n1 - wall\n0 - passage\n"
                + "Enter one line at at time; end with Ctrl-z:");
        try {
            String str = buffer.readLine();
            while (str != null) {
                row++;
                cols = str.length();
                str = "1" + str + "1";  // put 1s in the borderline cells;
                mazeRows.push(str);
                if (str.indexOf(exitMarker) != -1) {
                    exitCell.x = row;
                    exitCell.y = str.indexOf(exitMarker);
                }       
                if (str.indexOf(entryMarker) != -1) {
                    entryCell.x = row;
                    entryCell.y = str.indexOf(entryMarker);
                }
                str = buffer.readLine();
            }
        } catch(IOException eof) {
        }
        rows = row; 
        store = new char[rows+2][];       // create a 1D array of char arrays;
        store[0] = new char[cols+2];      // a borderline row;
        for ( ; !mazeRows.isEmpty(); row--)
            store[row] = mazeRows.pop().toCharArray();
        store[rows+1] = new char[cols+2]; // another borderline row;
        for (col = 0; col <= cols+1; col++) {
            store[0][col] = wall;         // fill the borderline rows with 1s;
            store[rows+1][col] = wall;
        }
    }
    private void display(PrintStream out) {
        for (int row = 0; row <= rows+1; row++)
            out.println(store[row]);
        out.println();
    }
    private void pushUnvisited(int row, int col) {
        if (store[row][col] == passage || store[row][col] == exitMarker)
            mazeStack.push(new MazeCell(row,col));
    }
    void exitMaze(PrintStream out) {
        currentCell = entryCell;
        out.println();
        while (!currentCell.equals(exitCell)) {
            int row = currentCell.x;
            int col = currentCell.y;
            display(System.out);        // print a snapshot;
            if (!currentCell.equals(entryCell))
                 store[row][col] = visited;
            pushUnvisited(row-1,col);
            pushUnvisited(row+1,col);
            pushUnvisited(row,col-1);
            pushUnvisited(row,col+1);
            if (mazeStack.isEmpty()) {
                 display(out);          
                 out.println("Failure");
                 return;
            }
            else currentCell = mazeStack.pop();
        }
        display(out);
        out.println("Success");
    }
    static public void main (String args[]) {
        (new Maze()).exitMaze(System.out);
    }
}
