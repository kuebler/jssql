package org.akuebler.rhino.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class PostgresConnectionFactory {
    Properties properties;

    public Properties getProperties() {
        try {
            if (properties == null) {
                properties = new Properties();
                properties.load(PostgresConnectionFactory.class.getResourceAsStream("/db.properties"));
            }
            return properties;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getDriverName() {
        return getProperties().getProperty("jdbc.driver");
    }

    public String getJdbcUrl() {
        return getProperties().getProperty("jdbc.url");
    }

    public Connection getConnection() {
        try {
            Class.forName(getDriverName());
            return DriverManager.getConnection(getJdbcUrl());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection createConnection() {
        return new PostgresConnectionFactory().getConnection();
    }
}
