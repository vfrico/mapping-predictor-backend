package es.upm.oeg.tools.mappings;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.ClassificationResult;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.resultset.SPARQLResult;
import org.dbpedia.mappingschecker.resources.AnnotationsResource;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainPlay {
    private static Logger logger = LoggerFactory.getLogger(MainPlay.class);


    public static void main(String[] args) {
        logger.info("START");
        SparqlReader reader = new SparqlReader("http://35.187.117.61:8890/sparql");
        SQLAnnotationReader sqlService = new SQLAnnotationReader("jdbc:"+Utils.getMySqlConfig());
        Annotation an = sqlService.getAnnotation(18618);
        List<Triple> triples = reader.getAnnotationHelp(an);
        logger.info("Triples: "+triples);

        logger.info("END");
    }

    public static void main2(String[] args) {
        logger.info("START");
        SparqlReader reader = new SparqlReader("http://google.com/sparql");
        long count = reader.getCountTemplateUsage("Ficha_de_entidad_subnacional", "es");
        logger.info("Count es: "+count);
    }




    public static void mainOLD(String[] args) {
//        Classifier c = new Classifier();
        logger.info("START");
        List<AnnotationDAO> anots = AnnotationsResource.getAnnotations("en", "es");

        List<Annotation> anotations = new ArrayList<>();

        anotations.addAll(anots);

//        Map<Annotation, ClassificationResult> classifiedOutput = c.classifyFrom(anotations);

        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        System.out.println(mysqlConfig);
        SQLAnnotationReader sqlService = new SQLAnnotationReader(mysqlConfig);

//        for (Annotation annotation : classifiedOutput.keySet()) {
//            logger.info("Annotation: "+classifiedOutput.get(annotation));
//            sqlService.addClassificationResult(((AnnotationDAO) annotation).getId(), classifiedOutput.get(annotation));
//        }

        for(AnnotationDAO anot : anots) {
            logger.info("Resultado: "+sqlService.getClassificationResult(anot.getId()));
        }
    }
}
