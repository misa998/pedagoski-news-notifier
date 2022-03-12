package com.misa.dao;

import com.misa.dao.exceptions.DaoException;
import com.misa.db.DataSource;
import com.misa.entity.News;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;

import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@NoArgsConstructor
public class NewsDAOImpl implements NewsDAO {
    private Connection connection;

    @Override
    public Optional<News> getById(BigDecimal id) {
        log.debug("Starting select by id for news table.");
        String queryString = "SELECT * FROM news WHERE id = id";
        this.connection = DataSource.getInstance().openConnection();

        try (PreparedStatement statement = connection.prepareStatement(queryString)) {
            statement.setBigDecimal(1, id);
            log.info(statement.toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info(String.format("Selected: %s record.", 1));
                return Optional.ofNullable(getNewsFromResultSet(resultSet));
            }
        } catch (SQLException | ParseException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Optional.empty();
    }

    @Override
    public Optional<News> getByNewsId(BigDecimal id) {
        log.debug("Starting select by news_id for news table.");
        String queryString = "SELECT * FROM news WHERE news_id = ?";
        log.info(queryString);
        this.connection = DataSource.getInstance().openConnection();

        try (PreparedStatement statement = connection.prepareStatement(queryString)) {
            statement.setBigDecimal(1, id);
            log.info(statement.toString());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                log.info(String.format("Selected: %s record.", 1));
                return Optional.ofNullable(getNewsFromResultSet(resultSet));
            }
        } catch (SQLException | ParseException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Optional.empty();
    }

    @Override
    public List<News> getAll() {
        log.debug("Starting select for all news table.");
        String query = "SELECT * FROM news";
        log.info(query);
        this.connection = DataSource.getInstance().openConnection();

        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            List<News> news = new ArrayList<>();
            while (resultSet.next())
                news.add(getNewsFromResultSet(resultSet));

            log.info(String.format("Selected: %s records.", news.size()));
            return news;
        } catch (SQLException | ParseException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    private News getNewsFromResultSet(ResultSet resultSet) throws SQLException, ParseException {
        return News.builder()
                   .id(resultSet.getBigDecimal("id"))
                   .newsId(resultSet.getBigDecimal("news_id"))
                   .title(resultSet.getBlob("title")
                                   .getBytes(1, (int) resultSet.getBlob("title").length()))
                   .link(resultSet.getString("link"))
                   .dateTime(resultSet.getTimestamp("date_time"))
                   .content(resultSet.getString("content"))
                   .build();
    }

    @Override
    public BigDecimal save(News news) {
        log.debug("Starting insert for news table.");
        String queryString = "INSERT INTO news (news_id, title, link, date_time, content) VALUES (?, ?, ?, ?, ?)";
        log.info(queryString);
        this.connection = DataSource.getInstance().openConnection();

        try (PreparedStatement insert = connection.prepareStatement(queryString,
                Statement.RETURN_GENERATED_KEYS)) {
            insert.setBigDecimal(1, news.getNewsId());
            insert.setBlob(2, new SerialBlob(news.getTitle()).getBinaryStream());
            insert.setString(3, news.getLink());
            insert.setTimestamp(4, news.getDateTime());
            insert.setString(5, news.getContent());
            log.info(insert.toString());

            insert.executeUpdate();
            ResultSet resultSet = insert.getGeneratedKeys();
            if (resultSet != null && resultSet.next()) {
                log.info(String.format("Inserted: %s record.", 1));
                return resultSet.getBigDecimal(1);
            } else {
                throw new SQLException(String.format("Record not inserted for news_id = %s.", news.getNewsId()));
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    @Override
    public List<BigDecimal> save(List<News> newsList) {
        String compiledQuery = "INSERT INTO news (news_id, title, link, date_time, content) VALUES (?, ?, ?, ?, ?)";
        this.connection = DataSource.getInstance().openConnection();

        try (PreparedStatement insert = connection.prepareStatement(compiledQuery)) {
            connection.setAutoCommit(true);

            for (News news : newsList) {
                insert.setBigDecimal(1, news.getNewsId());
                insert.setBlob(2, new SerialBlob(news.getTitle()).getBinaryStream());
                insert.setString(3, news.getLink());
                insert.setTimestamp(4, news.getDateTime());
                insert.setString(5, news.getContent());

                insert.addBatch();
            }

            int[] inserted = insert.executeBatch();

            insert.close();

            return Arrays.stream(inserted).mapToObj(BigDecimal::new).collect(Collectors.toList());
        } catch (SQLException ex) {
            throw new DaoException(ex);
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    @Override
    public List<News> getByIds(List<BigDecimal> newsIds) {
        log.debug("Starting select for news ids.");
        String query = String.format("SELECT * FROM news WHERE id IN (%s)",
                createCSVFromIdArray(newsIds));
        log.info(query);

        this.connection = DataSource.getInstance().openConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < newsIds.size(); i++) {
                statement.setBigDecimal(i, newsIds.get(i));
            }

            ResultSet resultSet = statement.executeQuery(query);

            List<News> news = new ArrayList<>();
            while (resultSet.next())
                news.add(getNewsFromResultSet(resultSet));

            log.info(String.format("Selected: %s records.", news.size()));
            return news;
        } catch (SQLException | ParseException e) {
            throw new DaoException(e);
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    private String createCSVFromIdArray(List<BigDecimal> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
    }

}
