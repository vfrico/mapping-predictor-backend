package org.dbpedia.mappingschecker.resources;


import com.auth0.jwt.exceptions.JWTVerificationException;
import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.Timestamp;

/**
 * Copyright 2018 Víctor Fernández <vfrico@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Víctor Fernández <vfrico@gmail.com>
 * @since 1.0.0
 */
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

    @GET
    @Path("/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getByUsername(@PathParam("user") String username) {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);
        UserDAO dbUser =  sql.getUser(username);

        logger.info("Get user info"+dbUser);

        if (dbUser == null) {
            ApiError resp = new ApiError("The user "+username+" does not exists", 404);
            return resp.toResponse().build();
        } else {
            UserDAO user = sql.getUser(username);
            if (user != null) {
                return Response.status(200).entity(user).build();
            }
        }
        ApiError err = new ApiError("The user could not been retrieved", 500);
        return err.toResponse().build();
    }

    @PUT
    @Path("/{user}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response modifyUser(UserDAO user, @PathParam("user") String username) {

        SQLAnnotationReader sql = new SQLAnnotationReader("jdbc:"+Utils.getMySqlConfig());
        boolean res = true;
        try {
            if (user.getRole() != null && !user.getRole().equals("")) {
                res &= sql.changeUserRole(username, user.getRole());
                if (!res) {
                    ApiError err = new ApiError("Something wen wrong when changing user role...", 500);
                    return err.toResponse().build();
                }
            }
            if (user.getPassword_md5() != null && !user.getPassword_md5().equals("")) {
                res &= sql.changeUserPassword(username, user.getPassword_md5());
                if (!res) {
                    ApiError err = new ApiError("Something went wrong when changing password...", 500);
                    return err.toResponse().build();
                }
            }

            if (res) {
                UserDAO newUser = sql.getUser(username);
                return Response.status(200).entity(newUser).build();
            } else {
                ApiError err = new ApiError("You should not been seeing this message... Something went bad...", 500);
                return err.toResponse().build();
            }
        } catch (Exception e) {
            ApiError err = new ApiError("Error when editing a user: ", 500, e);
            return err.toResponse().build();
        }
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

    @POST
    @Path("/{user}/login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(UserDAO user) {

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);
        UserDAO dbUser =  sql.getUser(user.getUsername());

        // check login/password
        if (dbUser != null &&
                dbUser.getUsername().equals(user.getUsername()) &&
                dbUser.getPassword_md5().equals(user.getPassword_md5())) {

            logger.info("Token on DB:"+dbUser.getJwt());

            // generate JWT token
            String token;
            if (dbUser.getJwt() == null || dbUser.getJwt().equals("")) {
                token = Utils.getToken(dbUser);
            } else {
                token = dbUser.getJwt();
            }

            logger.info("Token used is: "+token);
            dbUser.setJwt(token);

            boolean res = sql.loginUser(dbUser.getUsername(), token);
            if (res) {
                return Response.status(200)
                        .entity(dbUser)
                        .header("Authorization", token)
                        .build();
            } else {
                ApiError err = new ApiError("Unable to set token on DB", 500);
                return err.toResponse().build();
            }
        } else {
            ApiError err = new ApiError("Incorrect user/password", 400);
            return err.toResponse().build();
        }
    }


    @POST
    @Path("/{user}/logout")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response logout(@PathParam("user") String username, @HeaderParam("Authorization") String authHeader) {

        logger.info("Auth header is: "+authHeader);

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);

        UserDAO dbUser =  sql.getUser(username);

        try {
            // check login/password
            if (dbUser != null && username != null && !username.equals("") &&
                    Utils.verifyUser(authHeader, username) &&
                    dbUser.getJwt().equals(authHeader)) {

                boolean res = sql.logout(dbUser.getUsername());
                if (res) {
                    return Response.status(204).build();
                } else {
                    ApiError err = new ApiError("Unknown server error when deleting token", 500);
                    return err.toResponse().build();
                }

            } else {
                ApiError err = new ApiError("Incorrect user / auth token", 400);
                return err.toResponse().build();
            }
        } catch (JWTVerificationException verifError) {
            ApiError err = new ApiError("Invalid token", 400, verifError);
            return err.toResponse().build();
        }
    }

}
