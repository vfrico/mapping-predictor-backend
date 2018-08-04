package es.upm.oeg.tools.mappings;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.ClassificationResult;
import org.dbpedia.mappingschecker.resources.AnnotationsResource;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPlay {
    private static Logger logger = LoggerFactory.getLogger(MainPlay.class);
    public static void main(String[] args) {
        Classifier c = new Classifier();
        logger.info("START");
        List<AnnotationDAO> anots = AnnotationsResource.getAnnotations("en", "es");

        List<Annotation> anotations = new ArrayList<>();

        anotations.addAll(anots);

        Map<Annotation, ClassificationResult> classifiedOutput = c.classifyFrom(anotations);


    }
}
