package foerster.project.model;

import javafx.scene.paint.Color;

public class PdbAtomicSpecies {

    private String symbol;
    private int radiusPM;
    private Color color;
    private char atomType;

    PdbAtomicSpecies(char atomType){
        this.atomType = atomType;
        if (atomType == 'C'){
            this.symbol = "C";
            this.radiusPM = 170;
            this.color = Color.GRAY;
        }else if(atomType == 'H'){
            this.symbol = "H";
            this.radiusPM = 120;
            this.color = Color.WHITE;
        } else if (atomType == 'O'){
            this.symbol = "O";
            this.radiusPM = 152;
            this.color = Color.RED;
        } else if (atomType == 'S'){
            this.symbol = "S";
            this.radiusPM = 260;
            this.color = Color.YELLOW;
        }else if (atomType == 'N'){
            this.symbol = "N";
            this.radiusPM = 150;
            this.color = Color.BLUE;
        }else{
            this.symbol = null;
            this.radiusPM = 0;
            this.color = null;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public int getRadiusPM() {
        return radiusPM;
    }

    public Color getColor() {
        return color;
    }

    public char getAtomType() {
        return atomType;
    }
}
