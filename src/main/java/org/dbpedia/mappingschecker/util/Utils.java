package org.dbpedia.mappingschecker.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import es.upm.oeg.tools.mappings.SQLAnnotationReader;
import org.dbpedia.mappingschecker.web.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Utils {

    private static String propPath = "src/main/resources/mapper.properties";
//    private static String sqlFolderPath = "src/main/resources/sql";
    private static String sqlFolderPath = "sql";

    private static String propEnvVar = Props.MAPPER_BACKEND_PROPERTIES_FILE;

    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    private static Map<String, String> propsCache;

    /**
     * Generates a full mysql config string. Example of the output:
     *
     * mysql://localhost:3306/mysql?user=root&password=example&serverTimezone=UTC&useSSL=false
     *
     * @return
     */
    public static String getMySqlConfig() {
        Map<String, String> props = getProps();
        StringBuilder sb = new StringBuilder("mysql://");
        sb.append(props.get(Props.MYSQL_HOSTNAME));
        sb.append(":");
        sb.append(props.get(Props.MYSQL_PORT));
        sb.append("/");
        sb.append(props.get(Props.MYSQL_DATABASE));
        sb.append("?");
        sb.append("user="+props.get(Props.MYSQL_USER));
        sb.append("&");
        sb.append("password="+props.get(Props.MYSQL_PASSWORD));
        sb.append("&");
        sb.append("serverTimezone="+props.get(Props.MYSQL_TIMEZONE));
        sb.append("&");
        sb.append("useSSL="+props.get(Props.MYSQL_USE_SSL));

        return sb.toString();
    }

    public static Map<String, String> getProps() {
        if (propsCache == null) {
            propsCache = loadProperties();
        }
        return propsCache;
    }

    /**
     * This method returns the configuration properties to run the mapper backend.
     *
     * Firstly, loads default properties
     * Secondly, loads a .properties file.
     *      This file can be located on a default location
     *      OR can be overrided with MAPPER_BACKEND_PROPERTIES_FILE system variable
     * Thirdly, looks for System Env variables and overrides all previously loaded variables.
     *
     * @return
     */
    public static Map<String, String> loadProperties() {
        Map<String, String> map = new HashMap<>();

        map.put(Props.MYSQL_HOSTNAME, "localhost");
        map.put(Props.MYSQL_PORT, "3306");
        map.put(Props.MYSQL_USER, "root");
        map.put(Props.MYSQL_PASSWORD, "example");
        map.put(Props.MYSQL_DATABASE, "mysql");
        map.put(Props.MYSQL_TIMEZONE, "UTC");
        map.put(Props.MYSQL_USE_SSL, "false");

        map.put(Props.SQL_FILE_SCHEMA, "db.sql");
        map.put(Props.SQL_FILE_BASIC_DATA, "basic_data.sql");
        map.put(Props.SQL_FILE_DIRECTORY, sqlFolderPath);

        Properties prop = new Properties();
        try {
            prop.load(findPropFile());
            for (String name : prop.stringPropertyNames()) {
                String actualMap = map.get(name);
                String defaultForThisName;
                if (actualMap == null || actualMap.equals("")) {
                    defaultForThisName = "";
                } else {
                    defaultForThisName = prop.getProperty(name);
                }

                map.put(name, defaultForThisName);
            }
        } catch (IOException ioex) {
            logger.warn("Properties file could not be readed. Using default properties");
        }
        Map<String, String> sysMap = updateWithSystemEnv(map);
        return sysMap;
    }

    /**
     * Updates the Map of properties with system environment properties
     * @param props
     * @return
     */
    private static Map<String, String> updateWithSystemEnv(Map<String, String> props) {
        // Find if some of the key exist on system var and substitute with its value
        for (String propKey : props.keySet()) {
            String systemVar = System.getenv(propKey);
            if (!(systemVar == null || systemVar.equals(""))) {
                props.put(propKey, systemVar);
            }
        }
        return props;
    }

    /**
     * Returns a FileInputStream or exception. Gets the default property file or
     * looks for getenv MAPPER_BACKEND_PROPERTIES_FILE
     * @return
     * @throws FileNotFoundException
     */
    private static FileInputStream findPropFile() throws FileNotFoundException {
        String filePath = System.getenv(propEnvVar);
        System.out.println(filePath);
        if (filePath == null || filePath.equals("") || !fileExists(filePath)) {
            filePath = propPath;
        }
        return new FileInputStream(filePath);
    }

    /**
     * Helper function to check if a file exists
     * @param file
     * @return
     */
    private static boolean fileExists(String file) {
        File f = new File(file);
        return f.exists();
    }

    public static void main(String[] args) throws IOException {
        System.out.println(System.getenv("none"));
        System.out.println("Props: "+loadProperties());
        System.out.println(getMySqlConfig());
    }

    public static Path fullPathSQL(String sqlFileProp) {
        Map<String, String> props = getProps();
        String path = props.get(Props.SQL_FILE_DIRECTORY);
        String file = props.get(sqlFileProp);

        Path fullpath = Paths.get(path, file);
        return fullpath;
    }

    public static Algorithm getJWTAlgorithm() {
        return Algorithm.HMAC256("mappingpedia");
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static Date getCurrentDate() {
        return new Date(getCurrentTimestamp());
    }

    public static String getToken(UserDAO user) {
        Date now = getCurrentDate();
        // 1 month = 30 * 24 * 60 * 60 * 1000 ms
        Date expires = new Date(getCurrentTimestamp() + 30*24*60*60*1000L);
        logger.info("Now= "+now+", expires="+expires);
        String token;
        try {
            token = JWT.create()
                    .withIssuer("DBpedia")
                    .withClaim("username", user.getUsername())
                    .withClaim("email", user.getEmail())
                    .withIssuedAt(now)
                    .withExpiresAt(expires)
                    .sign(getJWTAlgorithm());
        } catch (JWTCreationException jwtexc) {
            logger.error("Error on creating token for user:"+user);
            return "";
        }
        return token;
    }

    public static DecodedJWT checkToken(String token) {
        try {
            Algorithm alg = Algorithm.HMAC256("mappingpedia");
            JWTVerifier verifier = JWT.require(alg)
                    .withIssuer("DBpedia")
                    .build();

            return verifier.verify(token);
        } catch (SignatureVerificationException sig) {
            logger.warn("Fails to verify signature");
            DecodedJWT jwt = JWT.decode(token);
            return jwt;
        } catch (JWTVerificationException exc) {
            logger.warn("Invalid token: {}", token, exc);
            return null;
        }

    }

    public static boolean verifyUser(String token, String username) {
        DecodedJWT jwt = checkToken(token);
        if (jwt == null) {
            return false;
        }
//        logger.info("Payload: "+jwt.getPayload());
//        JSONObject js = JSON.parseObject(jwt.getPayload());
//        logger.info("JS: "+js);
        String jwt_username = jwt.getClaim("username").asString();
        logger.info("JWTUsername: "+jwt_username);
        return jwt_username.equals(username);
    }

    public static String getUsername(String token) {
        DecodedJWT jwt = checkToken(token);
        if (jwt == null) {
            return null;
        }

        JSONObject js = JSON.parseObject(jwt.getPayload());
        logger.info("JS: "+js);
        return js.getString("username");

    }

    public static UserDAO getUserDao(String token) {
        String mysqlConfig = "jdbc:"+Utils.getMySqlConfig();
        SQLAnnotationReader sql = new SQLAnnotationReader(mysqlConfig);
        return sql.getUser(getUsername(token));
    }
}
