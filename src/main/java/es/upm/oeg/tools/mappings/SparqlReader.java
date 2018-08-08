package es.upm.oeg.tools.mappings;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;
import es.upm.oeg.tools.mappings.beans.Annotation;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.RDFNode;
import org.dbpedia.mappingschecker.resources.UsersResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SparqlReader {
    private static Logger logger = LoggerFactory.getLogger(UsersResource.class);

    private String endpoint;


    private static final String SPARQL_GET_COUNT_BY_TEMPLATE_NAME =
            "SELECT (COUNT(DISTINCT(?s)) AS ?count) " +
            "WHERE {" +
            "    graph ?rGraph {" +
            "    ?_x <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ?s;" +
            "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate>?_p;" +
            "        <http://dbpedia.org/x-template>  ?template;" +
            "        <http://dbpedia.org/x-attribute> ?_a ." +
            "    }" +
            "}";

    public SparqlReader(String endpoint) {
        this.endpoint = endpoint;
    }

    public long getCountTemplateUsage(String template, String lang) {
        long count = 0;

        ParameterizedSparqlString parametrizedQuery = new ParameterizedSparqlString();
        parametrizedQuery.setCommandText(SPARQL_GET_COUNT_BY_TEMPLATE_NAME);
        parametrizedQuery.setIri("rGraph", SPARQLUtils.getRGraphByLang(lang));
        parametrizedQuery.setLiteral("template", template);
        String query = parametrizedQuery.toString();
        Map<String, RDFNode> resultsList;
        logger.info("Before execution:");
        resultsList = SPARQLBackend.executeQueryForMap(query, this.endpoint, Sets.newHashSet("count"));
        logger.info("After execution:");
        if (resultsList.size() > 0) {
            count = resultsList.get("count").asLiteral().getLong();
        }
        return count;
    }

    public void getAnnotationHelp(Annotation annotation) {
        // TODO
    }
}
