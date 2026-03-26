package Mcts;
import Game.*;
import java.util.*;



public class NodeMcts {
    private int victories,visites;
    private State state;
    private List<NodeMcts> children;
    private NodeMcts parent;
    private Position lastMove;

    private List<Position> movesNotExplored;
    

    public NodeMcts(State state){
        this.victories = 0;
        this.visites = 0;
        this.state = state;
        this.children = new ArrayList<>();
        this.parent = null;
        this.lastMove = null;

        this.movesNotExplored = new ArrayList<>(this.state.getPossibleMoves());
    }

    public void setLastMove(Position pos){
        this.lastMove = pos;
    }

    public Position getLastMove(){
        return this.lastMove;
    }

    public void setParent(NodeMcts parent) {
        this.parent = parent;
    }

    public List<Position> getMovesNotExplored(){
        return this.movesNotExplored;
    }

    public void removeExploredMove(Position p){
        this.movesNotExplored.remove(p);
    }

    public List<NodeMcts> getChildren() {
        return children;
    }

    public NodeMcts getParent() {
        return parent;
    }

    public State getState() {
        return state;
    }

    public int getVictories() {
        return victories;
    }

    public int getVisites() {
        return visites;
    }

    public void visited(){
        visites++;
    }

    public void victory(){
        victories++;
    }

    public double ucb(){
        if(visites == 0){
            return Double.MAX_VALUE;
        }
        return (victories / (double) visites) + Math.sqrt(2) *  Math.sqrt(Math.log(parent.getVisites())/(double)visites);
    }

    public void addChild(NodeMcts child){
        this.children.add(child);
    }

    @Override
    public boolean equals(Object obj) { 
        if(obj == null || !(obj instanceof NodeMcts)){
            return false;
        }

        NodeMcts n = (NodeMcts) obj;
        return n.getState().equals(this.state);  
    }


    @Override
    public int hashCode() { //a verifier
        return Objects.hash(state);
    }


    @Override
    public String toString() {
        return "("+victories+"/"+visites+")  "; 
    }
    
}
