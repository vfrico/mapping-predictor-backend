package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.Annotation;

import java.util.List;

public class TemplateDAO {
    private String template;
    private String lang;
    private List<AnnotationDAO> annotations;
    private List<LockDAO> locks;
    private int allAnnotations;
    private int wrongAnnotations;
    private int correctAnnotations;
    private long templateUsages;

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

    public int getAllAnnotations() {
        return allAnnotations;
    }

    public TemplateDAO setAllAnnotations(int allAnnotations) {
        this.allAnnotations = allAnnotations;
        return this;
    }

    public int getWrongAnnotations() {
        return wrongAnnotations;
    }

    public TemplateDAO setWrongAnnotations(int wrongAnnotations) {
        this.wrongAnnotations = wrongAnnotations;
        return this;
    }

    public int getCorrectAnnotations() {
        return correctAnnotations;
    }

    public TemplateDAO setCorrectAnnotations(int correctAnnotations) {
        this.correctAnnotations = correctAnnotations;
        return this;
    }

    public long getTemplateUsages() {
        return templateUsages;
    }

    public TemplateDAO setTemplateUsages(long templateUsages) {
        this.templateUsages = templateUsages;
        return this;
    }

    public List<LockDAO> getLocks() {
        return locks;
    }

    public TemplateDAO setLocks(List<LockDAO> locks) {
        this.locks = locks;
        return this;
    }
}
