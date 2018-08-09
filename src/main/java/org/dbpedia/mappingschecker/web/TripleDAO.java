package org.dbpedia.mappingschecker.web;


import org.apache.jena.graph.Triple;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TripleDAO {
    String subject;
    String predicate;
    String object;

    public TripleDAO(String subject, String predicate, String object) {
        this.object = object;
        this.subject = subject;
        this.predicate = predicate;
    }

    public TripleDAO(Triple triple) {
        this(triple.getSubject().toString(),
             triple.getPredicate().toString(),
             triple.getObject().toString());
    }

    public String getSubject() {
        return subject;
    }

    public TripleDAO setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public String getPredicate() {
        return predicate;
    }

    public TripleDAO setPredicate(String predicate) {
        this.predicate = predicate;
        return this;
    }

    public String getObject() {
        return object;
    }

    public TripleDAO setObject(String object) {
        this.object = object;
        return this;
    }
}
