package org.dbpedia.mappingschecker.resources;


import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.UserDAO;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/users")
public class UsersResource {

    /**
     * Adds a new user to the system. If it exists, it responds with a 4xx response status code
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addUser(UserDAO newUser) {
        SQLAnnotationReader sql = new SQLAnnotationReader(Utils.getMySqlConfig());
        UserDAO dbUser =  sql.getUser(newUser.getUsername());

        if (dbUser != null) {
            boolean res = sql.addUser(newUser);
            if (res) {
                return Response.status(201).entity(newUser).build();
            }

        } else {
            ApiError err = new ApiError("The user "+newUser.getUsername()+" already exists on DB", 412);
            return  err.toResponse().build();
        }
        ApiError err = new ApiError("The user could not been created", 500);
        return err.toResponse().build();
    }
}
