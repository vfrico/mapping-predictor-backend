package es.upm.oeg.tools.mappings.beans;

import org.dbpedia.mappingschecker.AnnotationsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Copyright 2014-2018 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
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
 * @author Nandana Mihindukulasooriya
 * @since 1.0.0
 * @author Víctor Fernández <vfrico@gmail.com>
 * @since 2.0.0
 */

@XmlRootElement
public class Annotation {
    private static Logger logger = LoggerFactory.getLogger(Annotation.class);

    private String templateA;
    private String templateB;
    private String attributeA;
    private String attributeB;
    private String propA;
    private String propB;

    private String anotacion;
    private AnnotationType annotation;

    private long m1;
    private long m2;
    private long m3;
    private long m4a;
    private long m4b;
    private long m5a;
    private long m5b;

    private int tb1;
    private int tb2;
    private int tb3;
    private int tb4;
    private int tb5;
    private int tb6;
    private int tb7;
    private int tb8;
    private int tb9;
    private int tb10;
    private int tb11;

    private double c1;
    private double c2;
    private double c3a;
    private double c3b;

    // Lang in ISO 639-1 ("es") or if not exists, on ISO-631-2 format (eg: "ast")
    private String langA;
    private String langB;

    // Property mappings
    private String classA;
    private String classB;

    private String domainPropA;
    private String domainPropB;

    private String rangePropA;
    private String rangePropB;

    public Annotation() {
        logger.info("Default constructor");
    }

    public Annotation(Annotation other) {
        this.templateA = other.templateA;
        this.templateB = other.templateB;
        this.attributeA = other.attributeA;
        this.attributeB = other.attributeB;
        this.propA = other.propA;
        this.propB = other.propB;
        this.anotacion = other.anotacion;
        this.annotation = other.annotation;
        this.m1 = other.m1;
        this.m2 = other.m2;
        this.m3 = other.m3;
        this.m4a = other.m4a;
        this.m4b = other.m4b;
        this.m5a = other.m5a;
        this.m5b = other.m5b;
        this.tb1 = other.tb1;
        this.tb2 = other.tb2;
        this.tb3 = other.tb3;
        this.tb4 = other.tb4;
        this.tb5 = other.tb5;
        this.tb6 = other.tb6;
        this.tb7 = other.tb7;
        this.tb8 = other.tb8;
        this.tb9 = other.tb9;
        this.tb10 = other.tb10;
        this.tb11 = other.tb11;
        this.c1 = other.c1;
        this.c2 = other.c2;
        this.c3a = other.c3a;
        this.c3b = other.c3b;
        this.langA = other.langA;
        this.langB = other.langB;
        this.classA = other.classA;
        this.classB = other.classB;
        this.domainPropA = other.domainPropA;
        this.domainPropB = other.domainPropB;
        this.rangePropA = other.rangePropA;
        this.rangePropB = other.rangePropB;
    }

    public Annotation(String propA, String propB) {
        this.propA = propA;
        this.propB = propB;
    }

    public Annotation(String propA, String propB, long m1) {
        this.propA = propA;
        this.propB = propB;
        this.m1 = m1;
    }

    public Annotation(String templateA, String templateB, String attributeA, String attributeB,
                    String propA, String propB, long m1) {
        this.templateA = templateA;
        this.templateB = templateB;
        this.attributeA = attributeA;
        this.attributeB = attributeB;
        this.propA = propA;
        this.propB = propB;
        this.m1 = m1;
    }

    public String getPropA() {
        return propA;
    }

    public void setPropA(String propA) {
        this.propA = propA;
    }

    public String getPropB() {
        return propB;
    }

    public void setPropB(String propB) {
        this.propB = propB;
    }

    public String getTemplateA() {
        return templateA;
    }

    public void setTemplateA(String templateA) {
        this.templateA = templateA;
    }

    public String getTemplateB() {
        return templateB;
    }

    public void setTemplateB(String templateB) {
        this.templateB = templateB;
    }

    public String getAttributeA() {
        return attributeA;
    }

    public void setAttributeA(String attributeA) {
        this.attributeA = attributeA;
    }

    public String getAttributeB() {
        return attributeB;
    }

    public void setAttributeB(String attributeB) {
        this.attributeB = attributeB;
    }

    public long getM1() {
        return m1;
    }

    public void setM1(long m1) {
        this.m1 = m1;
    }

    public long getM2() {
        return m2;
    }

    public long getM3() {
        return m3;
    }

    public void setM3(long m3) {
        this.m3 = m3;
    }

    public long getM4a() {
        return m4a;
    }

    public void setM4a(long m4a) {
        this.m4a = m4a;
    }

    public void setM2(long m2) {
        this.m2 = m2;
    }

    public long getM4b() {
        return m4b;
    }

    public void setM4b(long m4b) {
        this.m4b = m4b;
    }

    public long getM5a() {
        return m5a;
    }

    public void setM5a(long m5a) {
        this.m5a = m5a;
    }

    public long getM5b() {
        return m5b;
    }

    public void setM5b(long m5b) {
        this.m5b = m5b;
    }

    public int getTb1() {
        return tb1;
    }

    public void setTb1(int tb1) {
        this.tb1 = tb1;
    }

    public void setTb1_bool(boolean tb1) {
        if (tb1)
            setTb1(1);
        else
            setTb1(0);
    }

    public int getTb2() {
        return tb2;
    }

    public void setTb2(int tb2) {
        this.tb2 = tb2;
    }

    public void setTb2_bool(boolean tb2) {
        if (tb2)
            setTb2(1);
        else
            setTb2(0);
    }

    public int getTb3() {
        return tb3;
    }

    public void setTb3(int tb3) {
        this.tb3 = tb3;
    }

    public void setTb3_bool(boolean tb3) {
        if (tb3)
            setTb3(1);
        else
            setTb3(0);
    }

    public int getTb4() {
        return tb4;
    }

    public void setTb4(int tb4) {
        this.tb4 = tb4;
    }

    public void setTb4_bool(boolean tb4) {
        if (tb4)
            setTb4(1);
        else
            setTb4(0);
    }

    public int getTb5() {
        return tb5;
    }

    public void setTb5(int tb5) {
        this.tb5 = tb5;
    }

    public void setTb5_bool(boolean tb5) {
        if (tb5)
            setTb5(1);
        else
            setTb5(0);
    }

    public int getTb6() {
        return tb6;
    }

    public void setTb6(int tb6) {
        this.tb6 = tb6;
    }

    public void setTb6_bool(boolean tb6) {
        if (tb6)
            setTb6(1);
        else
            setTb6(0);
    }

    public int getTb7() {
        return tb7;
    }

    public void setTb7(int tb7) {
        this.tb7 = tb7;
    }

    public void setTb7_bool(boolean tb7) {
        if (tb7)
            setTb7(1);
        else
            setTb7(0);
    }

    public int getTb8() {
        return tb8;
    }

    public void setTb8(int tb8) {
        this.tb8 = tb8;
    }

    public void setTb8_bool(boolean tb8) {
        if (tb8)
            setTb8(1);
        else
            setTb8(0);
    }

    public int getTb9() {
        return tb9;
    }

    public void setTb9(int tb9) {
        this.tb9 = tb9;
    }

    public void setTb9_bool(boolean tb9) {
        if (tb9)
            setTb9(1);
        else
            setTb9(0);
    }

    public int getTb10() {
        return tb10;
    }

    public void setTb10(int tb10) {
        this.tb10 = tb10;
    }

    public void setTb10_bool(boolean tb10) {
        if (tb10)
            setTb10(1);
        else
            setTb10(0);
    }

    public int getTb11() {
        return tb11;
    }

    public void setTb11(int tb11) {
        this.tb11 = tb11;
    }

    public void setTb11_bool(boolean tb11) {
        if (tb11)
            setTb11(1);
        else
            setTb11(0);
    }

    public double getC1() {
        return c1;
    }

    public void setC1(double c1) {
        this.c1 = c1;
    }

    public double getC2() {
        return c2;
    }

    public void setC2(double c2) {
        this.c2 = c2;
    }

    public double getC3a() {
        return c3a;
    }

    public void setC3a(double c3a) {
        this.c3a = c3a;
    }

    public double getC3b() {
        return c3b;
    }

    public void setC3b(double c3b) {
        this.c3b = c3b;
    }

    public String getAnotacion() {
        return anotacion;
    }

    public void setAnotacion(String anotacion) {
        this.anotacion = anotacion;
        if (getAnnotation() == null)
            setAnnotation(AnnotationType.fromString(anotacion));
    }

    public AnnotationType getAnnotation() {
        return annotation;
    }

    public void setAnnotation(AnnotationType annotation) {
        this.annotation = annotation;
        if (getAnotacion() == null)
            setAnotacion(annotation.toString());
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

    public String getClassA() {
        return classA;
    }

    public void setClassA(String classA) {
        this.classA = classA;
    }

    public String getClassB() {
        return classB;
    }

    public void setClassB(String classB) {
        this.classB = classB;
    }

    public String getDomainPropA() {
        return domainPropA;
    }

    public void setDomainPropA(String domainPropA) {
        this.domainPropA = domainPropA;
    }

    public String getDomainPropB() {
        return domainPropB;
    }

    public void setDomainPropB(String domainPropB) {
        this.domainPropB = domainPropB;
    }

    public String getRangePropA() {
        return rangePropA;
    }

    public void setRangePropA(String rangePropA) {
        this.rangePropA = rangePropA;
    }

    public String getRangePropB() {
        return rangePropB;
    }

    public void setRangePropB(String rangePropB) {
        this.rangePropB = rangePropB;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Annotation) {

            if (propA == null || propB == null) {
                return false;
            }

            if (propA.equals(((Annotation) obj).getPropA())
                    && propB.equals(((Annotation) obj).getPropB())) {
                return true;
            } else {
                return false;
            }

        } else {
            return false;
        }
    }

    public String toString() {
        return templateA+"/"+propA+" -> "+templateB+"/"+propB+" ["+m1+"]";
    }

    public static String headerCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("TemplateA").append(",");
        sb.append("AttributeA").append(",");
        sb.append("TemplateB").append(",");
        sb.append("AttributeB").append(",");
        sb.append("PropertyA").append(",");
        sb.append("PropertyB").append(",");
        sb.append("ClassA").append(",");
        sb.append("ClassB").append(",");
        sb.append("Anotacion").append(",");
        sb.append("DomainPropertyA").append(",");
        sb.append("DomainPropertyB").append(",");
        sb.append("RangePropertyA").append(",");
        sb.append("RangePropertyB").append(",");
        sb.append("C1").append(",");
        sb.append("C2").append(",");
        sb.append("C3a").append(",");
        sb.append("C3b").append(",");
        sb.append("M1").append(",");
        sb.append("M2").append(",");
        sb.append("M3").append(",");
        sb.append("M4a").append(",");
        sb.append("M4b").append(",");
        sb.append("M5a").append(",");
        sb.append("M5b").append(",");
        sb.append("TB1").append(",");
        sb.append("TB2").append(",");
        sb.append("TB3").append(",");
        sb.append("TB4").append(",");
        sb.append("TB5").append(",");
        sb.append("TB6").append(",");
        sb.append("TB7").append(",");
        sb.append("TB8").append(",");
        sb.append("TB9").append(",");
        sb.append("TB10").append(",");
        sb.append("TB11");
        return sb.toString();
    }

    public String toCsvString() {
        // Format needed to show always decimal with dot (as used on US)
        DecimalFormatSymbols symbolsEN_US = DecimalFormatSymbols.getInstance(Locale.US);
        NumberFormat formatter = new DecimalFormat("#0.00000", symbolsEN_US);

        StringBuilder sb = new StringBuilder();
        sb.append(getTemplateA()).append(",");
        sb.append(getAttributeA()).append(",");
        sb.append(getTemplateB()).append(",");
        sb.append(getAttributeB()).append(",");
        sb.append(getPropA()).append(",");
        sb.append(getPropB()).append(",");
        sb.append(getClassA()).append(",");
        sb.append(getClassB()).append(",");
        sb.append(getAnnotation()).append(",");
        sb.append(getDomainPropA()).append(",");
        sb.append(getDomainPropB()).append(",");
        sb.append(getRangePropA()).append(",");
        sb.append(getRangePropB()).append(",");
        sb.append(formatter.format(getC1())).append(",");
        sb.append(formatter.format(getC2())).append(",");
        sb.append(formatter.format(getC3a())).append(",");
        sb.append(formatter.format(getC3b())).append(",");
        sb.append(getM1()).append(",");
        sb.append(getM2()).append(",");
        sb.append(getM3()).append(",");
        sb.append(getM4a()).append(",");
        sb.append(getM4b()).append(",");
        sb.append(getM5a()).append(",");
        sb.append(getM5b()).append(",");
        sb.append(getTb1()).append(",");
        sb.append(getTb2()).append(",");
        sb.append(getTb3()).append(",");
        sb.append(getTb4()).append(",");
        sb.append(getTb5()).append(",");
        sb.append(getTb6()).append(",");
        sb.append(getTb7()).append(",");
        sb.append(getTb8()).append(",");
        sb.append(getTb9()).append(",");
        sb.append(getTb10()).append(",");
        sb.append(getTb11());
        return sb.toString();
    }
}

