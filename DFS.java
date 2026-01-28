import java.util.*;

public class DFS {

    public boolean hasPath(Color[][] grid, List<Set<Position>> ends, Color c) {
        int size = grid.length;

        Set<Position> start = ends.get(0);
        Set<Position> end = ends.get(1);

        // Utiliser un seul ensemble visited pour toute la recherche
        Set<Position> visited = new HashSet<>();

        for (Position p : start) {
            int x = p.getX();
            int y = p.getY();

            if (grid[x][y] == c) {
                if (dfs(grid, size, p, end, visited, c)) {
                    return true;
                }
                // Ne pas réinitialiser visited entre les points de départ
                // Le visited est partagé
            }
        }
        return false;
    }

    private boolean dfs(Color[][] grid, int size, Position p,
                        Set<Position> end, Set<Position> visited, Color c) {

        if (end.contains(p)) {
            return true; // On a atteint le bord opposé
        }
        
        if (visited.contains(p)) {
            return false; // Déjà visité
        }

        visited.add(p); // Marquer comme visité

        // Explorer tous les voisins
        for (Position neighbor : Board.getNeighbors(p, size)) {
            int x = neighbor.getX();
            int y = neighbor.getY();

            // Vérifier que le voisin a la bonne couleur
            if (grid[x][y] == c) {
                if (dfs(grid, size, neighbor, end, visited, c)) {
                    return true; // Chemin trouvé
                }
            }
        }
        
        return false; // Aucun chemin depuis ce point
    }
}