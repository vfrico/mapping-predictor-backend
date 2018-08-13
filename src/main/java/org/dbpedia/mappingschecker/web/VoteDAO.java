package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.AnnotationType;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

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
public class VoteDAO {
    int idVote;
    AnnotationType vote;
    Timestamp creationDate;
    UserDAO user;
    int annotationId;

    public AnnotationType getVote() {
        return vote;
    }

    public void setVote(AnnotationType vote) {
        this.vote = vote;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public UserDAO getUser() {
        return user;
    }

    public void setUser(UserDAO user) {
        this.user = user;
    }

    public int getAnnotationId() {
        return annotationId;
    }

    public void setAnnotationId(int annotationId) {
        this.annotationId = annotationId;
    }

    public int getIdVote() {
        return idVote;
    }

    public VoteDAO setIdVote(int idVote) {
        this.idVote = idVote;
        return this;
    }

    @Override
    public String toString() {
        String s = "VoteDAO{";

        s += vote + ", ";
        s += annotationId + ", ";
        s += user + ", ";
        s += creationDate;

        return s + "}";
    }
}
