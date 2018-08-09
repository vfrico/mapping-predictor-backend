package org.dbpedia.mappingschecker.web;

import java.util.List;

public class AnnotationHelperDAO {
    List<TripleDAO> relatedTriples;

    public List<TripleDAO> getRelatedTriples() {
        return relatedTriples;
    }

    public AnnotationHelperDAO setRelatedTriples(List<TripleDAO> relatedTriples) {
        this.relatedTriples = relatedTriples;
        return this;
    }
}
