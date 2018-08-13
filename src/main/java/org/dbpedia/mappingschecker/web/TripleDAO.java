package org.dbpedia.mappingschecker.web;


import org.apache.jena.graph.Triple;

import javax.xml.bind.annotation.XmlRootElement;

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
