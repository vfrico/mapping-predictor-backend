package org.dbpedia.mappingschecker.resources;

import com.mysql.cj.x.protobuf.MysqlxCrud;
import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.CSVAnnotationReader;
import es.upm.oeg.tools.mappings.beans.AnnotationType;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.dbpedia.mappingschecker.web.UserDAO;
import org.dbpedia.mappingschecker.web.VoteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/annotations")
public class AnnotationsResource {
    private static Logger logger = LoggerFactory.getLogger(AnnotationsResource.class);


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

    private static List<AnnotationDAO> getAnnotations(String langA, String langB) {
        List<AnnotationDAO> allAnnotations = null;
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        allAnnotations = sqlService.getAllAnnotations("en", "es");
        // Avoid returning a null object. Instead return an empty array

        List<AnnotationDAO> validAnnotations = new ArrayList<>();

        for (AnnotationDAO annotation : allAnnotations) {
            List<VoteDAO> votes = sqlService.getVotes(annotation.getId());
            AnnotationType votedType = voteAnnotation(annotation, votes);
            if (votedType != null) {
                annotation.setAnnotation(votedType);
                validAnnotations.add(annotation);
            }
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
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
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

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        AnnotationDAO resp = sqlService.getAnnotation(id);
        if (resp != null) {
            return Response.status(200).entity(resp).build();
        } else {
            ApiError err = new ApiError("Annotation id "+id+" not found", 404);
            return err.toResponse().build();
        }
    }

    @POST
    @Path("/{id}/vote")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addVote(VoteDAO vote, @PathParam("id") int id, @HeaderParam("Authorization") String authHeader) {

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
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

        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);

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
                logger.info("User {} had voted previously on {}. Deleting it to insert a new vote", oldVote.getUser().getUsername(), oldVote.getIdvote());
                sqlService.deleteVote(oldVote.getIdvote());
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

}
