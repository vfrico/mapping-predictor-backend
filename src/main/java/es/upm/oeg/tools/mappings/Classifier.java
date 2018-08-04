package es.upm.oeg.tools.mappings;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.AnnotationType;
import es.upm.oeg.tools.mappings.beans.ClassificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.*;
import java.util.stream.Collectors;

public class Classifier {

    private static Logger logger = LoggerFactory.getLogger(Classifier.class);


//    private Attribute templateA = new Attribute("templateA", true);
//    private Attribute templateB = new Attribute("templateB", true);
//    private Attribute attributeA = new Attribute("attributeA", true);
//    private Attribute attributeB = new Attribute("attributeB", true);
//    private Attribute propA = new Attribute("propA", true);
//    private Attribute propB = new Attribute("propB", true);
    private Attribute m1 = new Attribute("m1");
    private Attribute m2 = new Attribute("m2");
    private Attribute m3 = new Attribute("m3");
    private Attribute m4a = new Attribute("m4a");
    private Attribute m4b = new Attribute("m4b");
    private Attribute m5a = new Attribute("m5a");
    private Attribute m5b = new Attribute("m5b");
    private Attribute tb1 = new Attribute("tb1");
    private Attribute tb2 = new Attribute("tb2");
    private Attribute tb3 = new Attribute("tb3");
    private Attribute tb4 = new Attribute("tb4");
    private Attribute tb5 = new Attribute("tb5");
    private Attribute tb6 = new Attribute("tb6");
    private Attribute tb7 = new Attribute("tb7");
    private Attribute tb8 = new Attribute("tb8");
    private Attribute tb9 = new Attribute("tb9");
    private Attribute tb10 = new Attribute("tb10");
    private Attribute tb11 = new Attribute("tb11");
    private Attribute c1 = new Attribute("c1");
    private Attribute c2 = new Attribute("c2");
    private Attribute c3a = new Attribute("c3a");
    private Attribute c3b = new Attribute("c3b");
//    private Attribute langA = new Attribute("langA", true);
//    private Attribute langB = new Attribute("langB", true);
//    private Attribute classA = new Attribute("classA", true);
//    private Attribute classB = new Attribute("classB", true);
//    private Attribute domainPropA = new Attribute("domainPropA", true);
//    private Attribute domainPropB = new Attribute("domainPropB", true);
//    private Attribute rangePropA = new Attribute("rangePropA", true);
//    private Attribute rangePropB = new Attribute("rangePropB", true);
    private List<String> nominalValues = Arrays.stream(new String[] {
                                                        AnnotationType.CORRECT_MAPPING.toString(),
                                                        AnnotationType.WRONG_MAPPING.toString()})
                                                .collect(Collectors.toList());
    private Attribute annotation = new Attribute("annotation", nominalValues);

    // The input must be the list of all mappings to train

    // The output must be the same annotations (maybe an AnnotationDAO) with a classifiedAs attribute

    // This classified as can be an object containing the VoteType, the date on which the classification was done
    // and the precission of the classification (if exist)

    public Map<Annotation, ClassificationResult> classifyFrom(List<Annotation> anotations) {
        Map<Annotation, ClassificationResult> votesMap = new HashMap<>();

        Instances dataset = generateEmptyDataset(anotations.size());
        for (Annotation an : anotations) {
            Instance ins = generateInstance(an);
            ins.setDataset(dataset);
            dataset.add(ins);
        }

        dataset.setClassIndex(dataset.numAttributes()-1);

        try {
            RandomForest classifier = new RandomForest();
            String[] algOpts = {"-K", "0", "-M", "1.0", "-V", "0.001", "-S", "1", "-do-not-check-capabilities"};
            classifier.setOptions(algOpts);

            classifier.buildClassifier(dataset);

            logger.info("instances: "+anotations.size());

            logger.info("Print capabilities: "+classifier.getCapabilities());

            for (int i = 0; i < anotations.size(); i++) {
                logger.info("Classify instance: "+anotations.get(i));

                double[] votes = classifier.distributionForInstance(dataset.get(i));

                ClassificationResult result = new ClassificationResult();
                result.setClassifiedAs(AnnotationType.CORRECT_MAPPING, votes[0]);
                result.setClassifiedAs(AnnotationType.WRONG_MAPPING, votes[1]);
                votesMap.put(anotations.get(i), result);
                logger.info("Classified as: "+result);
            }

        } catch (Exception exc) {
            logger.warn("Exception caught", exc);
            return null;
        }
        return votesMap;
    }



    public Instances generateEmptyDataset(int maxCapacity) {
        ArrayList<Attribute> attributes = new ArrayList<>();
//        attributes.add(templateA);
//        attributes.add(templateB);
//        attributes.add(attributeA);
//        attributes.add(attributeB);
//        attributes.add(propA);
//        attributes.add(propB);
        attributes.add(m1);
        attributes.add(m2);
        attributes.add(m3);
        attributes.add(m4a);
        attributes.add(m4b);
        attributes.add(m5a);
        attributes.add(m5b);
        attributes.add(tb1);
        attributes.add(tb2);
        attributes.add(tb3);
        attributes.add(tb4);
        attributes.add(tb5);
        attributes.add(tb6);
        attributes.add(tb7);
        attributes.add(tb8);
        attributes.add(tb9);
        attributes.add(tb10);
        attributes.add(tb11);
        attributes.add(c1);
        attributes.add(c2);
        attributes.add(c3a);
        attributes.add(c3b);
//        attributes.add(langA);
//        attributes.add(langB);
//        attributes.add(classA);
//        attributes.add(classB);
//        attributes.add(domainPropA);
//        attributes.add(domainPropB);
//        attributes.add(rangePropA);
//        attributes.add(rangePropB);
        attributes.add(this.annotation);
        Instances dataset = new Instances("DBpediaMappings", attributes, maxCapacity);
        return dataset;
    }

    public Instance generateInstance(Annotation annotation) {
        //logger.info("generate instance from: "+annotation);
        Instance ins = new DenseInstance(23);
//        ins.setValue(templateA, annotation.getTemplateA());
//        ins.setValue(templateB, annotation.getTemplateB());
//        ins.setValue(attributeA, annotation.getAttributeA());
//        ins.setValue(attributeB, annotation.getAttributeB());
//        ins.setValue(propA, annotation.getPropA());
//        ins.setValue(propB, annotation.getPropB());
        ins.setValue(m1, annotation.getM1());
        ins.setValue(m2, annotation.getM2());
        ins.setValue(m3, annotation.getM3());
        ins.setValue(m4a, annotation.getM4a());
        ins.setValue(m4b, annotation.getM4b());
        ins.setValue(m5a, annotation.getM5a());
        ins.setValue(m5b, annotation.getM5b());
        ins.setValue(tb1, annotation.getTb1());
        ins.setValue(tb2, annotation.getTb2());
        ins.setValue(tb3, annotation.getTb3());
        ins.setValue(tb4, annotation.getTb4());
        ins.setValue(tb5, annotation.getTb5());
        ins.setValue(tb6, annotation.getTb6());
        ins.setValue(tb7, annotation.getTb7());
        ins.setValue(tb8, annotation.getTb8());
        ins.setValue(tb9, annotation.getTb9());
        ins.setValue(tb10, annotation.getTb10());
        ins.setValue(tb11, annotation.getTb11());
        ins.setValue(c1, annotation.getC1());
        ins.setValue(c2, annotation.getC2());
        ins.setValue(c3a, annotation.getC3a());
        ins.setValue(c3b, annotation.getC3b());
//        ins.setValue(langA, annotation.getLangA());
//        ins.setValue(langB, annotation.getLangB());
//        ins.setValue(classA, annotation.getClassA());
//        ins.setValue(classB, annotation.getClassB());
//        ins.setValue(domainPropA, annotation.getDomainPropA());
//        ins.setValue(domainPropB, annotation.getDomainPropB());
//        ins.setValue(rangePropA, annotation.getRangePropA());
//        ins.setValue(rangePropB, annotation.getRangePropB());
        if (annotation.getAnnotation() != null) {
            ins.setValue(this.annotation, annotation.getAnnotation().toString());
        }
        return ins;
    }
}
