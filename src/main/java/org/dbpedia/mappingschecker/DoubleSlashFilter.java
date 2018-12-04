package org.dbpedia.mappingschecker;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
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
@Provider
@PreMatching
public class DoubleSlashFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext reqContext) throws IOException {
        if (shouldRedirect(reqContext)) {
            URI fullURI = reqContext.getUriInfo().getAbsolutePath();

            String newPath = fullURI.getPath();
            if (newPath.startsWith("//")) {
                // replace the double slash on URI
                newPath = newPath.replace("//", "/");
            }
            String newURIString = fullURI.getScheme() + "://" + fullURI.getAuthority() + newPath + "?" + transformQueryParams(reqContext.getUriInfo().getQueryParameters(true));
            URI newURI = URI.create(String.valueOf(newURIString));
            reqContext.setRequestUri(newURI);
        }
    }

    /**
     * Filter is executed only if path contains a double "//".
     *
     * @param reqContext
     * @return
     */
    private boolean shouldRedirect(ContainerRequestContext reqContext) {
        return (reqContext.getUriInfo().getAbsolutePath().getPath().contains("//"));
    }


    /**
     * Transform a MultiValuedMap to a query parameters string
     *
     * The value of the map is always a list, because this is a valid URI
     *
     * http://example.com/resource?langa=en&langb=es&langb=es
     *
     * This method is aware of this list and transform the @param parameters
     * to the correct string.
     *
     * @param parameters
     * @return a valid Query Params string
     */
    private String transformQueryParams(MultivaluedMap<String, String> parameters) {
        StringBuilder sbquery = new StringBuilder();
        List<String> keys = new ArrayList<>(parameters.keySet());
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            List<String> values = parameters.get(key);
            for (int j = 0; j < values.size(); j++) {
                String value = values.get(j);
                sbquery.append(URLEncoder.encode(key)).append("=").append(URLEncoder.encode(value));
                if (j != values.size()-1) {
                    // If value is not the last one
                    sbquery.append("&");
                }
            }

            if (i != keys.size()-1) {
                // If key is not the last one
                sbquery.append("&");
            }
        }
        return sbquery.toString();
    }
}