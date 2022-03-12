package com.misa.dao;

import com.misa.dao.exceptions.DaoException;
import com.misa.db.DataSource;
import com.misa.entity.Recipient;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@NoArgsConstructor
public class RecipientDAOImpl implements RecipientDAO {

    private Connection connection;

    @Override
    public List<Recipient> getAll() {
        log.debug("Starting select for all recipient table.");
        String query = "SELECT * FROM recipient";
        log.info(query);
        this.connection = DataSource.getInstance().openConnection();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            List<Recipient> recipients = new ArrayList<>();
            while (resultSet.next())
                recipients.add(getRecipientFromResultSet(resultSet));

            log.info(String.format("Selected: %s records.", recipients.size()));
            return recipients;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    private Recipient getRecipientFromResultSet(ResultSet resultSet) throws SQLException {
        return Recipient.builder()
                        .id(resultSet.getBigDecimal("id"))
                        .address(resultSet.getString("address"))
                        .active(resultSet.getInt("active"))
                        .build();
    }
}
