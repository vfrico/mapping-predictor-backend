package es.upm.oeg.tools.mappings.beans;

import java.util.HashMap;
import java.util.Map;

public class ClassificationResult {

    long timestamp;
    Map<AnnotationType, Double> votesMap = new HashMap<>();

    public ClassificationResult() {
        this.timestamp = System.currentTimeMillis();
    }
    public ClassificationResult(Map<AnnotationType, Double> votesMap, long timestamp) {
        this.votesMap = votesMap;
        this.timestamp = timestamp;
    }

    public AnnotationType getClassifiedAs() {
        AnnotationType higherVote = null;
        double higherValue = Double.MIN_VALUE;

        for (AnnotationType vote : votesMap.keySet()) {
            if (vote != null) {
                Double prob = votesMap.get(vote);
                if (prob != null && prob > higherValue) {
                    higherValue = prob;
                    higherVote = vote;
                }
            }
        }
        return higherVote;
    }

    public ClassificationResult setClassifiedAs(AnnotationType classifiedAs, double prob) {
        votesMap.put(classifiedAs, prob);
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public ClassificationResult setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Map<AnnotationType, Double> getVotesMap() {
        return votesMap;
    }

    public ClassificationResult setVotesMap(Map<AnnotationType, Double> votesMap) {
        this.votesMap = votesMap;
        return this;
    }

    @Override
    public String toString() {
        return "ClassificationResult{" +
                "timestamp=" + timestamp +
                ", votesMap=" + votesMap +
                '}';
    }
}

