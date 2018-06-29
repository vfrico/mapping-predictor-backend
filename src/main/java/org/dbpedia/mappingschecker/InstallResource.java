package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import org.dbpedia.mappingschecker.util.Utils;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/installation")
public class InstallResource {

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

}
