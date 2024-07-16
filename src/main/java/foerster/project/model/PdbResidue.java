package foerster.project.model;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class PdbResidue {


    private char oneLetter;
    private String threeLetter;
    private int id;
    private ArrayList<PdbAtom> atoms;
    private String secStruc;

    private String chain;

    /**
     * Constructs a PdbResidue object with the specified three-letter code, ID, and chain.
     *
     * @param threeLetter   The three-letter code of the residue.
     * @param id            The ID of the residue.
     * @param chain         The chain the residue belongs to.
     */
    public PdbResidue(String threeLetter, int id, String chain){
        this.oneLetter = aminoacidmapping.get(threeLetter.toLowerCase());
        this.threeLetter = threeLetter;
        this.id = id;
        this.atoms = new ArrayList<>();
        this.secStruc = null;
        this.chain = chain;


    }
    public void addAtoms(PdbAtom atom){
        this.atoms.add(atom);
    }

    public void setSecStruc(String secStruc){
        this.secStruc = secStruc;
        for (PdbAtom atom: this.atoms){
            atom.setSecStruc(secStruc);
        }
    }

    public char getOneLetter() {
        return oneLetter;
    }

    public String getThreeLetter() {
        return threeLetter;
    }

    public int getId() {
        return id;
    }

    public ArrayList<PdbAtom> getAtoms() {
        return atoms;
    }

    public String getSecStruc() {
        return secStruc;
    }

    public String getChain() {return chain;}
    private Map<String, Character> aminoacidmapping = new HashMap<>(){{
        // Add mappings for each amino acid
        this.put("ala", 'A');
        this.put("arg", 'R');
        this.put("asn", 'N');
        this.put("asp", 'D');
        this.put("cys", 'C');
        this.put("gln", 'Q');
        this.put("glu", 'E');
        this.put("gly", 'G');
        this.put("his", 'H');
        this.put("ile", 'I');
        this.put("leu", 'L');
        this.put("lys", 'K');
        this.put("met", 'M');
        this.put("phe", 'F');
        this.put("pro", 'P');
        this.put("ser", 'S');
        this.put("thr", 'T');
        this.put("trp", 'W');
        this.put("tyr", 'Y');
        this.put("val", 'V');

    }};



}
