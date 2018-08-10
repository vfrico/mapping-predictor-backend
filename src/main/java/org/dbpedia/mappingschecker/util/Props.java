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

    // SQL Files
    public static final String SQL_FILE_DIRECTORY = "SQL_FILE_DIRECTORY";
    public static final String SQL_FILE_SCHEMA = "SQL_FILE_SCHEMA";
    public static final String SQL_FILE_BASIC_DATA = "SQL_FILE_BASIC_DATA";

    // CSV sample files
    public static final String CSV_FILE_DIRECTORY = "CSV_FILE_DIRECTORY";
    public static final String CSV_SAMPLE_EN_ES = "CSV_SAMPLE_EN_ES";
    public static final String CSV_SAMPLE_ES_DE = "CSV_SAMPLE_ES_DE";
    public static final String CSV_SAMPLE_EN_EL_LIT = "CSV_SAMPLE_EN_EL_LIT";
    public static final String CSV_SAMPLE_EN_EL_IRI = "CSV_SAMPLE_EN_EL_IRI";
    public static final String CSV_SAMPLE_EN_NL_LIT = "CSV_SAMPLE_EN_NL_LIT";
    public static final String CSV_SAMPLE_EN_NL_IRI = "CSV_SAMPLE_EN_NL_IRI";
}
