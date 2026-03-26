package Game;
import java.util.*;

public class Position {

    private int x;
    private int y;
    

    public Position (int x, int y) {
        this.x = x;
        this.y = y;
        
    }

    public int getX () {
        return this.x;
    }

    public int getY () {
        return this.y;
    }


   

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Position)) return false;

        Position p = (Position) obj;

        return x == p.getX() && y == p.getY();
    }

    @Override
    public String toString() {
        return "("+x+", "+y+")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(x,y);
    }

}
