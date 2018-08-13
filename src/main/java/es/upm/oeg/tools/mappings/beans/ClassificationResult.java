package es.upm.oeg.tools.mappings.beans;

import java.util.HashMap;
import java.util.Map;
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

