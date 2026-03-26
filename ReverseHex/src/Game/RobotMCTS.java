package Game;
import Mcts.*;

import java.util.Random;


public class RobotMCTS extends Player {

    private MCTS mcts;
    
    public RobotMCTS(Color c , int budget){
        super(c);
        this.mcts = new MCTS(budget, c);
    }

    public RobotMCTS(Color c , int budget,Random rand){
        super(c);
        this.mcts = new MCTS(budget, c, rand);
    }
    
    public RobotMCTS(Color c){
        this(c,10_000);
    }


    public Position play(Board board){
        Position best = mcts.getBestMove(board);
        return best;
    }
    
}
