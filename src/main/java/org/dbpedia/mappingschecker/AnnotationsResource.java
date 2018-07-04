package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.CSVAnnotationReader;
import org.dbpedia.mappingschecker.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/annotations")
public class AnnotationsResource {
    private static Logger logger = LoggerFactory.getLogger(AnnotationsResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Annotation get() throws IOException {
        CSVAnnotationReader reader = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
        return reader.getAnnotation(3);
    }

    @GET
    @Path("/csv/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Annotation get(@PathParam("id") int id) throws IOException {
        CSVAnnotationReader reader = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
        return reader.getAnnotation(id);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Annotation getSQL(@PathParam("id") int id) throws IOException {

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        return sqlService.getAnnotation(id);
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addAnnotationSQL(Annotation annotation) throws IOException {
        logger.info("Add annotation: "+annotation);
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        boolean isInserted = sqlService.addAnnotation(annotation);
        return "Inserted: "+isInserted;
    }
}
