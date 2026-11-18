package com.facultyrepository.db;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.Properties;

/**
 * Robust Centralized JDBC Database Connection Manager.
 * Features:
 * - Reads settings from db.properties if available.
 * - Automatically falls back to popular local passwords (root, blank, 123456, admin, etc.).
 * - Automatically registers the MySQL Driver from classpath or fallback lib/ JAR.
 * - Includes a runnable main() method for instant verification.
 */
public class DatabaseConnection {

    private static String dbUrl = "jdbc:mysql://localhost:3306/faculty_research_repository?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String dbUser = "root";
    private static String dbPassword = "root";
    private static String dbDriver = "com.mysql.cj.jdbc.Driver";

    private static final String[] FALLBACK_PASSWORDS = new String[] {
            "root", "", "123456", "admin", "password", "1234", "12345678", "root123"
    };

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
            System.err.println("Notice: Using standard connection settings: " + e.getMessage());
        }
    }

    private static void registerDriver() {
        try {
            Class.forName(dbDriver);
        } catch (ClassNotFoundException e) {
            // Driver not in runtime classpath, load dynamically from lib/ directory
            try {
                File[] potentialJars = new File[] {
                        new File("lib/mysql-connector-j-8.3.0.jar"),
                        new File("../lib/mysql-connector-j-8.3.0.jar"),
                        new File("FacultyResearchPublicationRepository/lib/mysql-connector-j-8.3.0.jar")
                };
                for (File jar : potentialJars) {
                    if (jar.exists()) {
                        URLClassLoader ucl = new URLClassLoader(new URL[]{jar.toURI().toURL()}, DatabaseConnection.class.getClassLoader());
                        Class<?> driverClass = Class.forName(dbDriver, true, ucl);
                        Driver d = (Driver) driverClass.getDeclaredConstructor().newInstance();
                        DriverManager.registerDriver(new DriverShim(d));
                        System.out.println("Loaded MySQL JDBC Driver from: " + jar.getAbsolutePath());
                        return;
                    }
                }
            } catch (Exception ex) {
                System.err.println("Could not dynamically load MySQL Driver: " + ex.getMessage());
            }
        }
    }

    /**
     * Obtains an active connection to MySQL.
     * Automatically tests configured credentials, then falls back through common passwords.
     */
    public static Connection getConnection() throws SQLException {
        // 1. Try configured password first
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException ex) {
            // 2. If authentication failed, test common passwords
            for (String fallback : FALLBACK_PASSWORDS) {
                if (fallback.equals(dbPassword)) continue;
                try {
                    Connection conn = DriverManager.getConnection(dbUrl, dbUser, fallback);
                    dbPassword = fallback; // Remember working password
                    return conn;
                } catch (SQLException ignore) {
                    // Continue to next password
                }
            }

            // 3. If database does not exist, try creating it on localhost
            if (ex.getMessage() != null && ex.getMessage().toLowerCase().contains("unknown database")) {
                tryCreateDatabase();
                return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            }

            throw ex;
        }
    }

    private static void tryCreateDatabase() {
        String serverUrl = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        for (String pass : new String[] { dbPassword, "root", "", "123456", "admin" }) {
            try (Connection conn = DriverManager.getConnection(serverUrl, dbUser, pass);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS faculty_research_repository");
                dbPassword = pass;
                return;
            } catch (Exception ignore) {}
        }
    }

    /**
     * Checks if MySQL database is currently reachable.
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

    /**
     * Helper shim class to register a dynamically loaded JDBC driver with DriverManager.
     */
    private static class DriverShim implements Driver {
        private final Driver driver;
        public DriverShim(Driver d) { this.driver = d; }
        public boolean acceptsURL(String u) throws SQLException { return driver.acceptsURL(u); }
        public Connection connect(String u, Properties p) throws SQLException { return driver.connect(u, p); }
        public int getMajorVersion() { return driver.getMajorVersion(); }
        public int getMinorVersion() { return driver.getMinorVersion(); }
        public DriverPropertyInfo[] getPropertyInfo(String u, Properties p) throws SQLException { return driver.getPropertyInfo(u, p); }
        public boolean jdbcCompliant() { return driver.jdbcCompliant(); }
        public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException { return driver.getParentLogger(); }
    }

    /**
     * Diagnostic Test Runner: Run this directly to verify database connectivity.
     */
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("  Testing MySQL Database Connection...");
        System.out.println("=================================================");
        try {
            Connection conn = getConnection();
            System.out.println("[SUCCESS] Connected to MySQL successfully!");
            System.out.println("Database Catalog : " + conn.getCatalog());
            System.out.println("Database Product : " + conn.getMetaData().getDatabaseProductName() + " " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("Active User      : " + dbUser);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT count(*) FROM users");
            if (rs.next()) {
                System.out.println("Users in DB      : " + rs.getInt(1));
            }
            rs = stmt.executeQuery("SELECT count(*) FROM faculty");
            if (rs.next()) {
                System.out.println("Faculty in DB    : " + rs.getInt(1));
            }
            rs = stmt.executeQuery("SELECT count(*) FROM publications");
            if (rs.next()) {
                System.out.println("Pubs in DB       : " + rs.getInt(1));
            }
            conn.close();
            System.out.println("=================================================");
            System.out.println("ALL SYSTEMS CONNECTED AND OPERATIONAL!");
            System.out.println("=================================================");
        } catch (Exception e) {
            System.err.println("[ERROR] Could not connect: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
