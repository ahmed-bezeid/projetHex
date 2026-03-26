package Game;

import Rave.*;         
import java.util.*;
public class RobotRave extends Player {
    private RAVE rave;
     
    public RobotRave(Color c , int budget){
        super(c);
        this.rave = new RAVE(budget, c);
    }

     public RobotRave(Color c , int budget,Random rand){
        super(c);
        this.rave = new RAVE(budget, c, rand);
    }
    
    public RobotRave(Color c){
        this(c,10000);
    }

    @Override
    public Position play(Board board) {
        Position best = rave.getBestMove(board);
        return best;
    }
    
}
