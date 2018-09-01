package es.upm.oeg.tools.mappings;

import com.github.jsonldjava.shaded.com.google.common.collect.Sets;
import es.upm.oeg.tools.mappings.beans.Annotation;
import org.apache.http.HttpException;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.util.SplitIRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
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
 */
public class SparqlReader {
    private static Logger logger = LoggerFactory.getLogger(SparqlReader.class);

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

    private static final String SPARQL_GET_RELATED_SUBJECTS =
            "select distinct ?s ?p1 ?p2 " +
            "where {" +
            "  graph ?rGraphA {" +
            "        ?_x1 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ?s;" +
            "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> ?p1;" +
            "        <http://dbpedia.org/x-template>  ?templateA;" +
            "        <http://dbpedia.org/x-attribute> ?attributeA ." +
            "  }" +
            " " +
            "  graph ?rGraphB {" +
            "   ?_x2 <http://www.w3.org/1999/02/22-rdf-syntax-ns#subject> ?s;" +
            "        <http://www.w3.org/1999/02/22-rdf-syntax-ns#predicate> ?p2;" +
            "        <http://dbpedia.org/x-template>  ?templateB;" +
            "        <http://dbpedia.org/x-attribute> ?attributeB ." +
            "    }" +
            "} limit 20";

    private static final String SPARQL_GET_RELATED_ENTITY =
            "PREFIX dbo: <http://dbpedia.org/ontology/> \n" +
            "select ?object " +
            "where {" +
            "  ?subject ?predicate ?object ." +
            "}";

    private static final String SPARQL_GET_SUBJECT_AND_OBJECT =
            "select ?subject ?object " +
            "where {" +
            "  ?subject ?predicate ?object ." +
            "} limit 10";

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
        //logger.info("Before execution:");
        resultsList = SPARQLBackend.executeQueryForMap(query, this.endpoint, Sets.newHashSet("count"));
        //logger.info("After execution:");
        if (resultsList.size() > 0) {
            count = resultsList.get("count").asLiteral().getLong();
        }
        return count;
    }


    public List<Triple> getTriplesFromB(Annotation annotation) {
        String fullUri = "http://dbpedia.org/ontology/"+SplitIRI.localname(annotation.getPropB());
        RDFNode predicate = new PropertyImpl(fullUri);
        ParameterizedSparqlString parametrizedQuery = new ParameterizedSparqlString();
        parametrizedQuery.setCommandText(SPARQL_GET_SUBJECT_AND_OBJECT);
        parametrizedQuery.setNsPrefix("dbo", "http://dbpedia.org/ontology/");
        parametrizedQuery.setParam("predicate", predicate);
        String query = parametrizedQuery.toString();
        logger.info("SPARQL: "+query);

        List<Map<String, RDFNode>> results;
        results = SPARQLBackend.executeQueryForList(query, SPARQLUtils.getDBpediaEndpointByLang(annotation.getLangB()),
                Sets.newHashSet("subject", "object"));

        List<Triple> helperTriples = new ArrayList<>();

        for (Map<String, RDFNode> result : results) {
            RDFNode subject = result.get("subject");
            RDFNode object = result.get("object");
            helperTriples.add(new Triple(subject.asNode(), predicate.asNode(), object.asNode()));
        }
        return helperTriples;
    }

    public List<Triple> getTriplesFromA(Annotation annotation) {
        String fullUri = "http://dbpedia.org/ontology/"+SplitIRI.localname(annotation.getPropA());
        RDFNode predicate = new PropertyImpl(fullUri);
        ParameterizedSparqlString parametrizedQuery = new ParameterizedSparqlString();
        parametrizedQuery.setCommandText(SPARQL_GET_SUBJECT_AND_OBJECT);
        parametrizedQuery.setNsPrefix("dbo", "http://dbpedia.org/ontology/");
        parametrizedQuery.setParam("predicate", predicate);
        String query = parametrizedQuery.toString();
        logger.info("SPARQL: "+query);

        List<Map<String, RDFNode>> results;
        results = SPARQLBackend.executeQueryForList(query, SPARQLUtils.getDBpediaEndpointByLang(annotation.getLangA()),
                Sets.newHashSet("subject", "object"));

        List<Triple> helperTriples = new ArrayList<>();

        for (Map<String, RDFNode> result : results) {
            RDFNode subject = result.get("subject");
            RDFNode object = result.get("object");
            helperTriples.add(new Triple(subject.asNode(), predicate.asNode(), object.asNode()));
        }
        return helperTriples;
    }

    public List<Triple> getAnnotationHelp(Annotation annotation) {
        ParameterizedSparqlString parametrizedQuery = new ParameterizedSparqlString();
        parametrizedQuery.setCommandText(SPARQL_GET_RELATED_SUBJECTS);
        parametrizedQuery.setIri("rGraphA", SPARQLUtils.getRGraphByLang(annotation.getLangA()));
        parametrizedQuery.setIri("rGraphB", SPARQLUtils.getRGraphByLang(annotation.getLangB()));
        parametrizedQuery.setLiteral("templateA", annotation.getTemplateA());
        parametrizedQuery.setLiteral("templateB", annotation.getTemplateB());
        parametrizedQuery.setLiteral("attributeA", annotation.getAttributeA());
        parametrizedQuery.setLiteral("attributeB", annotation.getAttributeB());
        String query = parametrizedQuery.toString();
        logger.info("SPARQL: "+query);
        List<Map<String, RDFNode>> results ;
        results = SPARQLBackend.executeQueryForList(query, this.endpoint, Sets.newHashSet("s", "p1", "p2"));

        List<Triple> helperTriples = new ArrayList<>();
        String endpointLangA = SPARQLUtils.getDBpediaEndpointByLang(annotation.getLangA());
        String endpointLangB = SPARQLUtils.getDBpediaEndpointByLang(annotation.getLangB());

        logger.info("Results are: "+results);
        for (Map<String, RDFNode> result : results) {
            RDFNode subject = result.get("s");
            RDFNode predicateA = result.get("p1");
            RDFNode predicateB = result.get("p2");
            RDFNode objectA = getObjectFromDBpedia(subject, predicateA, endpointLangA);
            RDFNode objectB = getObjectFromDBpedia(subject, predicateB, endpointLangB);
            if (subject != null && predicateA != null && objectA != null) {
                helperTriples.add(new Triple(subject.asNode(), predicateA.asNode(), objectA.asNode()));
            }
            if (subject != null && predicateB != null && objectB != null) {
                helperTriples.add(new Triple(subject.asNode(), predicateB.asNode(), objectB.asNode()));
            }
        }
        return helperTriples;
    }


    private RDFNode getObjectFromDBpedia(RDFNode subject, RDFNode predicate, String endpoint) {
        try {
            ParameterizedSparqlString parameterizedSparqlQuery = new ParameterizedSparqlString();
            parameterizedSparqlQuery.setCommandText(SPARQL_GET_RELATED_ENTITY);
            parameterizedSparqlQuery.setIri("subject", subject.toString());
            parameterizedSparqlQuery.setIri("predicate", predicate.toString());
            String query = parameterizedSparqlQuery.toString();
            logger.info("Query is: " + query + " and goes to endpoint: " + endpoint);
            Map<String, RDFNode> result;
            result = SPARQLBackend.executeQueryForMap(query, endpoint, Sets.newHashSet("object"));
            logger.info("Result is: " + result);
            return result.get("object");
        } catch (Exception exc) {
            logger.warn("HTTP query failed: {}", exc);
            return null;
        }
    }
}
