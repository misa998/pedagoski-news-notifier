package com.misa.db;

import com.misa.dao.exceptions.DaoException;
import com.misa.util.GetApplicationProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@NoArgsConstructor
public class DataSource {

    private static final DataSource instance = new DataSource();
    private Connection connection;

    public static DataSource getInstance() {
        return instance;
    }

    public Connection openConnection() {
        javax.sql.DataSource dataSource = createDataSource();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = dataSource.getConnection();
            log.debug("Connection obtained.");
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            throw new DaoException(e);
        }
    }

    private javax.sql.DataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getURL());
        config.setUsername(GetApplicationProperties.get("db.username"));
        config.setPassword(GetApplicationProperties.get("db.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.addDataSourceProperty("max_user_connections", "15");

        return new HikariDataSource(config);
    }

    public void closeConnection() {
        try {
            connection.close();
            log.debug("Connection closed.");
        } catch (SQLException | NullPointerException e) {
            throw new DaoException(e);
        }
    }

    private String getURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://");
        sb.append(GetApplicationProperties.get("db.ip"));
        sb.append(":");
        sb.append(GetApplicationProperties.get("db.port"));
        sb.append("/");
        sb.append(GetApplicationProperties.get("db.schema"));
        return sb.toString();
    }
}

