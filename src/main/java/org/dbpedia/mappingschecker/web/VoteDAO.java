package org.dbpedia.mappingschecker.web;

import es.upm.oeg.tools.mappings.beans.AnnotationType;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;

@XmlRootElement
public class VoteDAO {
    int idvote;
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

    public int getIdvote() {
        return idvote;
    }

    public VoteDAO setIdvote(int idvote) {
        this.idvote = idvote;
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
