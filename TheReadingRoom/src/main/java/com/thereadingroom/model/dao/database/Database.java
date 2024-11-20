package com.thereadingroom.model.dao.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Singleton class for managing the database connection pool using HikariCP.
 * Provides methods for acquiring database connections and managing the connection pool efficiently.
 */
public class Database {

    // The URL for the SQLite database
    private static final String DB_URL = "jdbc:sqlite:readingroom.db";

    // HikariCP DataSource for managing database connections
    private static HikariDataSource dataSource;

    // Singleton instance of the Database class
    private static final Database instance = new Database();

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the connection pool during class instantiation.
     */
    private Database() {
        initializeConnectionPool();  // Set up connection pool on creation of the singleton instance
    }

    /**
     * Returns the singleton instance of the Database class.
     * Ensures that only one instance of this class is created and shared globally.
     *
     * @return the singleton instance of Database
     */
    public static Database getInstance() {
        return instance;
    }

    /**
     * Initializes the HikariCP connection pool with the configured settings.
     * The method ensures that the pool is only created once during the application lifecycle.
     */
    private static void initializeConnectionPool() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig(); // HikariCP configuration settings
            config.setJdbcUrl(DB_URL);               // Set JDBC URL for the database
            config.setMaximumPoolSize(10);           // Max number of connections in the pool
            config.setConnectionTimeout(30000);      // Max wait time for a connection (30 seconds)
            config.setIdleTimeout(600000);           // Max idle time for a connection (10 minutes)
            config.setMaxLifetime(1800000);          // Max lifetime for a connection (30 minutes)
            dataSource = new HikariDataSource(config);  // Initialize the connection pool with the config
        }
    }

    /**
     * Provides a database connection from the HikariCP connection pool.
     * Connections should be closed after use to return them to the pool.
     *
     * @return a Connection object from the pool
     * @throws SQLException if unable to acquire a connection
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();  // Retrieve a connection from the HikariCP pool
    }
}
