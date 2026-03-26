package Game;


import java.util.Random;
import java.util.List;


public class Game {

    private Board board;
    private Player playerRed;
    private Player playerBlue;
    private Player currentPlayer;
    private boolean gameOver;

  
   
    public Game(Board board, Player red, Player blue) {
        this.board = board;
        this.playerRed = red;
        this.playerBlue = blue;
        this.currentPlayer = playerRed; 
        this.gameOver = false;
    }

    public void play() {
        System.out.println("Match Robot vs Robot (HEX INVERSÉ) lancé !");
        while (!gameOver) {
            Board.printBoard(board);
            System.out.println("\nTour de " + currentPlayer.getColor());

            Position move = currentPlayer.play(board);
            if (board.isAvailable(move)) {
                board.placePawn(currentPlayer.getColor(), move);
                // On vérifie si quelqu'un a connecté ses bords.
                if (board.isAWin(Color.RED) || board.isAWin(Color.BLUE)) {
    Board.printBoard(board);
    System.out.println("\n🏆 Victoire de " + getWinner());
    gameOver = true;
} else {
                switchPlayer();
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
            }
        }
    }


    /**
     * Utilisé par le Main pour compter les victoires (redWins / blueWins)
     */
  public Color getWinner() {
    // Si Rouge a connecté ses bords, il a perdu -> Bleu gagne
    // On vérifie si Rouge est connecté en utilisant isAWin du point de vue du BLEU
    if (board.isAWin(Color.BLUE)) { 
        return Color.BLUE;
    }
    return Color.RED;
}

    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerRed) ? playerBlue : playerRed;
    }

 
}
