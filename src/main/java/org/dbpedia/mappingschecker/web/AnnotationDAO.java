package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.ClassificationResult;

import javax.xml.bind.annotation.XmlRootElement;
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

