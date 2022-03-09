package com.misa.notify;

import com.misa.dao.KeywordsToAvoidDAOImpl;
import com.misa.dao.NewsDAOImpl;
import com.misa.entity.KeywordToAvoid;
import com.misa.entity.News;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j;

@Log4j
public class NotifierController {

    public List<News> getRecentNews(List<News> newsList) {
        String recentNewsLog = "Looking for recent news: %s.";
        log.debug(String.format(recentNewsLog, "Started"));

        List<News> recentNewsList = new ArrayList<>();
        List<News> persistedNews = new NewsDAOImpl().getAll();
        for (News news : newsList) {
            if (news.getNewsId() < 1)
                continue;

            if (persistedNews.stream()
                             .noneMatch(newsPers -> newsPers.getNewsId() == news.getNewsId())) {

                saveNewsToDataBase(news);
                recentNewsList.add(news);
            }
        }

        log.info(String.format("Found %s recent news.", recentNewsList.size()));
        log.debug(String.format(recentNewsLog, "Finished"));
        return recentNewsList;
    }


    public List<News> getNewsOfInterest(List<News> newsList) {
        return getRecentNews(newsList).stream()
                                      .filter(news -> isNewsOfInterest(news.getTitleString()))
                                      .collect(Collectors.toList());
    }

    private void saveNewsToDataBase(News news) {
        int id = new NewsDAOImpl().save(news);
        news.setId(id);
    }

    private boolean isNewsOfInterest(String title) {
        return getKeywordsToAvoid().stream()
                                   .noneMatch(k -> title.toLowerCase().contains(k.getWord()));
    }

    private List<KeywordToAvoid> getKeywordsToAvoid() {
        return new KeywordsToAvoidDAOImpl().getAll();
    }
}
