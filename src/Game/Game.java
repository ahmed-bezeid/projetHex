package Game;


import java.util.Random;
import java.util.List;


public class Game {

    private Board board;
    private Player playerRed;
    private Player playerBlue;
    private Player currentPlayer;
    private boolean gameOver;

    /**
     * Constructeur principal appelé par le Main.
     * Il cree les robots selon l'algorithme MCTS ou Rave choisi dans le script.
     */
    public Game(int size, Color startingPlayer, int budgetRED, String algoRED, int budgetBLUE, String algoBLUE, Random rand) {
        this.board = new Board(size);
        this.playerRed = createRobot(Color.RED, algoRED, budgetRED, rand);
        this.playerBlue = createRobot(Color.BLUE, algoBLUE, budgetBLUE, rand);
        
        // Définit qui commence selon l'argument du script
        this.currentPlayer = (startingPlayer == Color.RED) ? playerRed : playerBlue;
        this.gameOver = false;
        
        
    }

    /**
     * Ton ancien constructeur (conservé pour compatibilité)
     */
    public Game(Board board, Player red, Player blue) {
        this.board = board;
        this.playerRed = red;
        this.playerBlue = blue;
        this.currentPlayer = playerRed; 
        this.gameOver = false;
    }

    /**
     * Méthode SILENCIEUSE 
     * Elle joue la partie le plus vite possible sans aucun affichage ni pause.
     */
    public void playSilently() {
        while (!gameOver) {
            Position move = currentPlayer.play(board);
            if (move != null && board.isAvailable(move)) {
                board.placePawn(currentPlayer.getColor(), move);
                
                // Utilise la méthode isAWin 
                if (board.isAWin(currentPlayer.getColor())) {
                    gameOver = true;
                } else {
                    switchPlayer();
                }
            } else {
                gameOver = true; 
            }
        }
    }

    /**
     * Utile pour debugger ou voir une démonstration avec pauses.
     */
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


    /**
     * Utilisé par le Main pour compter les victoires 
     */
    public Color getWinner() {
        if (board.isAWin(Color.RED)) return Color.RED;
        return Color.BLUE;
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == playerRed) ? playerBlue : playerRed;
    }

    /**
     * Fabrique de robots : transforme le texte du script en objet Java
     */
    private Player createRobot(Color color, String algo, int budget, Random rand) {
        if (algo.equalsIgnoreCase("RAVE")) {
            return new RobotRave(color, budget, rand);
        } else {
            return new RobotMCTS(color, budget, rand);
        }
    }
}
