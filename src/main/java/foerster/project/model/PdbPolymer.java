package foerster.project.model;

import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PdbPolymer {

    private int numberOfErrors;
    private int numberOfRemovedAtoms;

    private ArrayList<PdbMonomer> monomers;

    public PdbPolymer(){

        this.monomers = new ArrayList<>();
        this.numberOfErrors =0;
        this.numberOfRemovedAtoms = 0;


    }

    public void addMonomer(PdbMonomer monomer){
        this.monomers.add(monomer);
    }


    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    public int getNumberOfRemovedAtoms() {
        return numberOfRemovedAtoms;
    }

    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }

    public void setNumberOfRemovedAtoms(int numberOfRemovedAtoms) {
        this.numberOfRemovedAtoms = numberOfRemovedAtoms;
    }

    public ArrayList<PdbMonomer> getMonomers() {
        return monomers;
    }


    /**
     * Get the last added monomerEntry
     * @return PdbMonomer which was last added to the polymer
     */
    public PdbMonomer getCurrentMonomer(){
        //returns the last monomer in the list, which is the element we are currently working on in the parser
        return this.monomers.get(this.monomers.size() - 1);
    }

    /**
     * Checks if the last added monomer-entry is empty. If yes the monomer is removed
     * Edgecase: The list has to be at least 1
     */
    public void clearLastMonomerIfEmpty(){
        if (this.monomers.size() > 0){
            if (this.monomers.get(this.monomers.size()-1).getResidues().size() == 0)
                this.monomers.remove(this.monomers.size()-1);
        }
    }

    /**
     *
     * @return List of all PdbResidues in the model
     */
    public ArrayList<PdbResidue> getAllResidues(){
        ArrayList<PdbResidue> r = new ArrayList<>();
        for (PdbMonomer monomer: this.monomers){
            r.addAll(monomer.getResidues());
        }
        return r;
    }

    /**
     * Get atoms from all monomers
     * @return list of PdbAtoms
     */
    public ArrayList<PdbAtom> getAllAtoms(){
        ArrayList<PdbAtom> r= new ArrayList<>();
        for (PdbMonomer monomer: this.monomers){
            r.addAll(monomer.getAtoms());
        }
        return r;
    }

    /**
     * get all bonds of all monomers
     * @return list of PdbMonomer.Bond
     */
    public ArrayList<PdbMonomer.Bond> getAllBonds(){
        ArrayList<PdbMonomer.Bond> r = new ArrayList<>();
        for (PdbMonomer monomer: this.monomers){
            r.addAll(monomer.getBonds());
        }
        return  r;
    }

    /**
     * Generates a Map that maps monomers/chains to their Id
     * @return
     */
    public Map<String, Map<Integer, PdbResidue>> getChainToResidueToIdMap(){
        Map<String, Map<Integer, PdbResidue>> chainToResidueMap = new HashMap<String, Map<Integer, PdbResidue>>();

        for (PdbMonomer monomer: this.getMonomers()){
            Map<Integer, PdbResidue> residueToIdMap = new HashMap<Integer, PdbResidue>();
            for (PdbResidue residue: monomer.getResidues()){
                residueToIdMap.put(residue.getId(), residue);
            }
            chainToResidueMap.put(monomer.getChain(), residueToIdMap);
        }
        return chainToResidueMap;
    }

    /**
     * Generate a List of the chainId of each monomer
     * @return List of Chain Ids
     */
    public ArrayList<String> getAllChains(){
        ArrayList<String> chains = new ArrayList<>();
        for (PdbMonomer monomer: this.monomers){
            chains.add(monomer.getChain());
        }
        return chains;
    }

    /**
     * Generates a List of Pairs of PdbResidues and their corresponding secondary structure
     * @return said List
     */
    public ArrayList<Pair<PdbResidue, String>> getSequenceandSecStruc(){
        ArrayList<Pair<PdbResidue, String>> output = new ArrayList<>();
        for (PdbMonomer monomer: this.monomers){
            if (monomer.getResidues().size() != 0)  // in case a chain is empty bc it contained non-amino acids
                output.add(new Pair<PdbResidue, String>(null, "<" + monomer.getChain() + ">"));
            for (PdbResidue res: monomer.getResidues()){
                String sec;
                if(res.getSecStruc() == null)
                    sec = "L";
                else
                    sec = res.getSecStruc();
                output.add(new Pair<>(res, res.getOneLetter() + "\n" + sec));

            }
        }
        return output;
    }

}
