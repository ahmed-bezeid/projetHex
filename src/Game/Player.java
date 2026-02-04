package Game;

public abstract class Player {

    protected Color color;


    public Player(Color color){
        this.color = color;
    }

    public Color getColor(){ 
        return color;
        
    }


    public abstract Position play(Board board);

    public String toString(){
        return color.toString();
    }
}