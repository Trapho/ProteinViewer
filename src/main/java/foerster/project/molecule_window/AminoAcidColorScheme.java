package foerster.project.molecule_window;

import foerster.project.model.PdbPolymer;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class AminoAcidColorScheme {

    private Map<Character, Color> colorMap;

    /*
   HashMap Generated using ChatGPT
    */
    public AminoAcidColorScheme() {
        colorMap = new HashMap<>();

        // Assign colors to amino acids
        colorMap.put('A', Color.rgb(165, 204, 226));  // Alanine
        colorMap.put('R', Color.rgb(152, 78, 163));   // Arginine
        colorMap.put('N', Color.rgb(242, 117, 119));  // Asparagine
        colorMap.put('D', Color.rgb(255, 127, 14));   // Aspartic Acid
        colorMap.put('C', Color.rgb(214, 39, 40));    // Cysteine
        colorMap.put('Q', Color.rgb(255, 217, 47));   // Glutamine
        colorMap.put('E', Color.rgb(44, 160, 44));    // Glutamic Acid
        colorMap.put('G', Color.rgb(140, 86, 75));    // Glycine
        colorMap.put('H', Color.rgb(227, 119, 194));  // Histidine
        colorMap.put('I', Color.rgb(148, 103, 189));  // Isoleucine
        colorMap.put('L', Color.rgb(127, 127, 127));  // Leucine
        colorMap.put('K', Color.rgb(188, 189, 34));   // Lysine
        colorMap.put('M', Color.rgb(23, 190, 207));   // Methionine
        colorMap.put('F', Color.rgb(140, 109, 49));   // Phenylalanine
        colorMap.put('P', Color.rgb(227, 119, 194));  // Proline
        colorMap.put('S', Color.rgb(199, 199, 199));  // Serine
        colorMap.put('T', Color.rgb(190, 174, 212));  // Threonine
        colorMap.put('W', Color.rgb(65, 68, 81));     // Tryptophan
        colorMap.put('Y', Color.rgb(253, 180, 98));   // Tyrosine
        colorMap.put('V', Color.rgb(211, 211, 211));  // Valine

    }


    public Color getColorForAminoAcid(char aminoAcid) {
        return colorMap.getOrDefault(Character.toUpperCase(aminoAcid), Color.WHITE);
    }

    /**
     * For a set of monomers generate a set of random colors
     * @param polymer
     * @return
     */
    public Map<String, Color> generateColorsForChains(PdbPolymer polymer){
        Map<String, Color> colorChains = new HashMap<>();
        int min = 20;
        int max = 230;
        for (String chain: polymer.getAllChains()){
            colorChains.put(chain, Color.rgb((int)(Math.random()*(max-min+1)+min),
                    (int)(Math.random()*(max-min+1)+min),
                    (int)(Math.random()*(max-min+1)+min)));
        }
        return  colorChains;
    }
}
