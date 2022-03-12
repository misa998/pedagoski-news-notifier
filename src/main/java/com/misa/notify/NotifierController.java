package com.misa.notify;

import com.misa.dao.KeywordsToAvoidDAOImpl;
import com.misa.dao.NewsDAOImpl;
import com.misa.entity.KeywordToAvoid;
import com.misa.entity.News;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@AllArgsConstructor
public class NotifierController {

    private final List<News> scrapedNews;
    private List<News> recentNewsList;

    public NotifierController(List<News> scrapedNews) {
        this.scrapedNews = scrapedNews;
    }

    public List<News> fetchRecentNews() {
        String recentNewsLog = "Looking for recent news: %s.";
        log.debug(String.format(recentNewsLog, "Started"));

        recentNewsList = new ArrayList<>();
        List<News> persistedNews = new NewsDAOImpl().getAll();
        for (News news : scrapedNews) {
            if (persistedNews.stream().noneMatch(
                    newsPers -> newsPers.getNewsId().equals(news.getNewsId()))) {

                recentNewsList.add(news);
            }
        }

        saveNewsToDatabase();

        log.info(String.format("Found %s recent news.", recentNewsList.size()));
        log.debug(String.format(recentNewsLog, "Finished"));
        return recentNewsList;
    }


    public List<News> getNewsOfInterest() {
        return getRecentNewsList().stream()
                                  .filter(news -> isNewsOfInterest(news.getTitleString()))
                                  .collect(Collectors.toList());
    }

    private List<News> getRecentNewsList() {
        if (recentNewsList == null) {
            fetchRecentNews();
        }
        return recentNewsList;
    }

    private List<BigDecimal> saveNewsToDatabase() {
        return new NewsDAOImpl().save(recentNewsList);
    }

    private boolean isNewsOfInterest(String title) {
        return getKeywordsToAvoid().stream()
                                   .noneMatch(k -> title.toLowerCase()
                                                        .contains(k.getWord().toLowerCase()));
    }

    private List<KeywordToAvoid> getKeywordsToAvoid() {
        return new KeywordsToAvoidDAOImpl().getAll();
    }
}
