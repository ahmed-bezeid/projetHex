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
        this.currentPlayer = playerRed; // RED commence
        this.gameOver = false;
    }
public void play() {
    System.out.println("Match Robot vs Robot lancé !");
    
    while (!gameOver) {
        // Affiche le plateau à chaque tour
        Board.printBoard(board); // Correction: Appeler la méthode statique
        
        System.out.println("\nTour de " + currentPlayer.getColor());

        // Le robot choisit son coup
        Position move = currentPlayer.play(board);

        // On vérifie et on joue
        if (board.isAvailable(move)) {
            board.placePawn(currentPlayer.getColor(), move);

            if (board.isAWin(currentPlayer.getColor())) {
                Board.printBoard(board); // Affiche le plateau final
                System.out.println("\n🏆 Victoire de " + currentPlayer.getColor());
                gameOver = true;
            } else {
                switchPlayer();
                
                // Petite pause pour voir les robots jouer
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        }
    }
}
    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerRed) ? playerBlue : playerRed;
    }
}
