package org.dbpedia.mappingschecker.resources;

import es.upm.oeg.tools.mappings.CSVAnnotationReader;
import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Props;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        System.out.println("Ejemplo: áñòp¿?¡!$");

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
    @Path("/fromCSV")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response fromCSV(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @Context UriInfo info) {

        String langA = info.getQueryParameters().getFirst("langa");
        String langB = info.getQueryParameters().getFirst("langb");
        if (langA == null || langB == null || langA.equals("") || langB.equals("")) {
            ApiError err = new ApiError("You should provide langa and langb query params. Current ones are: langa="+langA+" and langb="+langB+".", 400);
            return err.toResponse().build();
        }

        SQLAnnotationReader sql = new SQLAnnotationReader("jdbc:"+Utils.getMySqlConfig());
        logger.info("File details: "+fileDetail.toString());
        CSVAnnotationReader csv = new CSVAnnotationReader(uploadedInputStream, langA, langB);

        return wrapper(csv, sql);

    }

    private Response wrapper(CSVAnnotationReader csv, SQLAnnotationReader sql) {
        Boolean combined = true;
        int added = 0;

        for (int i = 1; i < csv.getMaxNumber(); i++) {
            try {
                Annotation ann = csv.getAnnotation(i);
                AnnotationDAO res = sql.addAnnotation(ann);
                combined &= res != null;
                if (combined) added++;
                else {
                    logger.info("Not added: "+ann);
                }
                if (res == null) {
                    ApiError err = new ApiError("The annotation was readed as null", 500);
                    return err.toResponse().build();
                }
            } catch (IllegalArgumentException iae) {
                logger.warn("Unable to parse annotationId="+i);
            } catch (NullPointerException|ArrayIndexOutOfBoundsException exc) {
                logger.info("Skip: ",exc);
            }
        }
        if (combined) {
            logger.info("Success! was added!!");
            ApiError success = new ApiError("The file was successfully readed. Added " + added + " annotations from "+csv.getMaxNumber(), 200);
            return success.toResponse().build();
        } else {
            ApiError err = new ApiError("The api failed to add annotations. Combined="+combined+" and added:"+added+".", 500);
            return err.toResponse().build();
        }
    }

    @POST
    @Path("/addAllCSV")
    public Response addAllCSV() throws IOException {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);

        String[] sampleFiles = {Props.CSV_SAMPLE_EN_ES, Props.CSV_SAMPLE_ES_DE, Props.CSV_SAMPLE_EN_EL_IRI,
                                Props.CSV_SAMPLE_EN_EL_LIT, Props.CSV_SAMPLE_EN_NL_IRI, Props.CSV_SAMPLE_EN_NL_LIT};
        String[] langA = {"en", "es", "en", "en", "en", "en"};
        String[] langB = {"es", "de", "el", "el", "nl", "nl"};

        //String[] sampleFiles = {Props.CSV_SAMPLE_EN_ES};

        List<String> messages = new ArrayList<>();
        for (int i = 0; i < sampleFiles.length; i++) {
            String sampleFile = sampleFiles[i];

            URL src = getClass().getClassLoader().getResource(Utils.fullPathCSV(sampleFile).toString());
            if (src == null) {
                ApiError err = new ApiError("Could not open CSV file", 500);
                return err.toResponse().build();
            }
            BufferedReader reader_schema = new BufferedReader(new InputStreamReader(src.openStream(), StandardCharsets.UTF_8));
            CSVAnnotationReader csv = new CSVAnnotationReader(reader_schema, langA[i], langB[i]);
            SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);

            Response resp = wrapper(csv, sql);
            if (resp.getStatus() != 200) {
                ApiError err = new ApiError("Found an error when processing "+sampleFile+". More info: "+((ApiError) resp.getEntity()).getMsg(), 500);
                return err.toResponse().build();
            } else {
                messages.add(((ApiError) resp.getEntity()).getMsg());
            }

        }
        logger.info("Success! was added");
        ApiError success = new ApiError("Everything went successful: "+messages, 200);
        return success.toResponse().build();

//        CSVAnnotationReader csv = new CSVAnnotationReader("/home/vfrico/anotados.csv", "en", "es");
//        CSVAnnotationReader csv = new CSVAnnotationReader("/home/vfrico/IdeaProjects/predictor/src/main/resources/csv/anotados-es-de.csv", "es", "de");
/*
        logger.info("Starts to read csv file");
        for (int i = 1; i < csv.getMaxNumber(); i++) {
            try {
                Annotation ann = csv.getAnnotation(i);
                logger.info("Annotation is: "+ann.toCsvString());
                AnnotationDAO res = sql.addAnnotation(ann);
                combined &= res != null;
                if (combined) added++;
                if (res == null) {
                    logger.warn("This call has returned false!!");
                    ApiError err = new ApiError("SQL has returned false", 500);
                    return err.toResponse().build();
                }

            } catch (IllegalArgumentException iae) {
                logger.warn("Unable to parse annotationId="+i);
            } catch (NullPointerException|ArrayIndexOutOfBoundsException exc) {
                logger.info("Skip");
            }

        }
        ApiError err = new ApiError("There were added "+added+" annotations and result is: "+combined, 500);
        return err.toResponse().build();
        */
    }
}
