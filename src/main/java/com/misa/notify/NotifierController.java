package com.misa.notify;

import com.misa.dao.NewsDAOImpl;
import com.misa.entity.News;
import lombok.extern.log4j.Log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class NotifierController {

    public List<News> getRecentNews(List<News> newsList) {
        List<News> recentNewsList = new ArrayList<>();
        List<News> persistedNews = new NewsDAOImpl().getAll();
        for (News news : newsList) {
            if (news.getNewsId() < 1)
                continue;

            if (persistedNews.stream()
                    .noneMatch(newsPers -> newsPers.getNewsId() == news.getNewsId())) {

                saveNewsToDataBase(news);
                recentNewsList.add(news);
                log.info("Recent news found.");
            }
        }
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
        log.info("Saved " + news.getTitleString() + ", on news_id = " + news.getNewsId());
    }

    private boolean isNewsOfInterest(String title) {
        for (String s : getKeywordsToAvoid()) {
            if (title.toLowerCase().contains(s)) {
                log.debug("Ignoring " + title);
                return false;
            }
        }

        log.info("News of interest found.");
        return true;
    }

    private String[] getKeywordsToAvoid() {
        return new String[]{"у прву годину", "бруцош", "упис 20", "изабран", "одмор", "одмори", "одлука", "одлуке", "пројекат",
                "докторске студије", "љиљана митић", "владимир момчиловић", "буба стојановић", "нела",
                "књижевности", "књижевност", "стилистике", "стилистика", "српског језика", "српски језик", "зоран момчиловић",
                "сунчица", "весна", "србољуб", "милена", "сања", "лидија", "биљана", "катарина"};
    }
}
