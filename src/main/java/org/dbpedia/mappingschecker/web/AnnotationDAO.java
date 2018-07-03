package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.Annotation;

public class AnnotationDAO extends Annotation {

    int id;

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
}
