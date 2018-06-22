package es.upm.oeg.tools.mappings;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import es.upm.oeg.tools.mappings.beans.Annotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLAnnotationReader implements AnnotationReader {

    protected static final String SQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String test = "jdbc:mysql://localhost/dbpedia_mappings?user=dbpedia_mapping&password=dbpedia_mapping&serverTimezone=UTC";

    private final Connection connection;

    private static Logger logger = LoggerFactory.getLogger(SQLAnnotationReader.class);

    // SQL
    private static final String SCHEMA_NAME = "";
    private static final String TABLE_NAME = "";

    public SQLAnnotationReader(String database) throws SQLException, ClassNotFoundException {
        Class.forName(SQL_DRIVER);
        connection = DriverManager.getConnection(database);
        logger.info("It works!");

        connection.close();
    }

    public static void main(String[] args) {
        logger.info("Start");
        try {
            SQLAnnotationReader n = new SQLAnnotationReader(SQLAnnotationReader.test);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Annotation getAnnotation(int id) {
        try {
            connection.prepareStatement("SELECT ");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
