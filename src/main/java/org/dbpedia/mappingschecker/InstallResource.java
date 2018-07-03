package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.CSVAnnotationReader;
import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.Annotation;
import org.dbpedia.mappingschecker.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;

@Path("/installation")
public class InstallResource {
    private static Logger logger = LoggerFactory.getLogger(InstallResource.class);

    @GET
    @Path("/testdb")
    public String testDB() {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader n = new SQLAnnotationReader(mysqlConfig);
        if (n.testConnection()) {
            return "Works";
        } else {
            return "Something failed";
        }
    }

    @POST
    @Path("/createtables")
    public String createTables() {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader n = new SQLAnnotationReader(mysqlConfig);
        n.createTables();
        return "Unimplemented";
    }

    @POST
    @Path("/addAllCSV")
    public String addAllCSV() throws IOException {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);

        Boolean combined = true;

        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);
        sql.createTables();

        CSVAnnotationReader csv = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
        for (int i = 1; i < csv.getMaxNumber(); i++) {
            try {
                Annotation ann = csv.getAnnotation(i);
                boolean res = sql.addAnnotation(ann);
                combined &= res;
                if (!res) {
                    logger.warn("This call has returned false!!");
                }

            } catch (IllegalArgumentException iae) {
                logger.warn("Unable to parse annotationId="+i);
            }


        }
        return "Added annotation: "+combined;
    }
}
