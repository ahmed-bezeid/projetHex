package Game;
import java.util.*;

public class Board {
    protected Color grid[][];
    protected int size;
    private DFS pathFinder;
    private List<Set<Position>> red_ends;
    private List<Set<Position>> blue_ends;
    private final static int[] POS_X = {0, 0, 1, -1, 1, -1};
    private final static int[] POS_Y = {1, -1, 0, 0, -1, 1};

 
    public static boolean isPositionValid(Position p, int size) {
        return p.getX() >= 0 && p.getX() < size && p.getY() >= 0 && p.getY() < size;
    }

    public static List<Position> getNeighbors(Position p, int size) {
        int x = p.getX();
        int y = p.getY();
        List<Position> res = new ArrayList<>();

        for (int i = 0; i < POS_X.length; i++) {
            Position np = new Position(x + POS_X[i], y + POS_Y[i]);
            if (isPositionValid(np, size)) {
                res.add(np);
            }
        }
        return res;
    }


    public static Color[][] cloneGrid(Color[][] grid) {
        int size = grid.length;
        Color[][] copy = new Color[size][size];

        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, size);
        }
        return copy;
    }

    public Board(int size) {
        this.size = size;
        this.grid = new Color[size][size];

        this.red_ends = new ArrayList<>();
        this.blue_ends = new ArrayList<>();

        boardInit();

        this.pathFinder = new DFS();
    }
    public Board(Color[][] grid) {
        this(grid.length);
        this.grid = grid;
    }
    public void boardInit() {
        Set<Position> north = new HashSet<>();
        Set<Position> south = new HashSet<>();
        Set<Position> west = new HashSet<>();
        Set<Position> east = new HashSet<>();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = Color.NONE;

                Position p = new Position(i, j);

                if (i == 0) north.add(p);
                if (i == size - 1) south.add(p);
                if (j == 0) west.add(p);
                if (j == size - 1) east.add(p);
            }
        }

        red_ends.add(north);
        red_ends.add(south);

        blue_ends.add(west);
        blue_ends.add(east);
    }


    public boolean isAvailable(Position p) {
        return isPositionValid(p, size) && grid[p.getX()][p.getY()] == Color.NONE;
    }

    public boolean placePawn(Color color, Position p) {
        if (isAvailable(p)) {
            grid[p.getX()][p.getY()] = color;
            return true;
        }
        return false;
    }
 

    public boolean isAWin(Color c) {
        List<Set<Position>> playerEnds = c == Color.RED ? red_ends : blue_ends;
        return pathFinder.hasPath(grid, playerEnds, c);
    }


    public int getSize() {
        return size;
    }

    public static void printBoard(Board board){

        Color[][] grid = board.getGrid();
    int n = board.getSize();

    System.out.println("\nPlateau :");

    for (int r = 0; r < n; r++) {
        // Décalage pour former la forme hexagonale
        for (int s = 0; s < r; s++) {
            System.out.print("  ");
        }
        // Afficher la ligne
        for (int c = 0; c < n; c++) {
            System.out.print(grid[r][c].getColorText() + " ");
        }

        System.out.println();
    }
    }

    public Color[][] getGrid() {
        return grid;
    }
    public List<Set<Position>> getRedEnds(){
        return this.red_ends;
    }

    public List<Set<Position>> getBlueEnds(){
        return this.blue_ends;
    }
}
