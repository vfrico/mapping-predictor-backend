package es.upm.oeg.tools.mappings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.AnnotationType;
import es.upm.oeg.tools.mappings.beans.ClassificationResult;
import org.dbpedia.mappingschecker.util.Props;
import org.dbpedia.mappingschecker.util.Utils;
import org.dbpedia.mappingschecker.web.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.Result;

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
public class SQLAnnotationReader implements AnnotationReader {

    private static String test = "jdbc:mysql://localhost/dbpedia_mappings?user=dbpedia_mapping&password=dbpedia_mapping&serverTimezone=UTC";

    private SQLBackend database;

    private static Logger logger = LoggerFactory.getLogger(SQLAnnotationReader.class);

    //
    private static final String DEFAULT_USERNAME = "default";

    // SQL
    private static final String SCHEMA_NAME = "mappings_annotations";
    private static final String TABLE_ANNOTATIONS_NAME = "annotation";
    private static final String TABLE_USERS_NAME = "users";
    private static final String TABLE_VOTE_NAME = "vote";
    private static final String TABLE_CLASSIFICATION_RESULTS = "classification_results";
    private static final String TABLE_LOCK = "lock";
    private static final String TABLE_TEMPLATES = "templates";

    // SQL Queries
    private static final String SQL_INSERT_ANNOTATION = "INSERT INTO `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"` \n" +
            "( `langA`, `langB`, `templateA`, `templateB`, `attributeA`, `attributeB`, `propertyA`, `propertyB`, \n" +
            "\t`classA`, `classB`, `propDomainA`, `propDomainB`, `propRangeA`, `propRangeB`, \n" +
            "\t`c1`, `c2`, `c3A`, `c3B`, \n" +
            "\t`m1`, `m2`, `m3`, `m4A`, `m4B`, `m5A`, `m5B`, \n" +
            "\t`tb1`, `tb2`, `tb3`, `tb4`, `tb5`, `tb6`, `tb7`, `tb8`, `tb9`, `tb10`, `tb11`) \n" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String SQL_GET_ANNOTATION = "SELECT * from `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"` where id=?";

    private static final String SQL_INSERT_USER = "INSERT INTO `"+SCHEMA_NAME+"`.`"+TABLE_USERS_NAME+"` \n" +
            "( `username`, `email`, `password_md5`, `creation_date`)" +
            "VALUES ( ?, ?, ?, ?);";
    private static final String SQL_SELECT_USERNAME = "SELECT * FROM `"+SCHEMA_NAME+"`.`"+TABLE_USERS_NAME+"` \n" +
            "where `username`= ?;";
    private static final String SQL_SELECT_VOTE = "SELECT * FROM `"+SCHEMA_NAME+"`.`"+TABLE_VOTE_NAME+"` \n" +
            "WHERE annotation_id=?;";
    private static final String SQL_INSERT_VOTE = "INSERT INTO `"+SCHEMA_NAME+"`.`"+TABLE_VOTE_NAME+"` \n" +
            "(`annotation_id`, `vote`, `username`) " +
            "VALUES (?, ?, ?);";
    private static final String SQL_DELETE_USER = "DELETE FROM `mappings_annotations`.`users` WHERE username=?;";

    private static final String SQL_GET_ANNOTATIONS_BY_TEMPLATEB_LANGB = "SELECT * FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"` " +
            "WHERE templateB=? AND langB=?;";

    private static final String SQL_GET_ANNOTATIONS_BY_LANGS = "SELECT * FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"` " +
            "WHERE langA=? AND langB=?;";
    private static final String SQL_GET_ALL_TEMPLATES_LANG = "SELECT templateB FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"`  " +
            "WHERE langB=? GROUP BY templateB;";
    private static final String SQL_GET_ALL_TEMPLATES_LANG_PAIR = "SELECT templateB FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"`  " +
            "WHERE langA=? and langB=? GROUP BY templateB;";
    private static final String SQL_ADD_CLASSIFICATION_RESULT = "INSERT INTO `"+SCHEMA_NAME+"`.`"+TABLE_CLASSIFICATION_RESULTS+"` \n" +
            "(`id_annotation`, `classified_as`, `probability`) VALUES (?, ?, ?)" +
            "ON DUPLICATE KEY UPDATE `probability` = VALUES(`probability`)";
    private static final String SQL_ADD_CLASSIFICATION_RESULT_WITH_DATE = "INSERT INTO `"+SCHEMA_NAME+"`.`"+TABLE_CLASSIFICATION_RESULTS+"` \n" +
            "(`id_annotation`, `classified_as`, `probability`, `date`) VALUES (?, ?, ?, ?)" +
            "ON DUPLICATE KEY UPDATE `probability` = VALUES(`probability`) AND `date` = VALUES(`date`)";
    private static final String SQL_DELETE_CLASSIFICATION_RESULT = "DELETE FROM `"+SCHEMA_NAME+"`.`"+TABLE_CLASSIFICATION_RESULTS+"` \n" +
            "WHERE id_annotation = ?;";

    private static final String SQL_COUNT_CLASSIFICATION_FROM_TEMPLATENAME = "SELECT " +
            "count(*) as 'all', " +
            "sum(case classified_as when '"+AnnotationType.CORRECT_MAPPING.toString()+"' then 1 else 0 end) as 'correct', " +
            "sum(case classified_as when '"+AnnotationType.WRONG_MAPPING.toString()+"' then 1 else 0 end) as 'wrong' " +
            " FROM `"+SCHEMA_NAME+"`.`"+TABLE_CLASSIFICATION_RESULTS+"` " +
            "    where id_annotation in (" +
            "        SELECT id FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"` " +
            "        where templateB=?) " +
            "and probability >= 0.5;";

    private static final String SQL_GET_LANG_PAIRS_v1 = "SELECT langA, langB FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"` GROUP BY langA, langB;";
    private static final String SQL_GET_LANG_PAIRS_v2 = "SELECT DISTINCT langA, langB FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"`;";

    private static final String SQL_INSERT_NUMINSTANCES = "INSERT INTO `" + SCHEMA_NAME + "`.`"+TABLE_TEMPLATES+"`  " +
            " (`name`, `lang`, `num_instances`) VALUES (?, ?, ?) " +
            " ON DUPLICATE KEY UPDATE `num_instances` = VALUES(`num_instances`);";
    private static final String SQL_GET_NUMINSTANCES = "SELECT * FROM `"+SCHEMA_NAME+"`.`"+TABLE_TEMPLATES+"` " +
            " WHERE `name` = ? AND `lang` = ?;";

    private static final String SQL_TEST_IF_TEMPLATE_LOCKED = "SELECT `id` FROM`" + SCHEMA_NAME + "`.`"+TABLE_ANNOTATIONS_NAME+"`  " +
            "WHERE `templateB`=? and `id` in (" +
            "   SELECT `id_annotation` FROM `" + SCHEMA_NAME + "`.`"+TABLE_LOCK+"` WHERE `date_end` > NOW()  " +
            ");";

    public SQLAnnotationReader(String jdbcURI) {
        database = new SQLBackend(jdbcURI);
    }


    public boolean testConnection() {
        Connection conn = null;
        try {
            conn = database.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            logger.info("MetaData info: {}", metaData.toString());
            return true;
        } catch (SQLException sqlExc) {
            return false;
        } finally {
            closeResources(conn);
        }
    }

    private URL getResource(String resourceName) {
        return getClass().getClassLoader().getResource(Utils.fullPathSQL(resourceName).toString());
    }

    public boolean createTables() throws IOException, SQLException {
        URL sqlSchema = getResource(Props.SQL_FILE_SCHEMA);
        URL sqlBasicData = getResource(Props.SQL_FILE_BASIC_DATA);

        String sqlSchema_str, sqlBasicData_str;

        try {
            BufferedReader reader_schema = new BufferedReader(new InputStreamReader(sqlSchema.openStream(), StandardCharsets.UTF_8));
            StringBuilder sql_schema_sb = new StringBuilder();
            reader_schema.lines().forEach(line -> sql_schema_sb.append(line).append("\n"));

            BufferedReader reader_basicdata = new BufferedReader(new InputStreamReader(sqlBasicData.openStream(), StandardCharsets.UTF_8));
            StringBuilder sql_basicdata_sb = new StringBuilder();
            reader_basicdata.lines().forEach(line -> sql_basicdata_sb.append(line).append("\n"));

            sqlSchema_str = sql_schema_sb.toString();
            sqlBasicData_str = sql_basicdata_sb.toString();

        } catch (IOException ioexc) {
            logger.error("Unable to read file", ioexc);
            throw new IOException("Files "+sqlSchema+" and/or "+sqlBasicData+" can't be accessed on the filesystem, or from "+System.getProperty("user.dir"), ioexc);
        }

        if (sqlBasicData_str == null || Objects.equals(sqlBasicData_str, "") ||
                sqlSchema_str == null || Objects.equals(sqlBasicData_str, "")) {
            return false;
        } else {
            try {
                // Execute SQL files
                executeComplexQuery(sqlSchema_str);
                executeComplexQuery(sqlBasicData_str);
            } catch (SQLException e) {
                e.printStackTrace();
                throw e;
            } finally {
            }
        }
        return true;
    }

    public boolean executeComplexQuery(String query) throws SQLException {
        String[] statements = query.split(";");
        Connection conn = null;
        for (int i = 0; i < statements.length; i++) {
            if (statements[i].trim().equals("")) {
                // Do not execute empty lines
                continue;
            }

            PreparedStatement pstmt = null;
            try {
                conn = database.getConnection();
                pstmt = conn.prepareStatement(statements[i]);
                logger.info(pstmt.toString());
            } catch (SQLSyntaxErrorException sqlsyn) {
                throw sqlsyn;
            }
            pstmt.execute();
            pstmt.close();
            closeResources(conn);
        }
        return true;
    }

    public boolean addUser(UserDAO user) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            logger.info("Inserting on DB {}", user);
            pstmt = conn.prepareStatement(SQL_INSERT_USER);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword_md5());
            pstmt.setTimestamp(4, user.getCreation_date());
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    public UserDAO getUser(String username) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserDAO user = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_SELECT_USERNAME);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                logger.info("Resultado encontrado");

                user = new UserDAO();

                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword_md5(rs.getString("password_md5"));
                user.setCreation_date(rs.getTimestamp("creation_date"));
                user.setJwt(rs.getString("jwt"));
                user.setId(rs.getInt("idUsers"));
                user.setRole(UserRole.fromString(rs.getString("role")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return user;
    }

    public String getToken(String username) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String token = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement("SELECT * FROM mappings_annotations.users WHERE username=?;");
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                token = rs.getString("jwt");
            }

            return token;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    public boolean changeUserRole(String username, UserRole role) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement("UPDATE  `mappings_annotations`.`users` SET role=? WHERE username=?;");
            pstmt.setString(1, role.toString());
            pstmt.setString(2, username);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(pstmt, conn);
        }
    }

    public boolean changeUserPassword(String username, String password) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement("UPDATE  `mappings_annotations`.`users` SET password_md5=? WHERE username=?;");
            pstmt.setString(1, password);
            pstmt.setString(2, username);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(pstmt,conn);
        }
    }


    public boolean loginUser(String username, String token) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
//            database.closeConnection();

            pstmt = conn.prepareStatement("UPDATE mappings_annotations.users SET jwt=? WHERE username=?;");
            pstmt.setString(1, token);
            pstmt.setString(2, username);

            pstmt.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Exception: {}", e);
            return false;
        } finally {
            closeResources(pstmt, conn);
        }

    }

    public boolean logout(String username) {
        return loginUser(username, "");
    }

    public boolean deleteUser(String username) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            // safety first ;)
//            database.closeConnection();

            pstmt = conn.prepareStatement(SQL_DELETE_USER);
            pstmt.setString(1, username);
            pstmt.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("Exception: {}", e);
            return false;
        } finally {
            closeResources(pstmt, conn);
        }
    }

    public ClassificationResult getClassificationResultWithOpenedConnection(int annotationId, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement("SELECT * FROM `mappings_annotations`.`classification_results` where id_annotation = ?;");
            pstmt.setInt(1, annotationId);
            rs = pstmt.executeQuery();
            ClassificationResult entry = new ClassificationResult();

            while (rs.next()) {
                AnnotationType type = AnnotationType.fromString(rs.getString("classified_as"));
                double prob = rs.getDouble("probability");
                Timestamp ts = rs.getTimestamp("date");

                entry.setTimestamp(ts.getTime());
                entry.setClassifiedAs(type, prob);
            }

            return entry;
        } finally {
            closeResources(pstmt, rs);
        }
    }

    public ClassificationResult getClassificationResult(int annotationId) {
        Connection conn = null;
        ClassificationResult entry = null;
        try {
            conn = database.getConnection();
            entry = getClassificationResultWithOpenedConnection(annotationId, conn);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn);
        }
        return entry;
    }

    public boolean deleteClassificationResults(int annotationId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_DELETE_CLASSIFICATION_RESULT);
            pstmt.setInt(1, annotationId);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(pstmt, conn);
        }
    }

    public boolean addAllClassificationResults(Map<Annotation, ClassificationResult> classifiedOutput) throws SQLException {
        boolean success = true;
        Connection conn = null;
        try {
            conn = database.getConnection();
            logger.info("Start adding annotations");
            for (Annotation annotation : classifiedOutput.keySet()) {
                // logger.info("Annotation: " + classifiedOutput.get(annotation));
                success &= addClassificationResultWithOpenedConnection(((AnnotationDAO) annotation).getId(), classifiedOutput.get(annotation), conn);
            }
            logger.info("End adding annotations");
            return success;
        } catch (SQLException sqlEx) {
            logger.error("Found error: "+sqlEx);
            throw sqlEx;
        } finally {
            closeResources(conn);
        }
    }

    public boolean addClassificationResultWithOpenedConnection(int annotationId, ClassificationResult result, Connection connection) throws SQLException {
        boolean aggregated = true;
        PreparedStatement pstmt = null;

        try {
            for (AnnotationType annotation : result.getVotesMap().keySet()) {
                pstmt = connection.prepareStatement(SQL_ADD_CLASSIFICATION_RESULT);
                pstmt.setInt(1, annotationId);
                pstmt.setString(2, annotation.toString());
                pstmt.setDouble(3, result.getVotesMap().get(annotation));
                //pstmt.setTimestamp(4, new Timestamp(result.getTimestamp()));
                pstmt.execute();
                aggregated &= true;
            }
            return aggregated;
        }
        finally {
            closeResources(pstmt);
        }
    }

    public boolean addClassificationResult(int annotationId, ClassificationResult result) throws SQLException {
        Connection conn = null;
/*
        if (!deleteClassificationResults(annotationId)) {
            return false;
        } */
        try {
            conn = database.getConnection();
            return addClassificationResultWithOpenedConnection(annotationId, result, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(conn);
        }
    }

    public boolean addVote(VoteDAO vote) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
//            database.closeConnection();
            pstmt = conn.prepareStatement(SQL_INSERT_VOTE);
            pstmt.setInt(1, vote.getAnnotationId());
            pstmt.setString(2, vote.getVote().toString());
            pstmt.setString(3, vote.getUser().getUsername());
            logger.info("Before execute query "+pstmt.toString());
            pstmt.execute();
            logger.info("After query");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    public boolean deleteVote(int voteId) {
        PreparedStatement pstmt = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement("DELETE FROM `mappings_annotations`.`vote` WHERE `idvote`=?;");
            pstmt.setInt(1, voteId);

            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(pstmt, conn);
        }
        return false;
    }

    public List<VoteDAO> getVotesWithOpenedConnection(int annotationId, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(SQL_SELECT_VOTE);
            pstmt.setInt(1, annotationId);
            rs = pstmt.executeQuery();
            List<VoteDAO> voteList = new ArrayList<>();
            while (rs.next()) {
                VoteDAO vote = new VoteDAO();
                vote.setAnnotationId(annotationId);
                vote.setCreationDate(rs.getTimestamp("creation_date"));
                vote.setIdVote(rs.getInt("idvote"));
                String strVote = rs.getString("vote");
                vote.setVote(AnnotationType.fromString(strVote));
                String username = rs.getString("username");
                UserDAO annotator = new UserDAO();
                annotator.setUsername(username);
                vote.setUser(annotator);
                voteList.add(vote);
            }
            return voteList;
        } finally {
            closeResources(pstmt, rs);
        }
    }

    public List<VoteDAO> getVotes(int annotationId) {
        List<VoteDAO> voteList = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            voteList = getVotesWithOpenedConnection(annotationId, conn);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn);
        }
        return voteList;
    }

    public AnnotationDAO getAnnotation(int annotationId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AnnotationDAO entry = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_GET_ANNOTATION);
            pstmt.setInt(1, annotationId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                logger.info("Resultado encontrado");

                entry = parseAnnotation(rs, annotationId);
                entry.setVotes(getVotesWithOpenedConnection(annotationId, conn));
                entry.setClassificationResult(getClassificationResultWithOpenedConnection(annotationId, conn));
                entry.setLocks(getLocks(annotationId, conn));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return entry;
    }

    private AnnotationDAO parseAnnotation(ResultSet rs, int annotationId) throws SQLException {
        String langA = rs.getString("langA");
        String langB = rs.getString("langB");
        String templateA = rs.getString("templateA");
        String templateB = rs.getString("templateB");
        String attributeA = rs.getString("attributeA");
        String attributeB = rs.getString("attributeB");
        String propA = rs.getString("propertyA");
        String propB = rs.getString("propertyB");

        int m1 = rs.getInt("m1");

        AnnotationDAO entry = new AnnotationDAO(templateA, templateB, attributeA, attributeB,
                propA, propB, m1, annotationId);
        entry.setLangA(langA);
        entry.setLangB(langB);

        entry.setClassA(rs.getString("classA"));
        entry.setClassB(rs.getString("classB"));
        entry.setDomainPropA(rs.getString("propDomainA"));
        entry.setDomainPropB(rs.getString("propDomainB"));
        entry.setRangePropA(rs.getString("propRangeA"));
        entry.setRangePropB(rs.getString("propRangeB"));

        entry.setC1(rs.getDouble("c1"));
        entry.setC2(rs.getDouble("c2"));
        entry.setC3a(rs.getDouble("c3a"));
        entry.setC3b(rs.getDouble("c3b"));

        entry.setM2(rs.getInt("m2"));
        entry.setM3(rs.getInt("m3"));
        entry.setM4a(rs.getInt("m4a"));
        entry.setM4b(rs.getInt("m4b"));
        entry.setM5a(rs.getInt("m5a"));
        entry.setM5b(rs.getInt("m5b"));

        entry.setTb1(rs.getInt("tb1"));
        entry.setTb2(rs.getInt("tb2"));
        entry.setTb3(rs.getInt("tb3"));
        entry.setTb4(rs.getInt("tb4"));
        entry.setTb5(rs.getInt("tb5"));
        entry.setTb6(rs.getInt("tb6"));
        entry.setTb7(rs.getInt("tb7"));
        entry.setTb8(rs.getInt("tb8"));
        entry.setTb9(rs.getInt("tb9"));
        entry.setTb10(rs.getInt("tb10"));
        entry.setTb11(rs.getInt("tb11"));
        return entry;
    }

    public TemplateDAO collectTemplateStats(String templateName, String lang) throws SQLException {
        Connection conn = null;
        try {
            conn = database.getConnection();
            return collectTemplateStatsWithOpenedConnection(templateName, lang, conn);
        } finally {
            closeResources(conn);
        }
    }

    private TemplateDAO collectTemplateStatsWithOpenedConnection(String templateName, String lang, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            TemplateDAO template = new TemplateDAO(templateName, lang);

            pstmt = conn.prepareStatement(SQL_COUNT_CLASSIFICATION_FROM_TEMPLATENAME);
            pstmt.setString(1, templateName);

            rs = pstmt.executeQuery();
            logger.info("Query is: "+pstmt.toString());
            while (rs.next()) {
                int allAnnotations = rs.getInt("all");
                int correct = rs.getInt("correct");
                int wrong = rs.getInt("wrong");
                template.setAllAnnotations(allAnnotations);
                template.setCorrectAnnotations(correct);
                template.setWrongAnnotations(wrong);
            }
            return template;
        } finally {
            closeResources(pstmt, rs);
        }
    }

    public List<TemplateDAO> getAllTemplatesByLangPair(LangPair langPair) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        List<TemplateDAO> templates = new ArrayList<>();
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_GET_ALL_TEMPLATES_LANG_PAIR);
            pstmt.setString(1, langPair.getLangA());
            pstmt.setString(2, langPair.getLangB());

            rs = pstmt.executeQuery();

            while (rs.next()) {
                logger.info("Resultado encontrado:");
                String template = rs.getString("templateB");

                templates.add(collectTemplateStatsWithOpenedConnection(template, langPair.getLangB(), conn));
                //templates.add(new TemplateDAO(template, lang));
            }

            return templates;
        } catch (SQLException sqle) {
            logger.warn("SQL exception", sqle);
            throw sqle;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    public List<TemplateDAO> getAllTemplatesByLang(String lang) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        List<TemplateDAO> templates = new ArrayList<>();

        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_GET_ALL_TEMPLATES_LANG);
            pstmt.setString(1, lang);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                logger.info("Resultado encontrado:");
                String template = rs.getString("templateB");

                templates.add(collectTemplateStatsWithOpenedConnection(template, lang, conn));
                //templates.add(new TemplateDAO(template, lang));
            }

            return templates;
        } catch (SQLException sqle) {
            logger.warn("SQL exception", sqle);
            throw sqle;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    public List<AnnotationDAO> getAnnotationsByTemplateB(String template, String lang) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection conn = null;
        List<AnnotationDAO> allAnnotations = new ArrayList<>();
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_GET_ANNOTATIONS_BY_TEMPLATEB_LANGB);
            pstmt.setString(1, template);
            pstmt.setString(2, lang);
            rs = pstmt.executeQuery();
            logger.trace("Query is: {}", pstmt.toString());
            while (rs.next()) {
                int annotationId = rs.getInt("id");
                AnnotationDAO entry = parseAnnotation(rs, annotationId);
                entry.setVotes(getVotesWithOpenedConnection(annotationId, conn));
                entry.setClassificationResult(getClassificationResultWithOpenedConnection(annotationId, conn));
                entry.setLocks(getLocks(annotationId, conn));
                allAnnotations.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return allAnnotations;
    }

    public List<AnnotationDAO> getAllAnnotations(String lang1, String lang2) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<AnnotationDAO> allAnnotations = new ArrayList<>();
        Connection conn = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_GET_ANNOTATIONS_BY_LANGS);
            pstmt.setString(1, lang1);
            pstmt.setString(2, lang2);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int annotationId = rs.getInt("id");
                //logger.info("Resultado encontrado");

                AnnotationDAO entry = parseAnnotation(rs, annotationId);/*
                entry.setVotes(getVotesWithOpenedConnection(annotationId, conn));
                entry.setClassificationResult(getClassificationResultWithOpenedConnection(annotationId, conn));
                entry.setLocks(getLocks(annotationId, conn)); */
                allAnnotations.add(entry);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }

        return allAnnotations;
    }

    public AnnotationDAO addAnnotation(Annotation annotation) {
        PreparedStatement pstmt = null;
        AnnotationDAO result = null;
        Connection conn = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_INSERT_ANNOTATION, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, annotation.getLangA());
            pstmt.setString(2, annotation.getLangB());
            pstmt.setString(3, annotation.getTemplateA());
            pstmt.setString(4, annotation.getTemplateB());
            pstmt.setString(5, annotation.getAttributeA());
            pstmt.setString(6, annotation.getAttributeB());
            pstmt.setString(7, annotation.getPropA());
            pstmt.setString(8, annotation.getPropB());
            pstmt.setString(9, annotation.getClassA());
            pstmt.setString(10, annotation.getClassB());
            pstmt.setString(11, annotation.getDomainPropA());
            pstmt.setString(12, annotation.getDomainPropB());
            pstmt.setString(13, annotation.getRangePropA());
            pstmt.setString(14, annotation.getRangePropB());

            pstmt.setDouble(15, annotation.getC1());
            pstmt.setDouble(16, annotation.getC2());
            pstmt.setDouble(17, annotation.getC3a());
            pstmt.setDouble(18, annotation.getC3b());

            pstmt.setInt(19, (int)annotation.getM1());
            pstmt.setInt(20, (int)annotation.getM2());
            pstmt.setInt(21, (int)annotation.getM3());
            pstmt.setInt(22, (int)annotation.getM4a());
            pstmt.setInt(23, (int)annotation.getM4b());
            pstmt.setInt(24, (int)annotation.getM5a());
            pstmt.setInt(25, (int)annotation.getM5b());

            pstmt.setInt(26, annotation.getTb1());
            pstmt.setInt(27, annotation.getTb2());
            pstmt.setInt(28, annotation.getTb3());
            pstmt.setInt(29, annotation.getTb4());
            pstmt.setInt(30, annotation.getTb5());
            pstmt.setInt(31, annotation.getTb6());
            pstmt.setInt(32, annotation.getTb7());
            pstmt.setInt(33, annotation.getTb8());
            pstmt.setInt(34, annotation.getTb9());
            pstmt.setInt(35, annotation.getTb10());
            pstmt.setInt(36, annotation.getTb11());

            int sqlOutput = pstmt.executeUpdate();
            logger.debug("Query executed: "+pstmt.toString()+" Result: "+sqlOutput);

            long annotationId = 0;
            ResultSet genKeys = pstmt.getGeneratedKeys();
            while (genKeys.next()) {
                annotationId = genKeys.getLong(1);
            }
            logger.trace("Annotation has id="+annotationId);
            result = new AnnotationDAO(annotation);
            result.setId((int) annotationId);

            // if annotated, assign it to default mapper user
            AnnotationType tipo = annotation.getAnnotation();
            if ((tipo != null) && (tipo != AnnotationType.UNKNOWN) && (annotationId != 0)) {
                VoteDAO voto = new VoteDAO();
                UserDAO user = new UserDAO();
                user.setUsername(DEFAULT_USERNAME);
                voto.setUser(user);
                voto.setVote(tipo);
                voto.setAnnotationId((int)annotationId);
                addVote(voto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeResources(pstmt, conn);
        }
        return result;
    }

    public boolean setLock(LockDAO lock, int idAnnotation) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO `mappings_annotations`.`lock` (`date_start`, `date_end`, `id_annotation`, `username`) VALUES (?, ?, ?, ?);");
            pstmt.setTimestamp(1, new Timestamp(lock.getDateStart()));
            pstmt.setTimestamp(2, new Timestamp(lock.getDateEnd()));
            pstmt.setInt(3, idAnnotation);
            pstmt.setString(4, lock.getUser().getUsername());

            pstmt.execute();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(pstmt, conn);
        }
    }

    public boolean unlockAnnotation(int idAnnotation, String username) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement("UPDATE `mappings_annotations`.`lock` SET `date_end`=NOW() WHERE `date_end` >= NOW() and `id_annotation` = ? and `username` = ?;");
            pstmt.setInt(1, idAnnotation);
            pstmt.setString(2, username);
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(pstmt, conn);
        }
    }

    public List<LockDAO> getLocks(int idAnnotation, Connection conn) throws SQLException {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<LockDAO> locks = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement("SELECT * FROM `mappings_annotations`.`lock` WHERE id_annotation=? and date_end > NOW();");
            pstmt.setInt(1, idAnnotation);
            logger.trace("SQL get locks: "+pstmt.toString());
            rs = pstmt.executeQuery();

            while (rs.next()) {
                LockDAO lock = new LockDAO();
                lock.setDateStart(rs.getTimestamp("date_start").getTime());
                lock.setDateEnd(rs.getTimestamp("date_end").getTime());
                lock.setLockId(rs.getInt("idLock"));
                UserDAO user = new UserDAO();
                user.setUsername(rs.getString("username"));
                lock.setLocked(true);
                lock.setUser(user);
                locks.add(lock);
            }
            return locks;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            closeResources(pstmt, rs);
        }
    }

    public List<LangPair> getLangPairs() throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_GET_LANG_PAIRS_v2);
            rs = pstmt.executeQuery();

            List<LangPair> pairs = new ArrayList<>();

            while (rs.next()) {

                String langA = rs.getString("langA");
                String langB = rs.getString("langB");
                pairs.add(new LangPair(langA, langB));
            }
            return pairs;
        } catch (SQLException sqlex) {
            logger.error("An SQL exception has been detected: {}", sqlex);
            throw sqlex;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    public int getNumInstancesOfTemplate(String template, String lang) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_GET_NUMINSTANCES);
            pstmt.setString(1, template);
            pstmt.setString(2, lang);
            rs = pstmt.executeQuery();

            int numInstances = -1;
            while (rs.next()) {
                numInstances = rs.getInt("num_instances");
            }
            return numInstances;
        } catch (SQLException sqlex) {
            logger.error("An SQL exception has been detected: {}", sqlex);
            throw sqlex;
        } finally {
            closeResources(conn, pstmt, rs);
        }
    }

    public boolean insertOrUpdateTemplateInstances(String template, String lang, int numInstances) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = database.getConnection();
            pstmt = conn.prepareStatement(SQL_INSERT_NUMINSTANCES);
            pstmt.setString(1, template);
            pstmt.setString(2, lang);
            pstmt.setInt(3, numInstances);
            pstmt.execute();
            return true;
        } catch (SQLException sqlex) {
            logger.error("An SQL exception has been detected: {}", sqlex);
            throw sqlex;
        } finally {
            closeResources(pstmt, conn);
        }
    }


    public List<TemplateDAO> checkIfLocked(List<TemplateDAO> templates) throws SQLException {
        Connection conn = null;
        try {
            conn = database.getConnection();
            for (TemplateDAO template : templates) {
                boolean isLocked = testIfTemplateLockedWithOpenedConnection(template.getTemplate(), conn);
                template.setLocked(isLocked);
            }
            return templates;
        } catch (SQLException sqlex) {
            logger.error("An SQL error was found: "+sqlex);
            throw sqlex;
        } finally {
            closeResources(conn);
        }
    }

    public boolean testIfTemplateLockedWithOpenedConnection(String template, Connection conn) throws SQLException {
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(SQL_TEST_IF_TEMPLATE_LOCKED);
            pstmt.setString(1, template);
            rs = pstmt.executeQuery();

            List<Integer> lockedIdsFromTemplate = new ArrayList<>();

            while (rs.next()) {
                lockedIdsFromTemplate.add(rs.getInt("id"));
            }

            return !lockedIdsFromTemplate.isEmpty();
        } catch (SQLException sqlex) {
            logger.error("An SQL exception has been detected: {}", sqlex);
            throw sqlex;
        } finally {
            closeResources(pstmt,rs);
        }
    }

    public static void main(String[] args) {
        logger.info("Start");
        SQLAnnotationReader n = new SQLAnnotationReader(SQLAnnotationReader.test);

    }
    private void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        closeResources(pstmt, rs);
        try { conn.close(); } catch (Exception exc) {logger.warn("ApiError closing Connection");}
    }

    private void closeResources(PreparedStatement pstmt, ResultSet rs) {
        try { rs.close(); } catch (Exception exc) {logger.warn("ApiError closing ResultSet");}
        try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
    }

    private void closeResources(PreparedStatement pstmt, Connection conn) {
        try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        try { conn.close(); } catch (Exception exc) {logger.warn("ApiError closing DB connection");}
    }

    private void closeResources(Connection conn) {
        try { conn.close(); } catch (Exception exc) {logger.warn("ApiError closing DB connection");}
    }

    private void closeResources(PreparedStatement pstmt) {
        try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
    }

}

