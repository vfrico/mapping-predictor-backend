package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.AnnotationType;

import java.sql.Timestamp;

public class VoteDAO {
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
}
