package Game;

import java.util.*;
public class Main {
    public static void main(String[] args) {
      
        Board board = new Board(4); 
        
        
        Player robotRed = new Robot(Color.RED);
        Player robotBlue = new Robot(Color.BLUE);
        
        
        Game game = new Game(board, robotRed, robotBlue);
        
        
        game.play();
    }
}