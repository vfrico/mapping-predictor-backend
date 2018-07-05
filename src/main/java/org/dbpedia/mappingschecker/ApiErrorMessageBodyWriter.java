package org.dbpedia.mappingschecker;

import es.upm.oeg.tools.mappings.beans.ApiError;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces(MediaType.TEXT_PLAIN)
public class ApiErrorMessageBodyWriter implements MessageBodyWriter<ApiError> {

    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass == ApiError.class;
    }

    @Override
    public void writeTo(ApiError apiError, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        Writer writer = new PrintWriter(outputStream);
        writer.write(apiError.toString());
        writer.flush();
        writer.close();
    }
}
