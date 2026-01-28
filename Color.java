import java.util.*;
public enum Color {
    RED("R"),
    BLUE("B"),
    NONE(".");

    private String text;

    Color(String text){
        this.text = text;
    }

    public String getColorText(){
        return text;
    }

    public Color getOppositeColor(){
        return this == RED ? BLUE : this == BLUE ? RED : NONE;
    }
}

