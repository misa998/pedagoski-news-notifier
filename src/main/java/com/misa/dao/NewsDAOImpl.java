package com.misa.dao;

import com.misa.db.DataSource;
import com.misa.entity.News;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import lombok.extern.log4j.Log4j;

@Log4j
public class NewsDAOImpl implements NewsDAO {
    private final Connection connection;

    public NewsDAOImpl() {
        this.connection = DataSource.getInstance().openConnection();
    }

    @Override
    public Optional<News> getById(int id) {
        log.debug("Starting select by id for news table.");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM news WHERE id = id");
            statement.setInt(1, id);
            log.info(statement.toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info(String.format("Selected: %s record.", 1));
                return Optional.ofNullable(getNewsFromResultSet(resultSet));
            }
        } catch (SQLException | ParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Optional.empty();
    }

    @Override
    public Optional<News> getByNewsId(int id) {
        log.debug("Starting select by news_id for news table.");
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM news WHERE news_id = ?");
            statement.setInt(1, id);
            log.info(statement.toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info(String.format("Selected: %s record.", 1));
                return Optional.ofNullable(getNewsFromResultSet(resultSet));
            }
        } catch (SQLException | ParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Optional.empty();
    }

    @Override
    public List<News> getAll() {
        log.debug("Starting select for all news table.");
        try {
            String query = "SELECT * FROM news";
            Statement statement = connection.createStatement();
            log.info(query);
            ResultSet resultSet = statement.executeQuery(query);

            List<News> news = new ArrayList<>();
            while (resultSet.next())
                news.add(getNewsFromResultSet(resultSet));

            log.info(String.format("Selected: %s records.", news.size()));
            return news;
        } catch (SQLException | ParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Collections.emptyList();
    }

    private News getNewsFromResultSet(ResultSet resultSet) throws SQLException, ParseException {
        return News.builder()
                   .id(resultSet.getInt("id"))
                   .newsId(resultSet.getInt("news_id"))
                   .title(resultSet.getBlob("title")
                                   .getBytes(1, (int) resultSet.getBlob("title").length()))
                   .link(resultSet.getString("link"))
                   .dateTime(LocalDateTime.parse(resultSet.getString("date_time")))
                   .content(resultSet.getString("content"))
                   .build();
    }

    @Override
    public int save(News news) {
        log.debug("Starting insert for news table.");
        PreparedStatement insert;
        try {
            insert = connection.prepareStatement(
                    "INSERT INTO news (news_id, title, link, date_time, content) VALUES (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            log.info(insert.toString());
            insert.setInt(1, news.getNewsId());
            insert.setBlob(2, new SerialBlob(news.getTitle()).getBinaryStream());
            insert.setString(3, news.getLink());
            insert.setString(4, news.getDateTime().toString());
            insert.setString(5, news.getContent());
            log.info(insert.toString());

            insert.executeUpdate();
            ResultSet resultSet = insert.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                log.info(String.format("Inserted: %s record.", 1));
                return resultSet.getInt(1);
            } else {
                throw new SQLException(String.format("Record not inserted for news_id = %s.", news.getNewsId()));
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
