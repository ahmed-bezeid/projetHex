package Game;
import java.util.*;

public class DFS {
  // Vérifie si le joueur "c" a un chemin entre ses deux bords
    public boolean hasPath(Color[][] grid, List<Set<Position>> ends, Color c) {
        int size = grid.length;

        Set<Position> start = ends.get(0);
        Set<Position> end = ends.get(1);

        Set<Position> visited = new HashSet<>();

        for (Position p : start) {
            int x = p.getX();
            int y = p.getY();

            if (grid[x][y] == c) {
                if (dfs(grid, size, p, end, visited, c)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfs(Color[][] grid, int size, Position p,
                        Set<Position> end, Set<Position> visited, Color c) {

        if (end.contains(p)) return true;
        if (visited.contains(p)) return false;

        visited.add(p);

        for (Position child : Board.getNeighbors(p, size)) {
            int x = child.getX();
            int y = child.getY();

            if (grid[x][y] == c) {
                if (dfs(grid, size, child, end, visited, c)) return true;
            }
        }
        return false;
    }
}

