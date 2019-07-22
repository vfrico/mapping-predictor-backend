package org.dbpedia.mappingschecker.resources;

import es.upm.oeg.tools.mappings.*;
import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.AnnotationType;
import es.upm.oeg.tools.mappings.beans.ApiError;
import es.upm.oeg.tools.mappings.beans.ClassificationResult;
import org.apache.jena.graph.Triple;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
@Path("/annotations")
public class AnnotationsResource {
    private static Logger logger = LoggerFactory.getLogger(AnnotationsResource.class);

    private static SQLAnnotationReader sqlService = new SQLAnnotationReader("jdbc:"+Utils.getMySqlConfig());

    private static int DAY = 24 * 60 * 60 * 1000;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context UriInfo info) {
        String langA = info.getQueryParameters().getFirst("langa");
        String langB = info.getQueryParameters().getFirst("langb");
        if (langA == null || langA.equals("") || langB == null || langB.equals("")) {
            ApiError err = new ApiError("Parameters langA (="+langA+") and/or " +
                    "langB (="+langB+") are not defined", 400);
            return Response.status(400).entity(err).build();
        }
        List<AnnotationDAO> validAnnotations = getAnnotations(langA,langB);
        if (validAnnotations == null) {
            validAnnotations = new ArrayList<>();
        }
        return Response.status(200)
                .entity(validAnnotations).build();

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCSV(@Context UriInfo info) {
        String langA = info.getQueryParameters().getFirst("langa");
        String langB = info.getQueryParameters().getFirst("langb");
        if (langA == null || langA.equals("") || langB == null || langB.equals("")) {
            ApiError err = new ApiError("Parameters langA (="+langA+") and/or " +
                    "langB (="+langB+") are not defined", 400);
            return Response.status(400).entity(err).build();
        }
        List<AnnotationDAO> validAnnotations = getAnnotations(langA,langB);
        if (validAnnotations == null) {
            validAnnotations = new ArrayList<>();
        }
        StringBuilder csv = new StringBuilder();
        // Header line
        csv.append(Annotation.headerCSV());
        csv.append("\n");

        validAnnotations.forEach((annotation) -> {
            csv.append(annotation.toCsvString());
            csv.append("\n");
        });
        return Response.status(200)
                .entity(csv.toString()).build();

    }

    /**
     * Returns an AnnotationType from a list of votes
     *
     * The logic of returning a valid annotation/vote should be done
     *
     * If has been done by "default" user, then consider the annotation as absolutely valid
     * else:
     *    only take in account annotations with more than three votes, where the vote is the same
     *
     * @param annotation
     * @param votes
     * @return
     */
    private static AnnotationType voteAnnotation(Annotation annotation, List<VoteDAO> votes) {
        AnnotationType votedAnnotation = null;
        if (!(votes == null || votes.size() <= 0)) {
            for (VoteDAO vote : votes) {
                // TODO: More intelligent annotation
                votedAnnotation = vote.getVote();
            }
        }
        return votedAnnotation;
    }

    public static List<AnnotationDAO> getAnnotations(String langA, String langB) {
        List<AnnotationDAO> allAnnotations = null;
        allAnnotations = sqlService.getAllAnnotations(langA, langB);
        // Avoid returning a null object. Instead return an empty array

        List<AnnotationDAO> validAnnotations = new ArrayList<>();

        for (AnnotationDAO annotation : allAnnotations) {
            List<VoteDAO> votes = sqlService.getVotes(annotation.getId());
            AnnotationType votedType = voteAnnotation(annotation, votes);
            if (votedType != null) {
                annotation.setAnnotation(votedType);
            }
            annotation.setVotes(votes);
            validAnnotations.add(annotation);
        }

        if (validAnnotations == null) {
            validAnnotations = new ArrayList<>();
        }
        return validAnnotations;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addAnnotationSQL(Annotation annotation) throws IOException {
        logger.info("Add annotation: "+annotation);
        AnnotationDAO inserted = sqlService.addAnnotation(annotation);
        if (inserted != null) {
            return Response.status(201)
                    .header("Location", "annotations/"+inserted.getId())
                    .entity(inserted).build();
        } else {
            ApiError err = new ApiError("Unable to insert DAO on database", 400);
            return Response.status(400)
                    .entity(err).build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSQL(@PathParam("id") int id)  {
        AnnotationDAO resp = sqlService.getAnnotation(id);
        if (resp != null) {
            return Response.status(200).entity(resp).build();
        } else {
            ApiError err = new ApiError("Annotation id "+id+" not found", 404);
            return err.toResponse().build();
        }
    }


    @GET
    @Path("/{id}/helpers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelpers(@PathParam("id") int id)  {
        AnnotationDAO resp = sqlService.getAnnotation(id);
        if (resp != null) {
            SparqlReader reader = new SparqlReader(Utils.getSPARQLEndpoint());
            //List<Triple> triplesJena = reader.getAnnotationHelp(resp);
            Map<String, List<Triple>> triples = reader.getHelpersTriple(resp);
            List<TripleDAO> triplesA = triples.get(resp.getLangA()).stream()
                                              .map(TripleDAO::new).collect(Collectors.toList());
            List<TripleDAO> triplesB = triples.get(resp.getLangB()).stream()
                                              .map(TripleDAO::new).collect(Collectors.toList());


            AnnotationHelperDAO helper = new AnnotationHelperDAO();
            helper.setRelatedTriplesA(triplesA);
            helper.setRelatedTriplesB(triplesB);
            List<TripleDAO> allTriples = new ArrayList<>(triplesA);
            allTriples.addAll(triplesB);
            helper.setRelatedTriples(allTriples);
            helper.setLangA(resp.getLangA());
            helper.setLangB(resp.getLangB());
            return Response.status(200).entity(helper).build();
        } else {
            ApiError err = new ApiError("Annotation id "+id+" not found", 404);
            return err.toResponse().build();
        }
    }

    @GET
    @Path("/{id}/helpers_old")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHelpersOLD(@PathParam("id") int id)  {
        AnnotationDAO resp = sqlService.getAnnotation(id);
        if (resp != null) {
            SparqlReader reader = new SparqlReader(Utils.getSPARQLEndpoint());
            //List<Triple> triplesJena = reader.getAnnotationHelp(resp);
            List<Triple> triplesJenaForA = reader.getTriplesFromA(resp);
            List<Triple> triplesJenaForB = reader.getTriplesFromB(resp);
            List<TripleDAO> triplesA = triplesJenaForA.stream().map(TripleDAO::new).collect(Collectors.toList());
            List<TripleDAO> triplesB = triplesJenaForB.stream().map(TripleDAO::new).collect(Collectors.toList());
            AnnotationHelperDAO helper = new AnnotationHelperDAO();
            helper.setRelatedTriplesA(triplesA);
            helper.setRelatedTriplesB(triplesB);
            List<TripleDAO> allTriples = new ArrayList<>(triplesA);
            allTriples.addAll(triplesB);
            helper.setRelatedTriples(allTriples);
            helper.setLangA(resp.getLangA());
            helper.setLangB(resp.getLangB());
            return Response.status(200).entity(helper).build();
        } else {
            ApiError err = new ApiError("Annotation id "+id+" not found", 404);
            return err.toResponse().build();
        }
    }

    @DELETE
    @Path("/{id}/lock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unlockAnnotation(@PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {
        // Check if user is valid
        if (sqlService.getToken(Utils.getUsername(authHeader)).equals(authHeader)) {
            try {
                sqlService.unlockAnnotation(id, Utils.getUsername(authHeader));
                return Response.status(204).build();
            } catch (SQLException sql) {
                ApiError err = new ApiError("Unexpected error on DB when deleting lock: "+sql.getMessage(), 500, sql);
                return err.toResponse().build();
            }
        }
        ApiError unauth = new ApiError("Unauthorized, or incorrect token.", 401);
        return unauth.toResponse().build();
    }

    @POST
    @Path("/{id}/lock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lockAnnotation(LockDAO lock, @PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {
        // Id annotation
        // user information (from JWT token)
        // date start and end must be provided from the user
        // or if not, start date is now() and end date is one week later
        // Enforce the comprobation that the end date will be one week later

        // Check lock preconditions:
        if (lock != null && lock.isLocked()) {
            long diffTime = lock.getDateEnd() - lock.getDateStart();
            if (diffTime >  7 * DAY) { // One week
                ApiError err = new ApiError("Precondition failed: The maximum time for a lock must be one week", 418);
                return err.toResponse().build();
            }
        } else {
            ApiError err = new ApiError("You must provide a valid lock object on the body", 400);
            return err.toResponse().build();
        }

        // Check if user is valid
        if (authHeader != null && !authHeader.equals("") &&
                Utils.verifyUser(authHeader, lock.getUser().getUsername()) &&
                sqlService.getToken(lock.getUser().getUsername()).equals(authHeader)) {

            try {
                sqlService.setLock(lock, id);
                return Response.status(201).build();
            } catch (SQLException sql) {
                ApiError err = new ApiError("Error when inserting the lock on DB: "+sql.getMessage(), 500, sql);
                return err.toResponse().build();
            }

        } else {
            ApiError err = new ApiError("You are not authroized to lock a mapping", 401);
            return err.toResponse().build();
        }
    }

    @POST
    @Path("/{id}/vote")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVote(VoteDAO vote, @PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {
        logger.info("VOte dao was: "+vote);
        // if vote dao has vote param to null, return 400
        if (vote.getVote() == null) {
            ApiError err = new ApiError("Param vote not correctly defined", 412);
            return err.toResponse().build();
        }

        if (vote.getUser().getUsername() == null || vote.getUser().getUsername().equals("")) {
            ApiError err = new ApiError("Username not defined", 412);
            return err.toResponse().build();
        }

        // Check that the user that tries to create the vote is the same that has the token
        if (authHeader == null || authHeader.equals("") ||
                !Utils.verifyUser(authHeader, vote.getUser().getUsername())) {
            ApiError err = new ApiError("Not authorized to vote", 401);
            return err.toResponse().build();
        }

        AnnotationDAO annotation = sqlService.getAnnotation(id);
        if (annotation == null) {
            ApiError notFound = new ApiError("Annotation with id:"+id+" cannot be found.", 404);
            return notFound.toResponse().build();
        }

        UserDAO user = sqlService.getUser(vote.getUser().getUsername());
        if (user == null) {
            ApiError notFound = new ApiError("User with username:"+vote.getUser().getUsername()+" cannot be found.", 404);
            return notFound.toResponse().build();
        }

        List<VoteDAO> votes = sqlService.getVotes(id);
        for (VoteDAO oldVote : votes) {
            if (oldVote.getUser().getUsername().equals(vote.getUser().getUsername())) {
                logger.info("User {} had voted previously on {}. Deleting it to insert a new vote", oldVote.getUser().getUsername(), oldVote.getIdVote());
                sqlService.deleteVote(oldVote.getIdVote());
            }
        }

        // TODO: Add primary key on DB which is username/annotation_id

        boolean resp = sqlService.addVote(vote);

        if (resp) {
            AnnotationDAO newAnnotation = sqlService.getAnnotation(id);
            return Response.status(201).entity(newAnnotation).build();
        } else {
            ApiError err = new ApiError("Unable to add vote", 500);
            return err.toResponse().build();
        }
    }

    @GET
    @Path("/csv/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Deprecated
    public Annotation get(@PathParam("id") int id) throws IOException {
        CSVAnnotationReader reader = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
        return reader.getAnnotation(id);
    }

    @POST
    @Path("/classify")
    public Response classifyMappings(@Context UriInfo info) {
        String langA = info.getQueryParameters().getFirst("langa");
        String langB = info.getQueryParameters().getFirst("langb");
        if (langA == null || langB == null || langA.equals("") || langB.equals("")) {
            ApiError err = new ApiError("You should provide langa and langb query params. Current ones are: langa="+langA+" and langb="+langB+".", 400);
            return err.toResponse().build();
        }
        Map<Annotation, ClassificationResult> classifiedOutput = null;

        // Cálculo de fleiss kappa
        FleissKappa fleiss;

        // Classfify annotations
        try {
            List<AnnotationDAO> anots = getAnnotations(langA, langB);
            Classifier c = new Classifier();
            List<Annotation> annotations = new ArrayList<>();
            annotations.addAll(anots);
            logger.info("All annotations: "+anots);
            classifiedOutput = c.classifyFrom(annotations);

            // Iniciamos fleiss kappa
            fleiss = new FleissKappa(anots);
        } catch (Exception exc) {
            logger.error("Se ha encontrado un error: ", exc);
            exc.printStackTrace();
            ApiError error = new ApiError("Error when classifying instances", 500, exc);
            return error.toResponse().build();
        }

        // Count template usages
        logger.warn("START!!");
        SparqlReader reader = new SparqlReader(Utils.getSPARQLEndpoint());
        Set<String> templatesB = classifiedOutput.keySet() // Generates a list with the names of the templates
                .stream().map(ann-> ann.getTemplateB())
                .collect(Collectors.toSet());
        for (String templateB : templatesB) {
            try {
                long count = reader.getCountTemplateUsage(templateB, langB);
                sqlService.insertOrUpdateTemplateInstances(templateB, langB, (int) count);
            } catch (SQLException sqlex) {
                logger.warn("Unable to insert on DB the instances count");
            }
        }
        logger.warn("END!!");

        double fleiss_kappa = fleiss.get();
        logger.info("Calculamos fleiss kappa="+fleiss_kappa);

        // Add classification results
        try {
            boolean success = sqlService.addAllClassificationResults(classifiedOutput);

            /*for (Annotation annotation : classifiedOutput.keySet()) {
                logger.info("Annotation: " + classifiedOutput.get(annotation));

                success &= sqlService.addClassificationResultWithOpenedConnection(((AnnotationDAO) annotation).getId(), classifiedOutput.get(annotation));
            }*/

            if (success) {
                ApiError successResponse = new ApiError("Successfully trained and classified " + classifiedOutput.size() + " instances, on langA="+langA+" and langB="+langB+". Agreement fleiss-kappa="+fleiss_kappa, 201);
                return successResponse.toResponse().build();
            } else {
                ApiError failed = new ApiError("All annotations couldn't successfully been classified", 500);
                return failed.toResponse().build();
            }
        } catch (Exception exc) {
            ApiError error = new ApiError("Something went wrong when trying to add classified annotations: "+exc.getMessage(), 500, exc);
            return error.toResponse().build();
        }
    }

}
