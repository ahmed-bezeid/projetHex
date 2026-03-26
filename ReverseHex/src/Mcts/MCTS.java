package Mcts;
import Game.*;


import java.util.*;



public class MCTS {
    private Map<State, NodeMcts> nodes;
    private int budget;
    private Color playerColor;
    private Color player;
    private List<Set<Position>> red_ends;
    private List<Set<Position>> blue_ends;
    private DFS dfs;
    private Random random;


    public MCTS(int budget, Color playerColor,Random random){
        this.budget = budget;
        this.random = random;
        this.playerColor = playerColor;
        this.dfs = new DFS();
        this.nodes = new CacheMap<>(10000);
    }

    public MCTS(int budget, Color playerColor){
        this(budget, playerColor, new Random());
    }
    public Position getBestMove(Board board){
        this.player = playerColor;
        this.red_ends = board.getRedEnds();
        this.blue_ends = board.getBlueEnds();
        
        State s = new State(Board.cloneGrid(board.getGrid()));
        NodeMcts actual = new NodeMcts(s);

        for (int i = 0; i <budget; i++) {
            NodeMcts leaf = selectAndExpand(actual);
            boolean res = simulation(leaf);
            backPropagation(leaf, res);
        }
        return getBestPosition(actual);
    }

    /*noeud le plus visité */
    private Position getBestPosition(NodeMcts node){
        NodeMcts argmax = null;
        double max = Double.MIN_VALUE;

        List<NodeMcts> children = node.getChildren();

        for(NodeMcts child : children){
            double value= child.getVisites();

            if(value>max){
                max=value;
                argmax=child;
            }
        }

    
        if(argmax==null){
            List<Position> availablePosition = node.getMovesNotExplored();
            return availablePosition.get(random.nextInt(availablePosition.size()));
        }
        return argmax.getLastMove();
    
    }

    
/*compare tous les enfants d'un nœud parent en calculant leur score UCB*/
    private NodeMcts getBestUcbChild(NodeMcts parent){
        int N = parent.getVisites();

        NodeMcts argmax = null;
        double max = Double.MIN_VALUE;

        List<NodeMcts> children = parent.getChildren();

        for(NodeMcts child : children){
            int visites = child.getVisites();
            if( visites == 0){
                return child;
            }
            double ucb= (child.getVictories() / (double) visites) + Math.sqrt(2) *  Math.sqrt(Math.log(N)/ (double) visites);

            if(ucb>max){
                max=ucb;
                argmax=child;
            }
        }

        return argmax;
    
    }
/*Descend dans l'arbre via UCB tant que les nœuds sont connus. Si elle trouve une fin de partie, elle s'arrête. Sinon, elle cherche un coup non testé pour l'expansion.*/
    private NodeMcts selectAndExpand(NodeMcts root){
        NodeMcts node = root;
    

        while (true) {
            /*Si le nœud actuel est déjà une victoire, on s'arrête ici.*/
            if(isAWin(node.getState().getGrid())){
                return node;
            }
/*On récupère la liste des coups que l'IA n'a pas encore essayés depuis ce nœud.*/
            List<Position> movesNotExplored = node.getMovesNotExplored();if(!movesNotExplored.isEmpty()){
            Position move = movesNotExplored.get(random.nextInt(movesNotExplored.size()));
                NodeMcts child = createChild(node,move);
                node.removeExploredMove(move);
                return child;
            }
        /* On descend avec UCB */
        NodeMcts nextNode = getBestUcbChild(node);        
        // Si UCB ne trouve rien (plateau plein), on s'arrête au nœud actuel
        if (nextNode == null) {
            return node; 
        }
        node = nextNode;
        player = player.getOppositeColor();
        }
    }

    /*Génère un nouvel état en posant un pion*/
    private NodeMcts createChild(NodeMcts parent, Position move){ //equivalent a expansion 
        Color [][] grid = Board.cloneGrid(parent.getState().getGrid()); 
        grid[move.getX()][move.getY()] = player;
        State state = new State(grid);
        NodeMcts child; 
        if(nodes.containsKey(state)){
            child = nodes.get(state);
        }else{
            child = new NodeMcts(state);
            nodes.put(state,child);
        }
        
        child.setLastMove(move);
        child.setParent(parent);
        parent.addChild(child);

        return child;
    }

  
    private boolean simulation(NodeMcts node){
        Color[][] grid = Board.cloneGrid(node.getState().getGrid());
        Color actual = player.getOppositeColor();      
        /*la liste des coups possibles*/
        List<Position> moves = new ArrayList<>(node.getState().getPossibleMoves());
    
        //mieux que de verifier si isAWin parce que c est plus couteux
        while(!moves.isEmpty()){

            Position p = moves.get(random.nextInt(moves.size()));

            grid[p.getX()][p.getY()] = actual;
            moves.remove(p);   
            actual = actual.getOppositeColor();

        }
          return isAWinInversed(playerColor, grid);
    }

    private void backPropagation(NodeMcts node, boolean result){ 
        while(node != null ){
            
            if(result){
                node.victory();
            }
            
            node.visited();

            node = node.getParent();
            
        
        }

    }

   
public boolean isAWinInversed(Color c, Color[][] grid) {
    Color autre = (c == Color.RED) ? Color.BLUE : Color.RED;
    
    // On regarde si l'AUTRE a fait un chemin
    List<Set<Position>> bordsAutre = (autre == Color.RED) ? red_ends : blue_ends;
    
    // Si l'autre est connecté, le joueur 'c' a GAGNÉ (retourne true)
    return dfs.hasPath(grid, bordsAutre, autre);
}

   public boolean isAWin(Color[][] grid) {
    // La partie s'arrête si l'un DES DEUX est connecté
    return dfs.hasPath(grid, red_ends, Color.RED) || dfs.hasPath(grid, blue_ends, Color.BLUE);
}


    
}


