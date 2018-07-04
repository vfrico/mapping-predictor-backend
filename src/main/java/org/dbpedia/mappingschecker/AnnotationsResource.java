package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.CSVAnnotationReader;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
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
    public Response get() throws IOException {
        // TODO: generate a proper query to get annotations
        List<AnnotationDAO> list = new ArrayList<>();
        AnnotationDAO anot = (AnnotationDAO)getSQL(15024).getEntity();
        if (anot != null) list.add(anot);
        return Response.status(200)
                .entity(list).build();
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
