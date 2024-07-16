package foerster.project.model;

import java.util.ArrayList;

public class PdbComplex {
    private ArrayList<PdbPolymer> polymers; // list of all polymers/models
    private PdbPolymer currentPolymer; // current Polymer which is visible

    private String pdbEntry;
    private String pdbID;

    public PdbComplex(){this.polymers = new ArrayList<>();}

    public void setPdbEntry(String pdbEntry) {
        this.pdbEntry = pdbEntry;
    }

    public void setPdbID(String pdbID) {
        this.pdbID = pdbID;
    }

    public String getPdbEntry() {
        return pdbEntry;
    }

    public String getPdbID() {
        return pdbID;
    }

    public void addPolymer(PdbPolymer polymer) {
        this.polymers.add(polymer);
    }

    public ArrayList<PdbPolymer> getPolymers(){
        return this.polymers;
    }

    /**
     * Resets the Model
     */
    public void resetModel(){this.polymers = new ArrayList<>();}

    /**
     * Selects a subset of models/polymers
     * @param from
     * @param to
     */
    public void createSubModel(int from, int to){
        ArrayList<PdbPolymer> submodel = new ArrayList<>();
        for(int i =  from -1; i <= to -1; i++){
            submodel.add(polymers.get(i));
        }
        polymers = submodel;
    }

    /**
     * sets the currentPolymer (the Polymer that is shown)
     * @param i index of the currentPolymer
     */
    public void setCurrentPolymer(int i){
        currentPolymer = polymers.get(i);
    }
    public PdbPolymer getCurrentPolymer(){
        return this.currentPolymer;
    }
}
