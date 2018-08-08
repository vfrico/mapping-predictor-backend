package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.ClassificationResult;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class AnnotationDAO extends Annotation {

    int id;
    List<VoteDAO> votes;
    ClassificationResult classificationResult;
    List<LockDAO> locks;

    public AnnotationDAO(Annotation other) {
        super(other);
    }

    public AnnotationDAO() {

    }

    public AnnotationDAO(String templateA, String templateB, String attributeA, String attributeB, String propA, String propB, long m1, int id) {
        super(templateA, templateB, attributeA, attributeB, propA, propB, m1);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<VoteDAO> getVotes() {
        return votes;
    }

    public AnnotationDAO setVotes(List<VoteDAO> votes) {
        this.votes = votes;
        return this;
    }

    public ClassificationResult getClassificationResult() {
        return classificationResult;
    }

    public AnnotationDAO setClassificationResult(ClassificationResult classificationResult) {
        this.classificationResult = classificationResult;
        return this;
    }

    public List<LockDAO> getLocks() {
        return locks;
    }

    public AnnotationDAO setLocks(List<LockDAO> locks) {
        this.locks = locks;
        return this;
    }
}

