package es.upm.oeg.tools.mappings;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.RDFNode;
import org.dbpedia.mappingschecker.resources.UsersResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Copyright 2014-2018 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
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
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 */
public class SPARQLBackend {
    private static Logger logger = LoggerFactory.getLogger(UsersResource.class);

    public static boolean ask(String queryString, String serviceEndpoint) {

        logger.debug("Executing query: {}", queryString);

        Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndpoint, query)) {

            return qexec.execAsk();
        } catch (Exception e) {
            System.out.println("Query :" + queryString);
            throw e;
        }

    }

    public static List<Map<String, RDFNode>> executeQueryForList(String queryString, String serviceEndpoint, Set<String> vars) {

        logger.debug("Executing query: {}", queryString);

        List<Map<String, RDFNode>> resultsList = new ArrayList<>();

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndpoint, query)) {
            {
                ResultSet results = qexec.execSelect();

                // collect all the values
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    Map<String, RDFNode> resultsMap = new HashMap<String, RDFNode>();

                    for (String var : vars) {
                        if (soln.contains(var)) {
                            resultsMap.put(var, soln.get(var));
                        }
                    }

                    resultsList.add(resultsMap);
                }

                return resultsList;
            }
        }
    }


    /**
     * A utility method to extract results when there is only single result variable with multiple values
     * @param queryString
     * @param serviceEndpoint
     * @param var
     * @return
     */
    public static List<RDFNode> executeQueryForList(String queryString, String serviceEndpoint, String var) {

        //logger.debug("Executing query: {}", queryString);

        List<RDFNode> resultsList = new ArrayList<>();

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndpoint, query)) {
            {
                ResultSet results = qexec.execSelect();

                // collect all the values
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    if (soln.contains(var)) {
                        resultsList.add(soln.get(var));
                    }
                }

                return resultsList;
            }
        }  catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /***
     * A utility method to execute SPARQL query when only single valued
     * @param queryString The SPARQL query string
     * @param serviceEndpoint The endpoint to run the query against
     * @param vars the variables from the result
     * @return Map of the the variable and the value from the solution
     */
    public static Map<String, RDFNode> executeQueryForMap(String queryString, String serviceEndpoint, Set<String> vars) {

        logger.debug("Executing query: {}", queryString);

        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(serviceEndpoint, query)) {
            {
                ResultSet results = qexec.execSelect();

                // We only return the first solutions, may be a map of map
                for (; results.hasNext(); ) {
                    QuerySolution soln = results.nextSolution();
                    Map<String, RDFNode> resultsMap = new HashMap<String, RDFNode>();

                    for (String var : vars) {
                        if (soln.contains(var)) {
                            resultsMap.put(var, soln.get(var));
                        }
                    }

                    return resultsMap;
                }
            }

            // No solutions found
            return Collections.EMPTY_MAP;
        }
    }
}
