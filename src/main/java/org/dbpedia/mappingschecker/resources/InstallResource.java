package org.dbpedia.mappingschecker.resources;

import es.upm.oeg.tools.mappings.CSVAnnotationReader;
import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.SQLException;

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
    public Response createTables() {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader n = new SQLAnnotationReader(mysqlConfig);
        boolean status;

        try {
            status = n.createTables();
        } catch (IOException ioex) {
            ApiError err = new ApiError("The SQL file required can not be found on filesystem", 500, ioex);
            return err.toResponse().build();
        } catch (SQLException sqlex) {
            ApiError err = new ApiError("The DB has found this error: "+sqlex.getMessage(), 500, sqlex);
            return err.toResponse().build();
        }
        if (status) {
            return Response.status(201).build();
        }
        else {
            ApiError err = new ApiError("MySQL could not perform query correctly", 500);
            return err.toResponse().build();
        }

    }

    @POST
    @Path("/addAllCSV")
    public String addAllCSV() throws IOException {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);

        Boolean combined = true;

        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);

        CSVAnnotationReader csv = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
        for (int i = 1; i < csv.getMaxNumber(); i++) {
            try {
                Annotation ann = csv.getAnnotation(i);
                logger.info("Annotation is: "+ann.toCsvString());
                AnnotationDAO res = sql.addAnnotation(ann);
                combined &= res != null;
                if (res == null) {
                    logger.warn("This call has returned false!!");
                    return "ERROR";
                }

            } catch (IllegalArgumentException iae) {
                logger.warn("Unable to parse annotationId="+i);
            }

        }
        return "Added annotation: "+combined;
    }
}
