package lambda.rds.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

import java.util.Properties;

/**
 * @Description:
 * @author: jiasfeng
 * @Date: 8/15/2018
 */
public class DBPoolConnection {

    private DBPoolConnection() {
    }

    private static class DBPoolConnectionHolder {
        private static DruidPooledConnection connection = getDruidDataSource();

        private static DruidPooledConnection getDruidDataSource() {
            try {
                DruidDataSource druidDataSource =
                        (DruidDataSource) DruidDataSourceFactory.createDataSource(setProperties());
                return druidDataSource.getConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static DruidPooledConnection getConnection() {
        return DBPoolConnectionHolder.connection;
    }

    private static Properties setProperties() {
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://lab.cxbwswfsjviz.rds.cn-northwest-1.amazonaws.com.cn:3306/labdb?useUnicode=true&characterEncoding=utf8&autoReconnect=true");
        properties.setProperty("username", "admin");
        properties.setProperty("password", "abcd1234");
        properties.setProperty("filters", "stat");
        properties.setProperty("initialSize", "2");
        properties.setProperty("maxActive", "300");
        properties.setProperty("maxWait", "60000");
        properties.setProperty("timeBetweenEvictionRunsMillis", "60000");
        properties.setProperty("minEvictableIdleTimeMillis", "300000");
        properties.setProperty("validationQuery", "SELECT 1");
        properties.setProperty("testWhileIdle", "true");
        properties.setProperty("testOnBorrow", "false");
        properties.setProperty("testOnReturn", "false");
        properties.setProperty("poolPreparedStatements", "false");
        properties.setProperty("maxPoolPreparedStatementPerConnectionSize", "200");
        return properties;
    }
}
