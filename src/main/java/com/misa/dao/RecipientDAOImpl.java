package com.misa.dao;

import com.misa.db.DataSource;
import com.misa.entity.News;
import com.misa.entity.Recipient;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Log4j
public class RecipientDAOImpl implements RecipientDAO {

    private final Connection connection;

    public RecipientDAOImpl() {
        this.connection = DataSource.getInstance().openConnection();
    }

    @Override
    public List<Recipient> getAll() {
        log.info("Select called for all the recipients");
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM recipient");

            List<Recipient> recipients = new ArrayList<>();
            while(resultSet.next())
                recipients.add(getRecipientFromResultSet(resultSet));

            return recipients;
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    private Recipient getRecipientFromResultSet(ResultSet resultSet) throws SQLException {
        return Recipient.builder()
                .id(resultSet.getInt("id"))
                .address(resultSet.getString("address"))
                .active(resultSet.getInt("active"))
                .build();
    }
}
