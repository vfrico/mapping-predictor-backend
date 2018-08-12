package org.dbpedia.mappingschecker.web;

public class LangPair {
    public String langA;
    public String langB;

    public LangPair(String langA, String langB) {
        this.langA = langA;
        this.langB = langB;
    }

    public String getLangA() {
        return langA;
    }

    public void setLangA(String langA) {
        this.langA = langA;
    }

    public String getLangB() {
        return langB;
    }

    public void setLangB(String langB) {
        this.langB = langB;
    }
}
