package com.misa.scraper;

import com.misa.entity.News;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
import org.apache.commons.codec.binary.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Log4j
public class WebScraper {
    private String url;
    private String className;

    public Element scrape() {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.getElementsByClass(className).first();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<News> getFromBulletinBoard() {
        log.info("Getting news from the bulletin board.");
        List<News> newsList = new ArrayList<>();
        Element column = scrape();
        int i = 0;
        for (Element anchorLink2 : column.getAllElements()) {
            if (i == 4)
                break;
            Element firstArticle = column.child(1).child(i++);
            Element anchorLink = firstArticle.child(0);
            News news = buildNewsFromElement(anchorLink);
            newsList.add(news);
        }

        return newsList;
    }

    private News buildNewsFromElement(Element anchorLink) {
        News news = new News();
        String title = anchorLink.attr("title");
        byte[] bytes = StringUtils.getBytesUtf8(title);
        news.setTitle(bytes);
        news.setLink("http://www.pfvr.ni.ac.rs/".concat(anchorLink.attr("href")));
        news.setNewsId(getIdFromLink(news.getLink()));
        return news;
    }

    private int getIdFromLink(String link) {
        Pattern pattern = Pattern.compile("(?<=\\=)(.*?)(?=\\&)");
        Matcher matcher = pattern.matcher(link);
        if (matcher.find())
            return Integer.parseInt(matcher.group(0));

        return 0;
    }

    public List<News> getNews() {
        log.info("Getting news from the main news page.");
        List<News> newsList = new ArrayList<>();
        Element column = scrape();

        int i = 1;
        for (Element anchorLink : column.getAllElements()) {
            if (i == 5)
                break;
            anchorLink = column.child(i++).child(0);

            News news = buildNewsFromElement(anchorLink);
            newsList.add(news);
        }

        return newsList;
    }

}
