package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.CSVAnnotationReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/annotations")
public class AnnotationsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Annotation get() throws IOException {
        CSVAnnotationReader reader = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
        return reader.getAnnotation(3);
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Annotation get(@PathParam("id") int id) throws IOException {
        CSVAnnotationReader reader = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
        return reader.getAnnotation(id);
    }
}
