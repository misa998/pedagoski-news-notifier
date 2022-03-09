package com.misa.dao;

import com.misa.db.DataSource;
import com.misa.entity.Recipient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class RecipientDAOImpl implements RecipientDAO {

    private final Connection connection;

    public RecipientDAOImpl() {
        this.connection = DataSource.getInstance().openConnection();
    }

    @Override
    public List<Recipient> getAll() {
        log.debug("Starting select for all recipient table.");
        try {
            String query = "SELECT * FROM recipient";
            Statement statement = connection.createStatement();
            log.info(query);
            ResultSet resultSet = statement.executeQuery(query);

            List<Recipient> recipients = new ArrayList<>();
            while (resultSet.next())
                recipients.add(getRecipientFromResultSet(resultSet));

            log.info(String.format("Selected: %s records.", recipients.size()));
            return recipients;
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Collections.emptyList();
    }

    private Recipient getRecipientFromResultSet(ResultSet resultSet) throws SQLException {
        return Recipient.builder()
                        .id(resultSet.getInt("id"))
                        .address(resultSet.getString("address"))
                        .active(resultSet.getInt("active"))
                        .build();
    }
}
