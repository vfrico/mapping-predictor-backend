package es.upm.oeg.tools.mappings;

import org.apache.commons.dbcp2.BasicDataSource;
import org.dbpedia.mappingschecker.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
 *
 * Manages the connection to the SQL DB
 */
public class SQLBackend {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    private static final String SQL_DRIVER = "com.mysql.cj.jdbc.Driver";
    private String jdbcURI;

    private BasicDataSource dataSource;

    public SQLBackend(String jdbcURI) {
        this.jdbcURI = jdbcURI;

        dataSource = new BasicDataSource();
        dataSource.setUrl(jdbcURI);
        dataSource.setDriverClassName(SQL_DRIVER);
    }

    public Connection getConnection() throws SQLException {
//        if (connection == null || connection.isClosed()) {
//            logger.debug("Last connection was {}, so new connection will be opened", connection);
//            Class.forName(SQL_DRIVER);
//            connection = DriverManager.getConnection(this.jdbcURI);
//        } else {
//            logger.debug("Last connection {} is still valid", connection);
//        }
        return dataSource.getConnection();
    }

//    @Deprecated
//    public boolean closeConnection() {
//        try {
//            if (connection == null || connection.isClosed()) {
//                return true;
//            } else {
//                connection.close();
//                return true;
//            }
//        } catch (SQLException sqlex) {
//            logger.trace("ApiError {} when closing connection", sqlex);
//            return false;
//        }
//    }
}
