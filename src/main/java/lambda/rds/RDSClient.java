package lambda.rds;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rds.auth.GetIamAuthTokenRequest;
import com.amazonaws.services.rds.auth.RdsIamAuthTokenGenerator;
import lambda.constants.Constants;
import lambda.rds.druid.DBPoolConnection;
import lambda.rds.model.Contract;
import lambda.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.*;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/7/2018
 */
public class RDSClient {
    private RDSClient() {
    }

    private static class RDSClientHolder {

        private static final String SSL_CERTIFICATE = "rds-ca-2015-us-west-2.pem";
        private static final String KEY_STORE_TYPE = "JKS";
        private static final String KEY_STORE_PROVIDER = "SUN";
        private static final String KEY_STORE_FILE_PREFIX = "sys-connect-via-ssl-test-cacerts";
        private static final String KEY_STORE_FILE_SUFFIX = ".jks";
        private static final String DEFAULT_KEY_STORE_PASSWORD = "changeit";

        private static Connection connection = getDBConnection();

        private static Connection getDBConnection() {
            try {
//                return DriverManager.getConnection(Constants.RDS_NORTHWEST_CONTRACT_JDBC, setConnectionProp());
                DruidPooledConnection connection = DBPoolConnection.getConnection();
                connection.setAutoCommit(true);
                return connection;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        private static Connection getDBConnectionUsingIAM() {
            try {
                setSslProperties();
                return DriverManager.getConnection(Constants.RDS_NORTHWEST_CONTRACT_JDBC, setConnectionProp());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * This method sets the SSL properties which specify the key store file, its type and password:
         * @throws Exception
         */
        private static void setSslProperties() throws Exception {
            System.setProperty("javax.net.ssl.trustStore", createKeyStoreFile());
            System.setProperty("javax.net.ssl.trustStoreType", KEY_STORE_TYPE);
            System.setProperty("javax.net.ssl.trustStorePassword", DEFAULT_KEY_STORE_PASSWORD);
        }

        /**
         * This method returns the path of the Key Store File needed for the SSL verification during the IAM Database Authentication to
         * the db instance.
         * @return
         * @throws Exception
         */
        private static String createKeyStoreFile() throws Exception {
            return createKeyStoreFile(createCertificate()).getPath();
        }

        /**
         *  This method generates the SSL certificate
         * @return
         * @throws Exception
         */
        private static X509Certificate createCertificate() throws Exception {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            URL url = new File(SSL_CERTIFICATE).toURI().toURL();
            if (url == null) {
                throw new Exception();
            }
            try (InputStream certInputStream = url.openStream()) {
                return (X509Certificate) certFactory.generateCertificate(certInputStream);
            }
        }

        /**
         * This method creates the Key Store File
         * @param rootX509Certificate - the SSL certificate to be stored in the KeyStore
         * @return
         * @throws Exception
         */
        private static File createKeyStoreFile(X509Certificate rootX509Certificate) throws Exception {
            File keyStoreFile = File.createTempFile(KEY_STORE_FILE_PREFIX, KEY_STORE_FILE_SUFFIX);
            try (FileOutputStream fos = new FileOutputStream(keyStoreFile.getPath())) {
                KeyStore ks = KeyStore.getInstance(KEY_STORE_TYPE, KEY_STORE_PROVIDER);
                ks.load(null);
                ks.setCertificateEntry("rootCaCertificate", rootX509Certificate);
                ks.store(fos, DEFAULT_KEY_STORE_PASSWORD.toCharArray());
            }
            return keyStoreFile;
        }

        private static Properties setConnectionProp() {
            Properties prop = new Properties();
            prop.setProperty("user", Constants.RDS_DB_USER);
            prop.setProperty("password", Constants.RDS_DB_PASSWD);
            return prop;
        }

        private static Properties setSSLConnectionProp() {
            Properties prop = new Properties();
            prop.setProperty("verifyServerCertificate", "true");
            prop.setProperty("useSSL", "true");
            prop.setProperty("user", Constants.RDS_DB_USER);
            prop.setProperty("password", generateAuthToken());
            return prop;
        }

        private static String generateAuthToken() {
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(Constants.AWS_ACCESS_KEY, Constants.AWS_SECRET_KEY);
            RdsIamAuthTokenGenerator generator = RdsIamAuthTokenGenerator.builder()
                    .credentials(new AWSStaticCredentialsProvider(awsCredentials)).region(Constants.RDS_NORTHWEST_REGION).build();
            return generator
                    .getAuthToken(GetIamAuthTokenRequest.builder()
                            .hostname(Constants.RDS_LAB_INSTANCE_HOSTNAME)
                            .port(Constants.RDS_LAB_INSTANCE_PORT)
                            .userName(Constants.RDS_DB_USER).build());
        }
    }

    public static Connection getConnection() {
        return RDSClientHolder.connection;
    }

    public static int insert(String table, Contract contract) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(
                    "insert into " + Constants.RDS_TABLE_CONTRACTS +
                            "(contract_id, contract_num, s3_bucket, s3_key, create_time, update_time) " +
                            "values(?, ?, ?, ?, ?, ?)");
            ps.setString(1, Utils.generateUUID());
            ps.setString(2, contract.getContractNum());
            ps.setString(3, contract.getS3Bucket());
            ps.setString(4, contract.getS3Key());
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()), Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")));
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()), Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai")));
            return ps.executeUpdate();
        } finally {
            closePreparedStatement(ps);
//            closeConnection(conn);
        }
    }


    private static void closeResultSet(ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
    }

    private static void closeStatement(Statement stm) throws SQLException {
        if (stm != null) {
            stm.close();
        }
    }

    private static void closePreparedStatement(PreparedStatement pstm) throws SQLException {
        if (pstm != null) {
            pstm.close();
        }
    }

    private static void closeConnection(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }
}



