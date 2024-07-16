package foerster.project.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;

public class PdbParser {


//TODO: add side chain identification. This should later be used to color bonds

    /**
     * Parses the PDB data and constructs a PdbComplex object with the provided ID and PDB data.
     * The parsed complex is added to the specified PdbComplex object.
     *
     * @param ID        The ID of the PDB entry.
     * @param pdb       The PDB data to parse.
     * @param complex   The PdbComplex object to add the parsed complex to.
     * @throws IOException if an error occurs while reading the PDB data.
     */
    public static void parse(String ID,String pdb, PdbComplex complex) throws IOException{

        complex.setPdbEntry(pdb);
        complex.setPdbID(ID);

        String[] aminoAcids = {
                "ALA", "ARG", "ASN", "ASP", "CYS", "GLN", "GLU", "GLY",
                "HIS", "ILE", "LEU", "LYS", "MET", "PHE", "PRO", "SER",
                "THR", "TRP", "TYR", "VAL"
        };


        try(var r = new BufferedReader(new StringReader(pdb))) {
            PdbPolymer polymer = new PdbPolymer();
            int current_id = -1; // this is needed to recognize that we reached a new aa
            String current_chain = "START";
            String line;
            while ((line = r.readLine()) != null){
                if (line.toUpperCase().startsWith("ENDMDL")){// if we have a file with more than one model
                    compute_bonds(polymer);
                    parseSecStruc(pdb, polymer);
                    complex.addPolymer(polymer);
                    current_chain = "START";
                    current_id = -1;                             // when we reach a new model then we reset the start id and chain
                    polymer =  new PdbPolymer();                 // begin a new Polymer
                }
                if (line.startsWith("ATOM")) {
                    String[] split_line = line.split("\\s+");
                    try{
                        if (Arrays.stream(aminoAcids).toList().contains(split_line[3])) { // skip all non-proteins and lines with errors
                            if (!split_line[4].equals(current_chain)) {
                                current_chain = split_line[4];
                                //befor adding the new monomer test if the last monomer was empty, if yes remove
                                polymer.clearLastMonomerIfEmpty();
                                polymer.addMonomer(new PdbMonomer(current_chain));
                            }
                            if (Integer.parseInt(split_line[5]) != current_id) {
                                current_id = Integer.parseInt(split_line[5]);
                                polymer.getCurrentMonomer().addResidue(new PdbResidue(split_line[3], Integer.parseInt(split_line[5]), split_line[4]));
                            }
                            PdbAtom atom = new PdbAtom(new PdbAtomicSpecies(split_line[2].charAt(0)),
                                    Double.valueOf(split_line[6]),
                                    Double.valueOf(split_line[7]),
                                    Double.valueOf(split_line[8]),
                                    Integer.parseInt(split_line[1]),
                                    polymer.getCurrentMonomer().getCurrentResidue(),
                                    split_line[2]);  // the type (CA, C , CB and so on)
                            if (atom.getAtomicSpecies().getSymbol() != "H") {
                                polymer.getCurrentMonomer().addAtom(atom);
                                polymer.getCurrentMonomer().getCurrentResidue().addAtoms(atom);
                            }
                        }else{ // the atom does not belong to an Amino acid
                            polymer.setNumberOfRemovedAtoms(polymer.getNumberOfRemovedAtoms() + 1);
                        }
                    }catch (Exception e){
                        polymer.setNumberOfErrors(polymer.getNumberOfErrors() + 1);
                    }
                }
            }
            polymer.clearLastMonomerIfEmpty();
            //case: we have only one model and no ENDMDL in the file :
            if (complex.getPolymers().size() == 0){
                compute_bonds(polymer);
                parseSecStruc(pdb, polymer);
                complex.addPolymer(polymer);
            }

        }

    }

    /**
     * Computes the bonds between atoms in the given polymer based on their distances.
     *
     * @param polymer   The polymer to compute the bonds for.
     */
    public static void compute_bonds(PdbPolymer polymer){
        for (PdbMonomer monomer: polymer.getMonomers()){
            var atoms = monomer.getAtoms();
            for(int i = 0; i < atoms.size(); i++){
                for(int j = (i+1); j < atoms.size(); j++){
                    var atom1 = atoms.get(i);
                    var atom2 = atoms.get(j);
                    var distance = get_distance(atom1, atom2);
                    if(atom1.getAtomicSpecies().getSymbol() != "H" | atom2.getAtomicSpecies().getSymbol() != "H"){
                        if(distance <= 200){ // 180 pm typical bond length
                            PdbMonomer.Bond new_bond = new PdbMonomer.Bond(atom1, atom2, atom1.getChain());
                            monomer.getBonds().add(new_bond);
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculates the distance between two atoms using their Cartesian coordinates.
     *
     * @param atom1 The first atom.
     * @param atom2 The second atom.
     * @return The distance between the atoms.
     */
    public static double get_distance(PdbAtom atom1, PdbAtom atom2){
        var distance = Math.sqrt(Math.pow(atom1.getX() - atom2.getX(), 2) +
                Math.pow(atom1.getY() - atom2.getY(), 2) +
                Math.pow(atom1.getZ() - atom2.getZ(), 2))*100; // *1000 due to conversion to pm
        return distance;
    }

    /**
     * Parses the secondary structure information from the PDB data and sets the secondary structure for residues
     * in the given polymer.
     *
     * @param pdb       The PDB data.
     * @param polymer   The polymer to set the secondary structure for.
     * @throws IOException if an error occurs while reading the PDB data.
     */
    public static void parseSecStruc(String pdb, PdbPolymer polymer) throws IOException{
        try(var r = new BufferedReader(new StringReader(pdb))) {
            String line;
            Map<String, Map<Integer, PdbResidue>> chainToResToId = polymer.getChainToResidueToIdMap();
            while ((line = r.readLine()) != null){
                String[] split_line = line.split("\\s+");
                if (split_line[0].equals("HELIX")){
                    try{
                        for (int id = Integer.parseInt(split_line[5]); id <= Integer.parseInt(split_line[8]); id++) {
                            chainToResToId.get(split_line[4]).get(id).setSecStruc("H");
                        }
                    }catch (Exception e){
                        continue;
                    }
                }
                if (split_line[0].equals("SHEET")){
                    try{
                        for (int id = Integer.parseInt(split_line[6]); id <= Integer.parseInt(split_line[9]); id++) {
                            chainToResToId.get(split_line[5]).get(id).setSecStruc("S");
                        }
                    }catch (Exception e){
                        continue;
                    }
                }
            }
        }
    }
}
