package server.db;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;


/**
 * Manages the connection to the PostgreSQL database.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:postgresql://pg:5432/studs";
    private static final String USER = "s408078";
    private static final String PASS = "qUD2797Z8A7vE3ch";
    private final HikariDataSource dataSource;

    public DatabaseManager() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(USER);
        config.setPassword(PASS);

        // --- Pool Configuration ---
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(20); // Max number of connections
        config.setConnectionTimeout(30000); // 30 seconds to get a connection
        config.setIdleTimeout(600000); // 10 minutes for an idle connection to be retired
        config.setMaxLifetime(1800000); // 30 minutes max lifetime

        // --- Optional: Performance Settings ---
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        this.dataSource = new HikariDataSource(config);
    }

    /**
     * Borrows a connection from the pool.
     * @return A database Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes the entire connection pool. Should be called on server shutdown.
     */
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

}