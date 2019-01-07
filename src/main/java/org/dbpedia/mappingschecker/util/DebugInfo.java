package org.dbpedia.mappingschecker.util;

import java.util.ArrayList;
import java.util.List;

public class DebugInfo {
    List<String> debugMessages;

    public DebugInfo() {
        debugMessages = new ArrayList<>();
    }

    public void addMessage(String message) {
        debugMessages.add(message);
    }

    public List<String> getMessages() {
        return debugMessages;
    }
}
