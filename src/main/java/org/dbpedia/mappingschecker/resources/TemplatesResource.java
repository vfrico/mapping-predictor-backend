package org.dbpedia.mappingschecker.resources;

import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import es.upm.oeg.tools.mappings.beans.ApiError;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.TemplateDAO;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.SQLException;
import java.util.List;

@Path("/templates")
public class TemplatesResource {
    /**
     * Get a list of all templates available
     *
     * needs a query param named "lang"
     *
     * @return
     */
    @GET
    public Response getTemplatesByLang(@Context UriInfo info) {
        String lang = info.getQueryParameters().getFirst("lang");
        if (lang == null || lang.equals("")) {
            ApiError err = new ApiError("Query param 'lang=' ("+lang+") is not defined or incorrect", 400);
            return Response.status(400).entity(err).build();
        }

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);
        try {
            List<TemplateDAO> templates = sqlService.getAllTemplatesByLang(lang);

            return Response.status(200)
                    .entity(templates).build();

        } catch (SQLException e) {
            e.printStackTrace();
            ApiError err = new ApiError("Error on SQL: "+e.getMessage(), 500, e);
            return err.toResponse().build();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ApiError err = new ApiError("SQL Driver Class not found: "+e.getMessage(), 500, e);
            return err.toResponse().build();
        }
    }


    /**
     * Get all information of a template: all related mappings
     * @param templateName
     * @return
     */
    @GET
    @Path("/{templateName}")
    public Response getAllInfo(@PathParam("templateName") String templateName, @Context UriInfo info) {
        String lang = info.getQueryParameters().getFirst("lang");
        if (lang == null || lang.equals("")) {
            ApiError err = new ApiError("Query param 'lang=' ("+lang+") is not defined or incorrect", 400);
            return Response.status(400).entity(err).build();
        }

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);

        TemplateDAO template = new TemplateDAO(templateName, lang);
        template.setAnnotations(sqlService.getAnnotationsByTemplateB(templateName, lang));

        return Response.status(200).entity(template).build();
    }
}
