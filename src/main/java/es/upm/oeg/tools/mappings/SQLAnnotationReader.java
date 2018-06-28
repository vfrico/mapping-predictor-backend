package es.upm.oeg.tools.mappings;

import java.sql.*;

import es.upm.oeg.tools.mappings.beans.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLAnnotationReader implements AnnotationReader {

    private static String test = "jdbc:mysql://localhost/dbpedia_mappings?user=dbpedia_mapping&password=dbpedia_mapping&serverTimezone=UTC";

    private SQLBackend database;

    private static Logger logger = LoggerFactory.getLogger(SQLAnnotationReader.class);

    // SQL
    private static final String SCHEMA_NAME = "";
    private static final String TABLE_NAME = "";

    public SQLAnnotationReader(String jdbcURI) {
        database = new SQLBackend(jdbcURI);
    }


    public boolean testConnection() {
        try {
            DatabaseMetaData metaData = database.getConnection().getMetaData();
            logger.info("MetaData info: {}", metaData.toString());
            return true;
        } catch (SQLException sqlExc) {
            return false;
        } catch (ClassNotFoundException cnfexc) {
            return false;
        }
    }

    public static void main(String[] args) {
        logger.info("Start");
        SQLAnnotationReader n = new SQLAnnotationReader(SQLAnnotationReader.test);

    }

    @Override
    public Annotation getAnnotation(int id) {
        try {
            database.getConnection().prepareStatement("SELECT ");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
