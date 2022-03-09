package com.misa.db;

import com.misa.util.GetProperties;

import java.sql.Connection;
import java.sql.DriverManager;
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
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    getURL(),
                    GetProperties.get("db.username"),
                    GetProperties.get("db.password"));
            log.debug("Obtained the connection.");
            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            log.error(e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    public void closeConnection() {
        try {
            connection.close();
            log.debug("Closed the connection.");
        } catch (SQLException | NullPointerException e) {
            log.error(e.getMessage());
        }
    }

    public String getURL() {
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:mysql://");
        sb.append(GetProperties.get("db.ip"));
        sb.append(":");
        sb.append(GetProperties.get("db.port"));
        sb.append("/");
        sb.append(GetProperties.get("db.schema"));
        return sb.toString();
    }
}

