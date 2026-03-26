import java.util.*;
import Game.*;
import Rave.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        
        int rounds = 1; // on a utilisé 150 pour l'experimentation
        int redWins = 0;
        int blueWins = 0;

        if(args.length < 6){
            System.out.println("Usage: <size> <startingPlayer> <budgetRED> <algoRED> <budgetBlue> <algoBLUE>");
            return;
        }

        try {
            int size = Integer.parseInt(args[0]);
            Color startingPlayer = args[1].equalsIgnoreCase("red") ? Color.RED : Color.BLUE;
            int budgetRED = Integer.parseInt(args[2]);
            String algoRED = args[3];
            int budgetBLUE = Integer.parseInt(args[4]);
            String algoBLUE = args[5];

            Random rand = new Random();

            for (int i = 0; i < rounds; i++) {
                Game game = new Game(size, startingPlayer, budgetRED, algoRED, budgetBLUE, algoBLUE, rand);
                
                game.play(); // remplacer par game.play(); pour la visualisation des plateaux
                
                String winner = (game.getWinner() == Color.RED) ? "RED" : "BLUE";
                System.out.println("DATA | Size:" + size + " | BudgetRED:" + budgetRED + " | BudgetBLUE:" + budgetBLUE + " | Winner:" + winner);
                
                if (winner.equals("RED")) {
                    redWins++;
                } else {
                    blueWins++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}