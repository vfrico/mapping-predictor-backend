package org.dbpedia.mappingschecker.web;

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
public class AnnotationHelperDAO {
    List<TripleDAO> relatedTriples;

    public List<TripleDAO> getRelatedTriples() {
        return relatedTriples;
    }

    public AnnotationHelperDAO setRelatedTriples(List<TripleDAO> relatedTriples) {
        this.relatedTriples = relatedTriples;
        return this;
    }
}
