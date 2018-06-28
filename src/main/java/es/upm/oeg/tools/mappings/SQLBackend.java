package es.upm.oeg.tools.mappings;

import org.dbpedia.mappingschecker.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection to the SQL DB
 */
public class SQLBackend {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    private static final String SQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String jdbcURI;

    private Connection connection;

    public SQLBackend(String jdbcURI) {
        this.jdbcURI = jdbcURI;
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || connection.isClosed()) {
            logger.debug("Last connection was {}, so new connection will be opened", connection);
            Class.forName(SQL_DRIVER);
            connection = DriverManager.getConnection(this.jdbcURI);
        } else {
            logger.debug("Last connection {} is still valid", connection);
        }
        return connection;
    }
    public boolean closeConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                return true;
            } else {
                connection.close();
                return true;
            }
        } catch (SQLException sqlex) {
            logger.trace("Error {} when closing connection", sqlex);
            return false;
        }
    }
}
