package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.CSVAnnotationReader;
import es.upm.oeg.tools.mappings.beans.AnnotationType;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.dbpedia.mappingschecker.web.VoteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/annotations")
public class AnnotationsResource {
    private static Logger logger = LoggerFactory.getLogger(AnnotationsResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() {
        List<AnnotationDAO> validAnnotations = getAnnotations("en","es");
        if (validAnnotations == null) {
            validAnnotations = new ArrayList<>();
        }
        return Response.status(200)
                .entity(validAnnotations).build();

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCSV() {
        List<AnnotationDAO> validAnnotations = getAnnotations("en","es");
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
    public Response getSQL(@PathParam("id") int id) throws IOException {

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        AnnotationDAO resp = sqlService.getAnnotation(id);
        if (resp != null) {
            return Response.status(200).entity(resp).build();
        } else {
            ApiError err = new ApiError("Annotation id "+id+" not found", 404);
            return Response.status(404).entity(err).build();
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
