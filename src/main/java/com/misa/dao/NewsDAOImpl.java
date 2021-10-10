package com.misa.dao;

import com.misa.db.DataSource;
import com.misa.entity.News;
import lombok.extern.log4j.Log4j;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j
public class NewsDAOImpl implements NewsDAO{
    private final Connection connection;

    public NewsDAOImpl() {
        this.connection = DataSource.getInstance().openConnection();
    }

    @Override
    public Optional<News> getById(int id) {
        log.info("Select called for id " + id);
        try {
            PreparedStatement getById = connection.prepareStatement(
                    "SELECT * FROM news WHERE id = id");
            getById.setInt(1, id);
            ResultSet resultSet = getById.executeQuery();

            if(resultSet.next())
                return Optional.ofNullable(getNewsFromResultSet(resultSet));
        } catch (SQLException | ParseException e){
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Optional.empty();
    }

    @Override
    public Optional<News> getByNewsId(int id) {
        log.info("Select called for news_id " + id);
        try {
            PreparedStatement getById = connection.prepareStatement(
                    "SELECT * FROM news WHERE news_id = ?");
            getById.setInt(1, id);
            ResultSet resultSet = getById.executeQuery();

            if(resultSet.next())
                return Optional.ofNullable(getNewsFromResultSet(resultSet));
        } catch (SQLException | ParseException e){
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return Optional.empty();
    }

    @Override
    public News getLatest() {
        log.info("Select called for latest news");
        try {
            PreparedStatement getCourseById = connection.prepareStatement(
                    "SELECT * FROM web_scraper.news ORDER BY news_id DESC LIMIT 1");
            ResultSet resultSet = getCourseById.executeQuery();
            if(resultSet.next())
                return getNewsFromResultSet(resultSet);
        } catch (SQLException | ParseException e){
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            DataSource.getInstance().closeConnection();
        }
        return null;
    }

    @Override
    public List<News> getAll() {
        log.info("Select called for all the news");
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM news");

            List<News> news = new ArrayList<>();
            while(resultSet.next())
                news.add(getNewsFromResultSet(resultSet));

            return news;
        } catch (SQLException | ParseException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            DataSource.getInstance().closeConnection();
        }
    }

    private News getNewsFromResultSet(ResultSet resultSet) throws SQLException, ParseException {
        return News.builder()
                .id(resultSet.getInt("id"))
                .newsId(resultSet.getInt("news_id"))
                .title(resultSet.getBlob("title").getBytes((long) 1, (int) resultSet.getBlob("title").length()))
                .link(resultSet.getString("link"))
                .dateTime(LocalDateTime.parse(resultSet.getString("date_time")))
                .build();
    }

    @Override
    public int save(News news) {
        log.info("Saving " + news.getNewsId());
        PreparedStatement insert;
        try {
            insert = connection.prepareStatement(
                    "INSERT INTO news (news_id, title, link, date_time) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            insert.setInt(1, news.getNewsId());
            insert.setBlob(2, new SerialBlob(news.getTitle()).getBinaryStream());
            insert.setString(3, news.getLink());
            insert.setString(4, news.getDateTime().toString());

            insert.executeUpdate();
            ResultSet resultSet = insert.getGeneratedKeys();
            if(resultSet != null && resultSet.next()){
                log.info(resultSet.getInt(1));
                return resultSet.getInt(1);
            } else {
                throw new SQLException("Item not inserted.");
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return 0;
        }finally {
            DataSource.getInstance().closeConnection();
        }
    }
}
