package es.upm.oeg.tools.mappings;

import java.util.HashMap;
import java.util.Map;
/**
 * Copyright 2018 Víctor Fernández <vfrico@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Víctor Fernández <vfrico@gmail.com>
 * @since 1.0.0
 *
 * This class contains methods to obtain and communicate with SPARQL
 */
public class SPARQLUtils {

    private static Map<String, String> rGraphByLang = new HashMap<>();
    private static String defaultRGraph = "http://%s.dbpedia.org/r";

    private static Map<String, String> graphByLang = new HashMap<>();
    private static String defaultGraph = "http://%s.dbpedia.org/";

    private static Map<String, String> dbpediaEndpointByLang = new HashMap<>();
    private static String defaultDBpediaEndpoint = "http://%s.dbpedia.org/sparql";

    static {
        rGraphByLang.put("es", "http://es.dbpedia.org/r");
        rGraphByLang.put("en", "http://dbpedia.org/r");

        graphByLang.put("es", "http://es.dbpedia.org/");
        graphByLang.put("en", "http://dbpedia.org/");

        dbpediaEndpointByLang.put("en", "http://dbpedia.org/sparql");
    }

    public static String getGraphByLang(String lang) {
        String graphURL = graphByLang.get(lang);
        if (graphURL == null) {
            return String.format(defaultGraph, lang);
        }
        return graphURL;
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
