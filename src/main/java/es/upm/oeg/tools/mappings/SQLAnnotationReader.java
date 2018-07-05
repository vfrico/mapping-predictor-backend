package es.upm.oeg.tools.mappings;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import es.upm.oeg.tools.mappings.beans.Annotation;
import es.upm.oeg.tools.mappings.beans.AnnotationType;
import org.dbpedia.mappingschecker.web.AnnotationDAO;
import org.dbpedia.mappingschecker.web.UserDAO;
import org.dbpedia.mappingschecker.web.VoteDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public boolean createTables() {
        return false;
        // TODO: (use .sql file)
    }

    public boolean addUser(UserDAO user) {
        PreparedStatement pstmt = null;
        try {
            pstmt = database.getConnection().prepareStatement(SQL_INSERT_USER);
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword_md5());
            pstmt.setTimestamp(4, user.getCreation_date());
            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        }
        return false;
    }

    public UserDAO getUser(String username) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        UserDAO user = null;
        try {
            pstmt = database.getConnection().prepareStatement(SQL_SELECT_USERNAME);
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
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { rs.close(); } catch (Exception exc) {logger.warn("ApiError closing ResultSet");}
            try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        }

        return user;
    }

    public boolean addVote(VoteDAO vote) {
        PreparedStatement pstmt = null;
        try {
            pstmt = database.getConnection().prepareStatement(SQL_INSERT_VOTE);
            pstmt.setInt(1, vote.getAnnotationId());
            pstmt.setString(2, vote.getVote().toString());
            pstmt.setString(3, vote.getUser().getUsername());

            pstmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        }
        return false;
    }

    public List<VoteDAO> getVotes(int annotationId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<VoteDAO> voteList = null;
        try {
            pstmt = database.getConnection().prepareStatement(SQL_SELECT_VOTE);
            pstmt.setInt(1, annotationId);
            rs = pstmt.executeQuery();
            voteList = new ArrayList<>();
            while (rs.next()) {
                logger.info("Resultado encontrado");
                VoteDAO vote = new VoteDAO();
                vote.setAnnotationId(annotationId);
                vote.setCreationDate(rs.getTimestamp("creation_date"));
                String strVote = rs.getString("vote");
                vote.setVote(AnnotationType.fromString(strVote));
                String username = rs.getString("username");
                UserDAO annotator = new UserDAO();
                annotator.setUsername(username);
                vote.setUser(annotator);
                voteList.add(vote);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { rs.close(); } catch (Exception exc) {logger.warn("ApiError closing ResultSet");}
            try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        }

        return voteList;
    }

    public AnnotationDAO getAnnotation(int annotationId) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        AnnotationDAO entry = null;
        try {
            pstmt = database.getConnection().prepareStatement(SQL_GET_ANNOTATION);
            pstmt.setInt(1, annotationId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                logger.info("Resultado encontrado");
                String langA = rs.getString("langA");
                String langB = rs.getString("langB");
                String templateA = rs.getString("templateA");
                String templateB = rs.getString("templateB");
                String attributeA = rs.getString("attributeA");
                String attributeB = rs.getString("attributeB");
                String propA = rs.getString("propertyA");
                String propB = rs.getString("propertyB");

                int m1 = rs.getInt("m1");

                entry = new AnnotationDAO(templateA, templateB, attributeA, attributeB,
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

                // TODO: Relate annotation result and user
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { rs.close(); } catch (Exception exc) {logger.warn("ApiError closing ResultSet");}
            try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        }

        return entry;
    }

    public List<AnnotationDAO> getAllAnnotations(String lang1, String lang2) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<AnnotationDAO> allAnnotations = new ArrayList<>();
        try {
            pstmt = database.getConnection().prepareStatement("SELECT * FROM `"+SCHEMA_NAME+"`.`"+TABLE_ANNOTATIONS_NAME+"` WHERE langA=? AND langB=?;");
            pstmt.setString(1, lang1);
            pstmt.setString(2, lang2);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                logger.info("Resultado encontrado");
                String langA = rs.getString("langA");
                String langB = rs.getString("langB");
                String templateA = rs.getString("templateA");
                String templateB = rs.getString("templateB");
                String attributeA = rs.getString("attributeA");
                String attributeB = rs.getString("attributeB");
                String propA = rs.getString("propertyA");
                String propB = rs.getString("propertyB");

                int m1 = rs.getInt("m1");
                int annId = rs.getInt("id");
                AnnotationDAO entry = new AnnotationDAO(templateA, templateB, attributeA, attributeB,
                        propA, propB, m1, annId);
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

                allAnnotations.add(entry);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try { rs.close(); } catch (Exception exc) {logger.warn("ApiError closing ResultSet");}
            try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        }

        return allAnnotations;
    }

    public AnnotationDAO addAnnotation(Annotation annotation) {
        PreparedStatement pstmt = null;
        AnnotationDAO result = null;
        try {
            pstmt = database.getConnection().prepareStatement(SQL_INSERT_ANNOTATION, Statement.RETURN_GENERATED_KEYS);
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
            logger.info("Query executed: "+pstmt.toString()+" Result: "+sqlOutput);

            long annotationId = 0;
            ResultSet genKeys = pstmt.getGeneratedKeys();
            while (genKeys.next()) {
                annotationId = genKeys.getLong(1);
            }
            logger.info("Annotation has id="+annotationId);
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try { pstmt.close(); } catch (Exception exc) {logger.warn("ApiError closing PreparedStatement");}
        }
        return result;
    }

    public static void main(String[] args) {
        logger.info("Start");
        SQLAnnotationReader n = new SQLAnnotationReader(SQLAnnotationReader.test);

    }
}

