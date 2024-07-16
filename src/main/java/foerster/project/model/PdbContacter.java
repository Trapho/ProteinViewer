package foerster.project.model;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
//import javax.json.*;
import org.json.*;

import javafx.concurrent.Task;






public class PdbContacter {

    /**
     * Retrieves a list of PDB entries from the RCSB PDB database.
     *
     * @param args The command-line arguments (not used in this method).
     * @return An ArrayList containing the PDB entry IDs.
     * @throws IOException if an error occurs while retrieving the data.
     */
    public static ArrayList<String> contact(String[] args) throws IOException {
            var entries = new ArrayList<String>();
            //var url = new URL("https://data.rcsb.org/rest/v1/holdings/current/entry_ids");
            String base = "https://search.rcsb.org/rcsbsearch/v2/query?json=";
            String search = "%7B%22query%22%3A%7B%22type%22%3A%22terminal%22%2C%22label%22%3A%22text%22%2C%22service%22%3A%22text%22%2C%22parameters%22%3A%7B%22attribute%22%3A%22rcsb_entry_info.polymer_entity_count_protein%22%2C%22operator%22%3A%22greater%22%2C%22negation%22%3Afalse%2C%22value%22%3A0%7D%7D%2C%22return_type%22%3A%22entry%22%2C%22request_options%22%3A%7B%22paginate%22%3A%7B%22start%22%3A0%2C%22rows%22%3A201885%7D%2C%22results_content_type%22%3A%5B%22experimental%22%5D%2C%22sort%22%3A%5B%7B%22sort_by%22%3A%22score%22%2C%22direction%22%3A%22desc%22%7D%5D%2C%22scoring_strategy%22%3A%22combined%22%7D%7D";
            //var url = new URL(base +search);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(base +search).openStream(), StandardCharsets.UTF_8)))  {
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                String jsonStr = responseBuilder.toString();
                var jsonobj = new JSONObject(jsonStr);
                var jsonarr = jsonobj.getJSONArray("result_set");
                for (int i = 0; i < jsonarr.length(); i++){
                    entries.add(jsonarr.getJSONObject(i).getString("identifier"));
                }
            }
            return entries;
    }

    /**
     * Task for opening a PDB file and returning its contents as a string.
     */
    public static class OpenEntry extends Task<String> {
        private final File entry;

        /**
         * Creates a new instance of OpenEntry task with the specified file.
         *
         * @param entry The PDB file to open.
         */
        public OpenEntry(File entry){
            this.entry =entry;
        }
        @Override
        public String call () throws IOException{
            var r =new BufferedReader(new FileReader(entry.getPath()));
            var rString = "";
            while(r.ready()){
                rString += (r.readLine() + "\n");
            }
            return rString;
        }


    }

    /**
     * Task for retrieving the contents of a PDB entry from the RCSB PDB database.
     */
    public static class GetEntry extends Task<String> {
        private final String entry;
        /**
         * Creates a new instance of GetEntry task with the specified PDB entry ID.
         *
         * @param entry The ID of the PDB entry to retrieve.
         */
        public  GetEntry(String entry){
            this.entry = entry;
        }

        @Override
        public String call () throws IOException{
            var code = entry.toLowerCase();
            var url = new URL("https://files.rcsb.org/download/" + code + ".pdb");
            var string = new String(getFromURL(url).readAllBytes());
            return string;
        }
    }

    /**
     * Retrieves the input stream from the specified URL.
     *
     * @param url The URL to retrieve the input stream from.
     * @return The input stream from the specified URL.
     * @throws IOException if an error occurs while retrieving the input stream.
     */
    public static InputStream getFromURL(URL url) throws IOException {
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getInputStream();
    }
}