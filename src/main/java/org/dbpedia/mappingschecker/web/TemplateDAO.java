package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.Annotation;

import java.util.List;

public class TemplateDAO {
    private String template;
    private String lang;
    private List<AnnotationDAO> annotations;

    public TemplateDAO(String template, String lang) {
        this.lang = lang;
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }

    public TemplateDAO setTemplate(String template) {
        this.template = template;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public TemplateDAO setLang(String lang) {
        this.lang = lang;
        return this;
    }

    public List<AnnotationDAO> getAnnotations() {
        return annotations;
    }

    public TemplateDAO setAnnotations(List<AnnotationDAO> annotations) {
        this.annotations = annotations;
        return this;
    }
}
