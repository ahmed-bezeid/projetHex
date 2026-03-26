package Rave;
import Game.*;
import Mcts.*;
import java.util.*;

public class RAVE {
    private Map<State, NodeRave> nodes;
    private int budget;
    private Color playerColor;
    private List<Set<Position>> redEnds;
    private List<Set<Position>> blueEnds;
    private DFS dfs;
    private Random random;

    private static final double k = 300;

    public RAVE(int budget, Color playerColor, Random random) {
        this.budget = budget;
        this.playerColor = playerColor;
        this.random = random;
        this.dfs = new DFS();
        this.nodes = new CacheMap<>(10000);
    }

    public RAVE(int budget, Color playerColor) {
        this(budget, playerColor, new Random());
    }

    public Position getBestMove(Board board) {
        this.redEnds = board.getRedEnds();
        this.blueEnds = board.getBlueEnds();

        State s = new State(Board.cloneGrid(board.getGrid()));
        NodeRave root = new NodeRave(s);

        for (int i = 0; i < budget; i++) {
            NodeRave leaf = selectAndExpand(root);
            SimulationResult simResult = simulation(leaf);
            backPropagation(leaf, simResult);
        }
        return getBestPosition(root);
    }

    private Position getBestPosition(NodeRave node) {
        NodeRave best = null;
        double maxVisits = -Double.MAX_VALUE;
        for (NodeRave child : node.getChildren()) {
            if (child.getVisits() > maxVisits) {
                maxVisits = child.getVisits();
                best = child;
            }
        }
        return best.getLastMove();
    }

    private NodeRave selectAndExpand(NodeRave root) {
        NodeRave node = root;
        Color currentPlayer = playerColor; // variable locale, pas de champ mutable

        while (true) {
            if (isTerminal(node.getState().getGrid())) {
                return node;
            }

            List<Position> notExplored = node.getMovesNotExplored();
            if (!notExplored.isEmpty()) {
                Position move = notExplored.get(random.nextInt(notExplored.size()));
                NodeRave child = createChild(node, move, currentPlayer); // on passe le joueur
                node.removeExploredMove(move);
                return child;
            }

            node = getBestUcbChild(node);
            currentPlayer = currentPlayer.getOppositeColor(); // toggle local après la descente
        }
    }

    private NodeRave createChild(NodeRave parent, Position move, Color currentPlayer) {
        Color[][] grid = Board.cloneGrid(parent.getState().getGrid());
        grid[move.getX()][move.getY()] = currentPlayer; // utilise le paramètre
        State newState = new State(grid);

        NodeRave child;
        if (nodes.containsKey(newState)) {
            child = nodes.get(newState);
        } else {
            child = new NodeRave(newState);
            nodes.put(newState, child);
        }
        child.setLastMove(move);
        child.setParent(parent);
        parent.addChild(child);
        return child;
    }

    private SimulationResult simulation(NodeRave node) {
        Color[][] grid = Board.cloneGrid(node.getState().getGrid());
        Color actual = playerColor;
        List<Position> simulationMoves = new ArrayList<>();
        List<Position> moves = new ArrayList<>(node.getState().getPossibleMoves());

        while (!moves.isEmpty()) {
            Position move = moves.get(random.nextInt(moves.size()));
            grid[move.getX()][move.getY()] = actual;
            simulationMoves.add(move);
            moves.remove(move);
            actual = actual.getOppositeColor();
        }

        boolean win = isAWin(playerColor, grid);
        return new SimulationResult(win, simulationMoves);
    }

    private void backPropagation(NodeRave node, SimulationResult simResult) {
        boolean result = simResult.getResult();
        List<Position> moves = simResult.getMoves();
        int taille = moves.size() - 1;
        int t = 0;

        while (node != null) {
            if (result) {
                node.victory();
            }
            node.visited();

            if (node.getParent() != null && taille >= 0) {
                int startIdx = Math.max(0, taille - t);
                for (int i = startIdx; i <= taille; i++) {
                    Position move = moves.get(i);
                    node.updateRaveStats(move, result);
                }
                t++;
            }

            node = node.getParent();
        }
    }

    private NodeRave getBestUcbChild(NodeRave parent) {
        NodeRave bestChild = null;
        double maxValue = -Double.MAX_VALUE;

        for (NodeRave child : parent.getChildren()) {
            if (child.getVisits() == 0) {
                return child;
            }
            double value = child.getRaveFormula(k);
            if (value > maxValue) {
                maxValue = value;
                bestChild = child;
            }
        }
        return bestChild;
    }

    private boolean isTerminal(Color[][] grid) {
        return dfs.hasPath(grid, redEnds, Color.RED) || dfs.hasPath(grid, blueEnds, Color.BLUE);
    }

    private boolean isAWin(Color c, Color[][] grid) {
        List<Set<Position>> playerEnds = c == Color.RED ? redEnds : blueEnds;
        return dfs.hasPath(grid, playerEnds, c);
    }
}