package org.dbpedia.mappingschecker;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.net.URI;

@Provider
@PreMatching
public class DoubleSlashFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext reqContext) throws IOException {
        if (shouldRedirect(reqContext)) {
            URI fullURI = reqContext.getUriInfo().getAbsolutePath();
            System.out.println("URI completa: "+fullURI);
            System.out.printf("PATH: "+fullURI.getPath());
            System.out.printf("PATH: "+fullURI.getAuthority());

            String newPath = fullURI.getPath();
            System.out.println("Path is: "+newPath);
            if (newPath.startsWith("//")) {
                newPath = newPath.replace("//", "/");
                System.out.println("New Path is: "+newPath);
            }

            String newURI = fullURI.getScheme() + "://" + fullURI.getAuthority() + newPath;
            System.out.println("New URI is: "+newURI);
            reqContext.setRequestUri(URI.create(String.valueOf(newURI)));
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
}