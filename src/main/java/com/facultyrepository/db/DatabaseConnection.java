package com.facultyrepository.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class providing centralized JDBC MySQL database connections.
 * Follows singleton connection factory pattern.
 */
public class DatabaseConnection {

    private static String dbUrl = "jdbc:mysql://localhost:3306/faculty_research_repository?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPassword = "root";
    private static String dbDriver = "com.mysql.cj.jdbc.Driver";

    static {
        loadProperties();
        registerDriver();
    }

    private static void loadProperties() {
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                if (props.containsKey("db.url")) dbUrl = props.getProperty("db.url").trim();
                if (props.containsKey("db.user")) dbUser = props.getProperty("db.user").trim();
                if (props.containsKey("db.password")) dbPassword = props.getProperty("db.password").trim();
                if (props.containsKey("db.driver")) dbDriver = props.getProperty("db.driver").trim();
            }
        } catch (Exception e) {
            System.err.println("Could not load db.properties, using fallback defaults: " + e.getMessage());
        }
    }

    private static void registerDriver() {
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Ensure mysql-connector-j is in classpath: " + e.getMessage());
        }
    }

    /**
     * Obtains a new database connection.
     * Tries configured password first, then attempts empty password if user is 'root' as a fallback.
     *
     * @return active java.sql.Connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException ex) {
            // Fallback attempt: if configured password failed and was "root", try empty password ""
            if ("root".equals(dbUser) && "root".equals(dbPassword)) {
                try {
                    Connection conn = DriverManager.getConnection(dbUrl, dbUser, "");
                    dbPassword = ""; // Remember working password
                    return conn;
                } catch (SQLException ignore) {
                    // throw original exception below
                }
            }
            throw ex;
        }
    }

    /**
     * Checks if the database is reachable.
     *
     * @return true if connection succeeds, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }

    public static String getDbUser() {
        return dbUser;
    }

    public static void setCredentials(String url, String user, String password) {
        dbUrl = url;
        dbUser = user;
        dbPassword = password;
    }
}
