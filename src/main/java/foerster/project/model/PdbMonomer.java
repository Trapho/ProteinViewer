package foerster.project.model;

import java.util.ArrayList;

public class PdbMonomer {

    private ArrayList<PdbAtom> atoms;
    private ArrayList<PdbResidue> residues;
    private ArrayList<Bond> bonds;

    private String chain;

    public PdbMonomer(String chain){
        this.chain = chain;
        this.atoms = new ArrayList<>();
        this.residues = new ArrayList<>();
        this.bonds = new ArrayList<>();
    }


    public PdbResidue getCurrentResidue(){
        // residue we are working on
        return this.residues.get(this.residues.size() -1);
    }

    public void addAtom(PdbAtom atom){
        this.atoms.add(atom);
    }
    public void addResidue(PdbResidue residue){
        this.residues.add(residue);
    }
    public void addBond(Bond bond){
        this.bonds.add(bond);
    }

    public ArrayList<PdbAtom> getAtoms() {
        return atoms;
    }

    public ArrayList<PdbResidue> getResidues() {
        return residues;
    }

    public ArrayList<Bond> getBonds() {
        return bonds;
    }

    public String getChain(){return this.chain;}

    public static class Bond{
        private PdbAtom atom1;
        private PdbAtom atom2;

        private String chain;

        Bond(PdbAtom atom1, PdbAtom atom2, String chain){
            this.atom1 = atom1;
            this.atom2 = atom2;
            this.chain = chain;
        }

        public PdbAtom getAtom1() {
            return atom1;
        }

        public PdbAtom getAtom2() {
            return atom2;
        }
    }


}
