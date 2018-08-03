package es.upm.oeg.tools.mappings.beans;

public class ClassificationResult {

    AnnotationType classifiedAs;
    long timestamp;
    double precision;

    public AnnotationType getClassifiedAs() {
        return classifiedAs;
    }

    public ClassificationResult setClassifiedAs(AnnotationType classifiedAs) {
        this.classifiedAs = classifiedAs;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ClassificationResult setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public double getPrecision() {
        return precision;
    }

    public ClassificationResult setPrecision(double precision) {
        this.precision = precision;
        return this;
    }
}
