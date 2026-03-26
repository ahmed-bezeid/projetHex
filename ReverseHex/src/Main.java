import java.util.*;
import Game.*;
import Rave.*;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int size = 8; 
        Random rand = new Random();
        
        System.out.println("=== TEST HEX INVERSÉ (REVERSE HEX) ===");
        System.out.println("Règle : Celui qui connecte ses deux bords A PERDU.");
        System.out.println("----------------------------------------------");

        // On donne un budget de 1000 simulations à chaque MCTS;
        Player rouge = new RobotRave(Color.RED, 1000, rand);
        Player bleu = new RobotMCTS(Color.BLUE, 1000, rand);

        // On utilise le constructeur simple : Game(Board board, Player red, Player blue)
        Board board = new Board(size);
        Game game = new Game(board, rouge, bleu);

        // 4. Lancement du match visuel
        // --- DÉBUT DU CHRONOMÈTRE ---
        long startTime = System.currentTimeMillis();
        // La méthode play() affichera le plateau 
        game.play();
    // --- FIN DU CHRONOMÈTRE ---
        long endTime = System.currentTimeMillis();
        
        // Calcul du temps total en secondes
        double durationSeconds = (endTime - startTime) / 1000.0;
        // 5. Résultat final
        System.out.println("\n----------------------------------------------");
        System.out.println("Fin du match !");
        System.out.println("Le vainqueur officiel est : " + game.getWinner());
        System.out.printf("Temps total du match : %.2f secondes\n", durationSeconds);
        System.out.println("----------------------------------------------");
    }
}