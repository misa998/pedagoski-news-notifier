package com.misa.db;

import com.misa.util.GetProperties;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Log4j
public class DataSource {
    private static DataSource instance = new DataSource();
    private DataSource(){}
    public static DataSource getInstance(){
        return instance;
    }

    private Connection connection;

    public Connection openConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(
                    getURL(),
                    GetProperties.get("db.username"),
                    GetProperties.get("db.password"));
            return connection;
        } catch (SQLException | ClassNotFoundException e){
            log.error(e.getMessage());
            System.exit(-1);
            return null;
        }
    }

    public void closeConnection(){
        try{
            connection.close();
        } catch (SQLException | NullPointerException e){
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

