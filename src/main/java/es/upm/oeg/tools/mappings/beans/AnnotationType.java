package es.upm.oeg.tools.mappings.beans;

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
public enum AnnotationType {

    CORRECT_MAPPING(0), WRONG_MAPPING(1), CORRECT_BUT_SPECIFY(2), WRONG_ONTOLOGY(3), UNKNOWN(100);

    private int id;

    AnnotationType(int id) {
        this.id = id;
    }

    /**
     * Generates an AnnotationType from a character string. Valid inputs are:
     *  * CORRECT_MAPPING or a string that contains the term "correct"
     *  * WRONG_MAPPING or a string that contains the term "wrong"
     *  * CORRECT_BUT_SPECIFY
     *  * WRONG_ONTOLOGY
     * @param name
     */
    public static AnnotationType fromString(String name) {
        // Please, avoid annotating a mapping with the term *incorrect*, as this could lead to errors
        if (name.equals("CORRECT_BUT_SPECIFY")) {
            return AnnotationType.CORRECT_BUT_SPECIFY;
        } else if (name.equals("WRONG_ONTOLOGY")) {
            return AnnotationType.WRONG_ONTOLOGY;
        } else if (name.equals("CORRECT_MAPPING") || name.toLowerCase().contains("correct")) {
            return AnnotationType.CORRECT_MAPPING;
        } else if (name.equals("WRONG_MAPPING") || name.toLowerCase().contains("wrong")) {
            return AnnotationType.WRONG_MAPPING;
        } else {
            return AnnotationType.UNKNOWN;
        }
    }

    public String toString() {
        return this.name();
    }

}
