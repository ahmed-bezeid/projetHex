package Game; 

import java.util.*;

public class State {
    private Map<Position,Color> state;
    private Set<Position> possibleMoves;
    private Color [][] grid;
    private int size;


    public State(Color [][] grid){
        this.grid = grid;
        this.size = grid.length;
        this.possibleMoves = new HashSet<>();
        this.state = new HashMap<>();
       
        int size = grid.length;

        for(int i =0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(grid[i][j] != Color.NONE){
                    this.state.put(new Position(i, j), grid[i][j]);
                }else{
                    this.possibleMoves.add(new Position(i, j));
                }
            }
        }
       
    }


    public Map<Position, Color> getState() {
        return state;
    }

    public Color [][] getGrid(){return this.grid;}


    public Set<Position> getPossibleMoves() {
        return possibleMoves;
    }

    public boolean equals(Object obj){

        if(!(obj instanceof State)) return false;

        State s1 = (State)obj;

        for (Position pos : state.keySet()) {
            if (!s1.getState().containsKey(pos) || !s1.getState().get(pos).equals(state.get(pos))) {
                return false;
            }
        }

        return possibleMoves.equals(s1.getPossibleMoves());

    }


    @Override
    public int hashCode() {
        int result = size;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                result = 31 * result + (grid[i][j] == null ? 0 : grid[i][j].hashCode());
            }
        }
        return result;
    }

    public String toString(){
        return this.state.toString();
    }


    public Map<Position,Color> cloneState(){
        return new HashMap<Position,Color>(state);
    }
    
}