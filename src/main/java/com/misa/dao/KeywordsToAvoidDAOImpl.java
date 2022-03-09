package com.misa.dao;

import com.misa.db.DataSource;
import com.misa.entity.KeywordToAvoid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class KeywordsToAvoidDAOImpl implements KeywordsToAvoidDAO {

    private final Connection connection;

    public KeywordsToAvoidDAOImpl() {
        this.connection = DataSource.getInstance().openConnection();
    }

    @Override
    public List<KeywordToAvoid> getAll() {
        log.debug("Starting select for all keywords_to_avoid table.");
        try {
            String query = "SELECT * FROM keywords_to_avoid";
            Statement statement = connection.createStatement();
            log.info(query);
            ResultSet resultSet = statement.executeQuery(query);

            List<KeywordToAvoid> keywords = new ArrayList<>();
            while (resultSet.next())
                keywords.add(getKeywordFromResultSet(resultSet));

            log.info(String.format("Selected: %s records.", keywords.size()));
            return keywords;
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Collections.emptyList();
    }

    private KeywordToAvoid getKeywordFromResultSet(ResultSet resultSet) throws SQLException {
        return KeywordToAvoid.builder()
                             .id(resultSet.getInt("id"))
                             .word(resultSet.getString("word"))
                             .build();
    }

    @Override
    public int save(String word) {
        log.debug("Starting insert for keywords_to_avoid table.");
        PreparedStatement insert;
        try {
            insert = connection.prepareStatement(
                    "INSERT INTO keywords_to_avoid (word) VALUES (?)", Statement.RETURN_GENERATED_KEYS);

            insert.setString(1, word);
            log.info(insert.toString());

            insert.executeUpdate();
            ResultSet resultSet = insert.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                log.info(String.format("Inserted: %s record.", 1));
                return resultSet.getInt(1);
            } else {
                throw new SQLException(String.format("Record not inserted for word = %s.", word));
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }
}
