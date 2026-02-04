package Game;

import java.util.*;

public class Robot extends Player {
    private Random rand;
    
    public Robot(Color c) {
        super(c);
        this.rand = new Random();
    } 
    
    public Position play(Board board) {
        int size = board.getSize();
        Position p;
        
        do {
            int x = rand.nextInt(size);
            int y = rand.nextInt(size);
            p = new Position(x, y);
        } while (!board.isAvailable(p));
        
        System.out.println("Robot " + color + " joue en position " + p);
        return p;
    }
}