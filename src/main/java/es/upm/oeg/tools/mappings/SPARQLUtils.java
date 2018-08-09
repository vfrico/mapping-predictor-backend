package es.upm.oeg.tools.mappings;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains methods to obtain and communicate with SPARQL
 */
public class SPARQLUtils {

    private static Map<String, String> rGraphByLang = new HashMap<>();
    private static String defaultRGraph = "http://%s.dbpedia.org/r";

    private static Map<String, String> dbpediaEndpointByLang = new HashMap<>();
    private static String defaultDBpediaEndpoint = "http://%s.dbpedia.org/sparql";

    static {
        rGraphByLang.put("es", "http://es.dbpedia.org/r");
        rGraphByLang.put("en", "http://dbpedia.org/r");

        dbpediaEndpointByLang.put("en", "http://dbpedia.org/sparql");
    }

    public static String getRGraphByLang(String lang) {
        String graphURL = rGraphByLang.get(lang);
        if (graphURL == null) {
            return String.format(defaultRGraph, lang);
        }
        return graphURL;
    }

    public static String getDBpediaEndpointByLang(String lang) {
        String endpoint = dbpediaEndpointByLang.get(lang);
        if (endpoint == null) {
            return String.format(defaultDBpediaEndpoint, lang);
        }
        return endpoint;
    }
}
