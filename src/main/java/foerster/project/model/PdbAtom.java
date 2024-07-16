package foerster.project.model;

import javafx.scene.shape.Sphere;

public class PdbAtom {

    private String type;
    private int id;

    private String chain;
    private PdbResidue parentResidue;

    private String secStruc;
    private PdbAtomicSpecies atomicSpecies;
    private double x;
    private double y;
    private double z;
    private Sphere shape;

    public PdbAtomicSpecies getAtomicSpecies() {
        return atomicSpecies;
    }

    public String getType(){return this.type;}

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getChain(){return chain;}

    public Sphere getShape() {
        return shape;
    }

    public void setShape(Sphere shape) {
        this.shape = shape;
    }

    public PdbAtom(PdbAtomicSpecies atomicSpecies, double x, double y, double z, int id, PdbResidue parentResidue, String type) {
        this.atomicSpecies = atomicSpecies;
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.parentResidue = parentResidue;
        this.shape = null;
        this.chain = parentResidue.getChain();
        this.type = type;
    }

    public void setSecStruc(String secStruc) {
        this.secStruc = secStruc;
    }

    public String getSecStruc() {return this.secStruc;}

    public PdbResidue getParentResidue() {return this.parentResidue;}
}
