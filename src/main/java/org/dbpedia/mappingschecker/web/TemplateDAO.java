package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.Annotation;

import java.util.List;

/**
 * Copyright 2018 Víctor Fernández <vfrico@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Víctor Fernández <vfrico@gmail.com>
 * @since 1.0.0
 */
public class TemplateDAO {
    private String template;
    private String lang;
    private List<AnnotationDAO> annotations;
    private List<LockDAO> locks;
    private int allAnnotations;
    private int wrongAnnotations;
    private int correctAnnotations;
    private long templateUsages;
    private boolean isLocked;
    private double fleissKappa = 0;

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

    public boolean isLocked() {
        return isLocked;
    }

    public TemplateDAO setLocked(boolean locked) {
        isLocked = locked;
        return this;
    }

    public double getFleissKappa() {
        return fleissKappa;
    }

    public TemplateDAO setFleissKappa(double fleissKappa) {
        if (!Double.isNaN(fleissKappa) && Double.isFinite(fleissKappa)) {
            this.fleissKappa = fleissKappa;
            return this;
        }
        return this;
    }
}
