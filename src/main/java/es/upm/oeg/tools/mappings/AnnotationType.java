package es.upm.oeg.tools.mappings;

import javax.xml.bind.annotation.XmlRootElement;

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
