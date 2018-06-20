package es.upm.oeg.tools.mappings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {

    private Path csvPath;
    private List<String> lines = new ArrayList<>();

    // Pointer to current line (as annotation id)
    private int currentLine;

    public CSVReader(String filepath) throws IOException {
        csvPath = FileSystems.getDefault().getPath(filepath);
        BufferedReader reader = Files.newBufferedReader(csvPath);
        reader.lines().forEach(l -> lines.add(l));
    }

    public Annotation getAnnotation(int id) {
        String linea = lines.get(id);
        return parseAnnotation(linea);
    }

    private Annotation parseAnnotation(String linea) {
        String[] campos = linea.split(",");
        String templateA = campos[0].trim();
        String templateB = campos[2].trim();

        String attributeA = campos[1].trim();
        String attributeB = campos[3].trim();

        String propA = campos[4].trim();
        String propB = campos[5].trim();

        String sM1 = campos[19].trim();
        String sM2 = campos[20].trim();
        String sM3 = campos[21].trim();
        String sM4a = campos[22].trim();
        String sM4b = campos[23].trim();
        String sM5a = campos[24].trim();
        String sM5b = campos[25].trim();

        String anotacion = campos[6].trim();
        long m1 = 0;
        long m2 = 0;
        long m3 = 0;
        long m4a = 0;
        long m5a = 0;
        long m4b = 0;
        long m5b = 0;
        try {
            m1 = Long.parseLong(sM1);
            m2 = Long.parseLong(sM2);
            m3 = Long.parseLong(sM3);
            m4a = Long.parseLong(sM4a);
            m4b = Long.parseLong(sM4b);
            m5a = Long.parseLong(sM5a);
            m5b = Long.parseLong(sM5b);
        } catch (NumberFormatException nfe) {
            System.out.println("Error parsing "+sM1);
        }
        Annotation entry = new Annotation(templateA, templateB, attributeA, attributeB,
                propA, propB, m1);
        entry.setAnotacion(anotacion);
        entry.setM2(m2);
        entry.setM3(m3);
        entry.setM4a(m4a);
        entry.setM4b(m4b);
        entry.setM5a(m5a);
        entry.setM5b(m5b);

        return entry;
    }
}
