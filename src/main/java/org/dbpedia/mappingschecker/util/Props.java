package org.dbpedia.mappingschecker.util;

public class Props {

    /**
     * Properties mapping:
     * left side: Java scope
     * Right side: properties/system env scope
     */

    // MySQL variables
    public static final String MYSQL_HOSTNAME   = "MYSQL_HOSTNAME";
    public static final String MYSQL_PORT       = "MYSQL_PORT";
    public static final String MYSQL_USER       = "MYSQL_USER";
    public static final String MYSQL_PASSWORD   = "MYSQL_ROOT_PASSWORD";
    public static final String MYSQL_DATABASE   = "MYSQL_MAPPER_DATABASE";

    public static final String MYSQL_TIMEZONE   = "MYSQL_MAPPER_SERVER_TIMEZONE";
    public static final String MYSQL_USE_SSL    = "MYSQL_MAPPER_USE_SSL";

    // Property to map a Properties file
    public static final String MAPPER_BACKEND_PROPERTIES_FILE = "MAPPER_BACKEND_PROPERTIES_FILE";
}
