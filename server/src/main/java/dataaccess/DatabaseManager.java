package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
        try {
            createDatabase();
            createTables();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to initialize database", ex);
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        String urlWithoutDb = connectionUrl.substring(0, connectionUrl.lastIndexOf("/"));
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(urlWithoutDb, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    public static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            Connection conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("resources/db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, databaseName);
    }

    /** making tables**/

    static public void createTables() throws DataAccessException {
        // SQL statements for creating your tables (User, Game, AuthToken)
        String[] createStatements = {
                """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(256) NOT NULL PRIMARY KEY,
                password VARCHAR(256) NOT NULL,
                email VARCHAR(256) NOT NULL
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS authToken (
                authToken VARCHAR(256) NOT NULL PRIMARY KEY,
                username VARCHAR(256) NOT NULL
            )
            """,
                """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                gameName VARCHAR(256) NOT NULL,
                whiteUsername VARCHAR(256) DEFAULT NULL,
                blackUsername VARCHAR(256) DEFAULT NULL,
                -- The game state is serialized to a JSON string
                chessGame LONGTEXT NOT NULL
            )
            """
        };

        try (var conn = getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Failed to create tables", ex);
        }
    }

}
