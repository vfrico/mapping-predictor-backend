package es.upm.oeg.tools.mappings;

import es.upm.oeg.tools.mappings.beans.Annotation;
import org.dbpedia.mappingschecker.resources.AnnotationsResource;
import org.dbpedia.mappingschecker.web.AnnotationDAO;

import java.util.ArrayList;
import java.util.List;

public class MainPlay {
    public static void main(String[] args) {
        Classifier c = new Classifier();

        List<AnnotationDAO> anots = AnnotationsResource.getAnnotations("en", "es");
        List<Annotation> anotations = new ArrayList<>();

        anotations.addAll(anots);

        c.classifyFrom(anotations);
    }
}
