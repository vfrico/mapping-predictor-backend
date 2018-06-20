package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.Annotation;
import es.upm.oeg.tools.mappings.CSVReader;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("/annotations")
public class AnnotationsResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Annotation get() {
        try {
            CSVReader reader = new CSVReader("/home/vfrico/anotados.csv");
            return reader.getAnnotation(3);
//            return new Annotation("dbo:height", "dbo:width");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
