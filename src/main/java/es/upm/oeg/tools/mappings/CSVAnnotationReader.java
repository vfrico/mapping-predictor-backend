package es.upm.oeg.tools.mappings;

import es.upm.oeg.tools.mappings.beans.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

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
public class CSVAnnotationReader implements AnnotationReader {
    private static Logger logger = LoggerFactory.getLogger(CSVAnnotationReader.class);


    //private Path csvPath;
    private List<String> lines = new ArrayList<>();
    private Map<String, Integer> fileMap;

    // Lang in ISO 639-1 ("es") or if not exists, on ISO-631-2 format (eg: "ast")
    private String langA;
    private String langB;

    public CSVAnnotationReader(String filepath, String langA, String langB) throws IOException {
        Path csvPath = FileSystems.getDefault().getPath(filepath);
        BufferedReader reader = Files.newBufferedReader(csvPath);
        reader.lines().forEach(l -> lines.add(l));
        fileMap = getMap();

        this.langA = langA;
        this.langB = langB;
    }

    public CSVAnnotationReader(BufferedReader reader, String langA, String langB) {
        reader.lines().forEach(l -> lines.add(l));
        fileMap = getMap();

        this.langA = langA;
        this.langB = langB;
    }

    public CSVAnnotationReader(InputStream inputStream, String langA, String langB) {
        this(new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)), langA, langB);
    }

    public Annotation getAnnotation(int id) {
        String linea = lines.get(id);
        return parseAnnotation(linea);
    }

    /**
     * Get as much params as it can recover from CSV
     * @param id
     * @return
     */
    public Annotation getPartialAnnotation(int id) {
        String linea = lines.get(id);
        return parsePartialAnnotation(linea);
    }

    private Map<String, Integer> getMap() {
        // Obtiene el mapping a través de la primera línea
        String primera = lines.get(0);
        String[] campos = primera.split(",");
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < campos.length; i++) {
            map.put(campos[i].trim().toLowerCase(), i);
        }
        return map;
    }

    private boolean parseFieldBool(String fieldName, String[] line) {
        String feature = parseFieldString(fieldName, line);
        long field = Long.parseLong(feature);
        if (field == 1)
            return true;
        else
            return false;
    }

    private String parseFieldString(String fieldName, String[] line) {
        return line[fileMap.get(fieldName.toLowerCase().trim())].trim();
    }

    private long parseFieldLong(String fieldName, String[] line) {
        String feature = parseFieldString(fieldName, line);
        long field = Long.parseLong(feature);
        return field;
    }

    private double parseFieldDouble(String fieldName, String[] line) {

        String feature = parseFieldString(fieldName, line);
        double field = Double.parseDouble(feature);
        logger.trace("Double parse: {} y termina siendo: {}", fieldName ,field);
        return field;
    }

    /**
     * Generates a simple annotation (without vote and without params)
     *
     * @param campos line divided by CSV fields
     * @return a new Annotation object
     */
    private Annotation generateBasicAnnotation(String[] campos) {
        //String templateA = campos[0].trim();
        String templateA = parseFieldString("Template A", campos);
        //String templateB = campos[2].trim();
        String templateB = parseFieldString("Template B", campos);

        //String attributeA = campos[1].trim();
        String attributeA = parseFieldString("Attribute A", campos);
        // String attributeB = campos[3].trim();
        String attributeB = parseFieldString("Attribute B", campos);

        //String propA = campos[4].trim();
        String propA = parseFieldString("Property A", campos);
        //String propB = campos[5].trim();
        String propB = parseFieldString("Property B", campos);

        Annotation entry = new Annotation(templateA, templateB, attributeA, attributeB,
                propA, propB);


        entry.setLangA(langA);
        entry.setLangB(langB);

        return entry;
    }

    private void parseDomainRange(Annotation entry, String[] campos) {
        entry.setClassA(parseFieldString("Class A", campos));
        entry.setClassB(parseFieldString("Class B", campos));
        entry.setDomainPropA(parseFieldString("Domain Property A", campos));
        entry.setDomainPropB(parseFieldString("Domain Property B", campos));
        entry.setRangePropA(parseFieldString("Range Property A", campos));
        entry.setRangePropB(parseFieldString("Range Property B", campos));
    }

    private void parseMparams(Annotation entry, String[] campos) {
        entry.setM1(parseFieldLong("M1", campos));
        entry.setM2(parseFieldLong("M2", campos));
        entry.setM3(parseFieldLong("M3", campos));
        entry.setM4a(parseFieldLong("M4a", campos));
        entry.setM4b(parseFieldLong("M4b", campos));
        entry.setM5a(parseFieldLong("M5a", campos));
        entry.setM5b(parseFieldLong("M5b", campos));
    }

    private void parseTparams(Annotation entry, String[] campos) {
        entry.setTb1_bool(parseFieldBool("TB1", campos));
        entry.setTb2_bool(parseFieldBool("TB2", campos));
        entry.setTb3_bool(parseFieldBool("TB3", campos));
        entry.setTb4_bool(parseFieldBool("TB4", campos));
        entry.setTb5_bool(parseFieldBool("TB5", campos));
        entry.setTb6_bool(parseFieldBool("TB6", campos));
        entry.setTb7_bool(parseFieldBool("TB7", campos));
        entry.setTb8_bool(parseFieldBool("TB8", campos));
        entry.setTb9_bool(parseFieldBool("TB9", campos));
        entry.setTb10_bool(parseFieldBool("TB10", campos));
        entry.setTb11_bool(parseFieldBool("TB11", campos));
    }

    private void parseCparams(Annotation entry, String[] campos) {
        entry.setC1(parseFieldDouble("C1", campos));
        entry.setC2(parseFieldDouble("C2", campos));
        entry.setC3a(parseFieldDouble("C3a", campos));
        entry.setC3b(parseFieldDouble("C3b", campos));
    }

    /**
     * Parse an annotation String in CSV format
     *
     * Problem: this is a very ad-hoc solution...
     *
     * @param linea CSV line to be parsed into an Annotation
     * @return
     */
    private Annotation parseAnnotation(String linea) {
        String[] campos = linea.split(",", -1);
        logger.info("Parse linea: "+linea+" que tiene: "+campos.length);

        Annotation entry = generateBasicAnnotation(campos);

        parseDomainRange(entry, campos);

        // Add vote from CSV
        entry.setAnotacion(parseFieldString("Annotation", campos));

        parseMparams(entry, campos);
        parseCparams(entry, campos);
        parseTparams(entry, campos);

        return entry;
    }

    private Annotation parsePartialAnnotation(String linea) {

        String[] campos = linea.split(",", -1);
        logger.info("Parse linea: "+linea+" que tiene: "+campos.length);

        Annotation entry = generateBasicAnnotation(campos);

        try {
            // Add vote from CSV
            entry.setAnotacion(parseFieldString("Annotation", campos));
        } catch (Exception e) {
            logger.warn("Unable to parse annotation's vote");
        }

        try {
            parseDomainRange(entry, campos);
        } catch (Exception e) {
            logger.warn("Unable to parse annotation's domain and range", e);
        }

        try {
            parseMparams(entry, campos);
        } catch (Exception e) {
            logger.warn("Unable to parse annotation's M params", e);
        }

        try {
            parseCparams(entry, campos);
        } catch (Exception e) {
            logger.warn("Unable to parse annotation's C params", e);
        }

        try {
            parseTparams(entry, campos);
        } catch (Exception e) {
            logger.warn("Unable to parse annotation's T params", e);
        }

        return entry;
    }


    /**
     * Gets the maximum annotations number on the file
     * @return
     */
    public int getMaxNumber() {
        return lines.size();
    }
}
