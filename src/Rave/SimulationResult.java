package Rave;
import Game.*;
import Mcts.*;
import java.util.*;

public class SimulationResult {

        private boolean result;
        private List<Position> moves;
        
        public SimulationResult(boolean result, List<Position> moves) {
            this.result = result;
            this.moves = moves;
        }

        public List<Position> getMoves() {
            return moves;
        }

        public boolean getResult() {
            return result;
        }
}
