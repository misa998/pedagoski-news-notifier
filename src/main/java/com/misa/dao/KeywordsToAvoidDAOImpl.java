package com.misa.dao;

import com.misa.dao.exceptions.DaoException;
import com.misa.db.DataSource;
import com.misa.entity.KeywordToAvoid;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
@NoArgsConstructor
public class KeywordsToAvoidDAOImpl implements KeywordsToAvoidDAO {

    private Connection connection;

    @Override
    public List<KeywordToAvoid> getAll() {
        log.debug("Starting select for all keywords_to_avoid table.");
        String query = "SELECT * FROM keywords_to_avoid";
        log.info(query);
        this.connection = DataSource.getInstance().openConnection();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            List<KeywordToAvoid> keywords = new ArrayList<>();
            while (resultSet.next())
                keywords.add(getKeywordFromResultSet(resultSet));

            log.info(String.format("Selected: %s records.", keywords.size()));
            return keywords;
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    private KeywordToAvoid getKeywordFromResultSet(ResultSet resultSet) throws SQLException {
        return KeywordToAvoid.builder()
                             .id(resultSet.getBigDecimal("id"))
                             .word(resultSet.getString("word"))
                             .build();
    }

    @Override
    public BigDecimal save(@NonNull String word) {
        log.debug("Starting insert for keywords_to_avoid table.");
        String queryString = "INSERT INTO keywords_to_avoid (word) VALUES (?)";
        log.info(queryString);
        this.connection = DataSource.getInstance().openConnection();

        try (PreparedStatement insert = connection
                                                .prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, word);
            log.info(insert.toString());

            insert.executeUpdate();
            ResultSet resultSet = insert.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                log.info(String.format("Inserted: %s record.", 1));
                return resultSet.getBigDecimal(1);
            } else {
                throw new SQLException(String.format("Record not inserted for word = %s.", word));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }
}
