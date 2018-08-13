package org.dbpedia.mappingschecker.resources;

import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.SparqlReader;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.apache.jena.riot.Lang;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.dbpedia.mappingschecker.web.LangPair;
import org.dbpedia.mappingschecker.web.LockDAO;
import org.dbpedia.mappingschecker.web.TemplateDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
@Path("/templates")
public class TemplatesResource {

    private static Logger logger = LoggerFactory.getLogger(TemplatesResource.class);

    /**
     * Get a list of all templates available
     *
     * needs a query param named "lang"
     *
     * @return
     */
    @GET
    public Response getTemplatesByLang(@Context UriInfo info) {
        String langA = info.getQueryParameters().getFirst("langA");
        String langB = info.getQueryParameters().getFirst("langB");
        if (langA == null || langA.equals("") || langB == null || langB.equals("")) {
            ApiError err = new ApiError("Query param 'langA=' ("+langA+") or langB=("+langB+") is not defined or incorrect", 400);
            return Response.status(400).entity(err).build();
        }

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        try {
            List<TemplateDAO> templates = sqlService.getAllTemplatesByLangPair(new LangPair(langA, langB));

            return Response.status(200)
                    .entity(templates).build();

        } catch (SQLException e) {
            e.printStackTrace();
            ApiError err = new ApiError("Error on SQL: "+e.getMessage(), 500, e);
            return err.toResponse().build();
        }
    }


    /**
     * Get all information of a template: all related mappings
     * @param templateName
     * @return
     */
    @GET
    @Path("/{lang}/{templateName}")
    public Response getAllInfo(@PathParam("templateName") String templateName, @PathParam("lang") String lang, @Context UriInfo info) {
        //String lang = info.getQueryParameters().getFirst("lang");
        if (lang == null || lang.equals("")) {
            ApiError err = new ApiError("Query param 'lang=' ("+lang+") is not defined or incorrect", 400);
            return Response.status(400).entity(err).build();
        }

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        try {
            List<AnnotationDAO> annotations = sqlService.getAnnotationsByTemplateB(templateName, lang);
            List<LockDAO> locks = annotations.stream()
                                            .map((annotation -> annotation.getLocks()))
                                            .flatMap(List::stream)
                                            .collect(Collectors.toList());

            if (annotations.size() == 0) {
                // If no annotations on DB, then template does not exists
                ApiError err = new ApiError("The template " + templateName + " in language " + lang + " does not exists in database", 404);
                return err.toResponse().build();
            } else {
                TemplateDAO template = sqlService.collectTemplateStats(templateName, lang);
                template.setAnnotations(annotations);
                template.setLocks(locks);
                // Find template usages:
                try {
                    SparqlReader reader = new SparqlReader(Utils.getSPARQLEndpoint());
                    long count = reader.getCountTemplateUsage(templateName, lang);
                    template.setTemplateUsages(count);
                } catch (Exception exc) {
                    logger.warn("Could not load number of template usages");
                }

                return Response.status(200).entity(template).build();
            }
        } catch (SQLException sqlex) {
            logger.error("Error caught on GET /templates/templateName");
            ApiError err = new ApiError("Exception caught: "+sqlex.getMessage(), 500, sqlex);
            return err.toResponse().build();
        }
    }


    /**
     *
     * @return A list of language pairs
     */
    @GET
    @Path("/langPairs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableLanguages() {
        SQLAnnotationReader sqlService = new SQLAnnotationReader("jdbc:"+Utils.getMySqlConfig());
        try {
            List<LangPair> pairs = sqlService.getLangPairs();

            if (pairs != null && !pairs.isEmpty()) {
                return Response.status(200).entity(pairs).build();
            } else {
                ApiError err = new ApiError("Unable to read a list of language pairs", 500);
                return err.toResponse().build();
            }
        } catch (SQLException sqlex) {
            ApiError err = new ApiError("Got a SQL error: "+sqlex.getMessage(), 500, sqlex);
            return err.toResponse().build();
        }
    }
}
