package org.dbpedia.mappingschecker.resources;


import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.Timestamp;

@Path("/users")
public class UsersResource {

    private static Logger logger = LoggerFactory.getLogger(UsersResource.class);

    /**
     * Adds a new user to the system. If it exists, it responds with a 4xx response status code
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(UserDAO newUser) {
        newUser.setId(0);
        newUser.setCreation_date(new Timestamp(System.currentTimeMillis()));

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);
        UserDAO dbUser =  sql.getUser(newUser.getUsername());

        logger.info("dbUser: "+dbUser);
        if (dbUser == null) {
            boolean res = sql.addUser(newUser);
            UserDAO addedUser = sql.getUser(newUser.getUsername());
            if (res) {
                return Response.status(201).entity(addedUser).build();
            }

        } else {
            ApiError err = new ApiError("The user "+newUser.getUsername()+" already exists on DB", 412);
            return  err.toResponse().build();
        }
        ApiError err = new ApiError("The user could not been created", 500);
        return err.toResponse().build();
    }

    @DELETE
    @Path("/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("user") String username) {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);
        UserDAO dbUser =  sql.getUser(username);

        logger.info("Delete user "+dbUser);

        if (dbUser == null) {
            ApiError resp = new ApiError("The user "+username+" does not exists", 404);
            return resp.toResponse().build();
        } else {
            boolean res = sql.deleteUser(username);
            if (res) {
                return Response.status(204).build();
            }
        }
        ApiError err = new ApiError("The user could not been deleted", 500);
        return err.toResponse().build();
    }

    
}