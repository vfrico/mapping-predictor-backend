package es.upm.oeg.tools.mappings;

import es.upm.oeg.tools.mappings.beans.AnnotationType;
import org.dbpedia.mappingschecker.resources.AnnotationsResource;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.dbpedia.mappingschecker.web.UserDAO;
import org.dbpedia.mappingschecker.web.VoteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class FleissKappa {
    private static Logger logger = LoggerFactory.getLogger(FleissKappa.class);


    int anotaciones_N;
    int anotadores_n;
    int categorias_k = 2;


    int[][] table;

    public FleissKappa(List<AnnotationDAO> anotaciones) {

        List<int[]> pretable = new ArrayList<>();
        Set<String> usuarios = new HashSet<>();

        for (AnnotationDAO anotacion : anotaciones) {
            List<VoteDAO> votes = anotacion.getVotes();
            if (votes != null && votes.size() > 0) {
                int correct = (int) votes.stream().filter(v->v.getVote().equals(AnnotationType.CORRECT_MAPPING)).count();
                int incorrect = (int) votes.stream().filter(v->v.getVote().equals(AnnotationType.WRONG_MAPPING)).count();

                int[] row = new int[]{correct, incorrect};

                usuarios.addAll(votes.stream().map(v->v.getUser().getUsername()).collect(Collectors.toList()));

                pretable.add(row);
            }
        }

        table = new int[pretable.size()][];
        for (int i = 0; i < pretable.size(); i++) {
            table[i] = pretable.get(i);
        }

        anotaciones_N = pretable.size();
        logger.info("Han anotado los usuarios: "+usuarios);
        anotadores_n = usuarios.size();

//        // Normalizamos la tabla
//        for (int[] row : table) {
//            // Todos deben sumar n
//            int ayb = row[0] + row[1];
//            row[2] = anotadores_n - ayb;
//        }
    }

    public double get() {

        logger.info("Parameters up to this point: \n" +
                "\tTable rows"+table.length+"\n"+
                "\tn = "+anotadores_n+"\n"+
                "\tN = "+anotaciones_N+"\n"+
                "\tk = "+categorias_k+"\n");
        double P_ = 0;
        for (int[] row : table) {
            double sum = Arrays.stream(row).mapToLong(i -> i*i).sum();
            double P_i = 1.0/(anotadores_n * (anotadores_n-1)) * (sum-anotadores_n);
            P_ += P_i;
        }
        logger.info("P_ antes: "+P_);
        P_ /= anotaciones_N;
        logger.info("P_ después: "+P_);


        double P_e = 0;
        for (int i = 0; i < categorias_k; i++) {
            double sumColumn = 0;
            for (int[] row : table) {
                sumColumn += row[i];
            }
            sumColumn /= (anotadores_n * anotaciones_N);
            System.out.println("Suma columna "+i+" es: "+sumColumn);
            P_e += sumColumn * sumColumn;
        }
        logger.info("P_e antes: "+P_e);
        double kappa = (P_ - P_e) / (1 - P_e);
        logger.info("Kappa calculada: "+kappa);
        return kappa;
    }
}
