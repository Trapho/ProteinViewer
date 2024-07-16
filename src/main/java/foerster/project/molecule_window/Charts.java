package foerster.project.molecule_window;

import foerster.project.model.PdbComplex;
import foerster.project.model.PdbPolymer;
import foerster.project.model.PdbResidue;
import javafx.scene.chart.PieChart;
import javafx.scene.paint.Color;

import java.util.*;

public class Charts {

    /**
     * Computes and returns a PieChart representing the secondary structure composition of a PdbPolymer.
     *
     * @param polymer   The PdbPolymer for which the secondary structure composition is computed.
     * @return          The PieChart representing the secondary structure composition.
     */
    public static PieChart computeBarChartsSecStruc(PdbPolymer polymer){
        Map<String, Double> allSecStruc = new HashMap<>();
        allSecStruc.put("H", 0.0);
        allSecStruc.put("S", 0.0);
        allSecStruc.put("L", 0.0);

        for(PdbResidue residue : polymer.getAllResidues()){
            if(residue.getSecStruc() == null)
                allSecStruc.put("L", allSecStruc.get("L") +1);
            else
                allSecStruc.put(residue.getSecStruc(), allSecStruc.get(residue.getSecStruc()) + 1.0);
        }
        PieChart secStrucPie = new PieChart();
        Double aaNumber = Double.valueOf(polymer.getAllResidues().size());
        for (var set: allSecStruc.entrySet()){

            Double pie = set.getValue()/aaNumber;
            String key = "";
            switch (set.getKey()){
                case "H":
                    key = "Helix"; break;
                case "L":
                    key = "Loop"; break;
                case "S":
                    key = "Sheet"; break;
            }
            secStrucPie.getData().add(new PieChart.Data(key, pie));
            secStrucPie.setTitle("Secondary Structure Composition");
        }
        return secStrucPie;

     }

    /**
     * Computes and returns a PieChart representing the amino acid composition of a PdbPolymer.
     *
     * @param polymer   The PdbPolymer for which the amino acid composition is computed.
     * @return          The PieChart representing the amino acid composition.
     */
    public static PieChart computeBarCharts(PdbPolymer polymer){

        Map<String, Double> allAA = new HashMap<>();
        allAA.put("ALA", 0.0);  // Alanine
        allAA.put("ARG", 0.0);   // Arginine
        allAA.put("ASN", 0.0);  // Asparagine
        allAA.put("ASP", 0.0);   // Aspartic Acid
        allAA.put("CYS", 0.0);    // Cysteine
        allAA.put("GLN", 0.0);   // Glutamine
        allAA.put("GLU", 0.0);    // Glutamic Acid
        allAA.put("GLY", 0.0);    // Glycine
        allAA.put("HIS", 0.0);  // Histidine
        allAA.put("ILE", 0.0);  // Isoleucine
        allAA.put("LEU", 0.0);  // Leucine
        allAA.put("LYS", 0.0);   // Lysine
        allAA.put("MET", 0.0);   // Methionine
        allAA.put("PHE", 0.0);   // Phenylalanine
        allAA.put("PRO", 0.0);  // Proline
        allAA.put("SER", 0.0);  // Serine
        allAA.put("THR", 0.0);  // Threonine
        allAA.put("TRP", 0.0);     // Tryptophan
        allAA.put("TYR", 0.0);   // Tyrosine
        allAA.put("VAL", 0.0);  // Valine



        for(PdbResidue residue : polymer.getAllResidues()){
            allAA.put(residue.getThreeLetter(), allAA.get(residue.getThreeLetter()) + 1.0);
        }

        Double numberOfAA = Double.valueOf(polymer.getAllResidues().size());
        var pieChart = new PieChart();

        for (var set: allAA.entrySet()){

            Double pie = set.getValue()/numberOfAA;
            pieChart.getData().add(new PieChart.Data(set.getKey(), pie));
            pieChart.setTitle("Amino Acid composition");
        }
        return pieChart;

    }


}
