package Game;

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
        Board.printBoard(board); 
        
        System.out.println("\nTour de " + currentPlayer.getColor());

        
        Position move = currentPlayer.play(board);

        if (board.isAvailable(move)) {
            board.placePawn(currentPlayer.getColor(), move);

            if (board.isAWin(currentPlayer.getColor())) {
                Board.printBoard(board); 
                System.out.println("\n🏆 Victoire de " + currentPlayer.getColor());
                gameOver = true;
            } else {
                switchPlayer();
                
                try { Thread.sleep(500); } catch (InterruptedException e) {}
            }
        }
    }
}
    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerRed) ? playerBlue : playerRed;
    }
}
