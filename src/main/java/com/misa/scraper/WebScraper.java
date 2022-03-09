package com.misa.scraper;

import com.misa.entity.News;

import org.apache.commons.codec.binary.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Log4j
public class WebScraper {
    public static final Pattern PATTERN = Pattern.compile("(?<=\\=)(.*?)(?=\\&)");
    private String url;
    private String className;

    private String currentNewsLink;

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
        String scrapingLog = "Scraping news form the the bulletin board: %s.";
        log.debug(String.format(scrapingLog, "Started"));

        List<News> newsList = new ArrayList<>();
        Element column = scrape();
        int i = 0;
        for (Element anchorLink2 : column.getAllElements()) {
            if (i == 4)
                break;
            Element firstArticle = column.child(1).child(i++);
            Element anchorLink = firstArticle.child(0);
            News news = buildNewsFromElement(anchorLink);

            WebScraper ws = new WebScraper();
            ws.setUrl(news.getLink());
            ws.setClassName("wrap_content leva3_3width");
            ws.setCurrentNewsLink(currentNewsLink);
            String content = ws.getNewsContent();
            news.setContent(content);


            newsList.add(news);
        }

        log.debug(String.format(scrapingLog, "Finished"));
        log.debug(String.format("News scraped: %s", newsList.size()));
        return newsList;
    }

    private News buildNewsFromElement(Element anchorLink) {
        News news = new News();
        String title = anchorLink.attr("title");
        byte[] bytes = StringUtils.getBytesUtf8(title);
        news.setTitle(bytes);
        news.setLink("http://www.pfvr.ni.ac.rs/".concat(anchorLink.attr("href")));
        news.setNewsId(getIdFromLink(news.getLink()));
        news.setContent(anchorLink.html());

        currentNewsLink = news.getLink();

        return news;
    }

    private int getIdFromLink(String link) {
        Matcher matcher = PATTERN.matcher(link);
        if (matcher.find())
            return Integer.parseInt(matcher.group(0));

        return 0;
    }

    public String getNewsContent() {
        Element content = scrape();
        String modifiedContent = scrapeAttachments(content);
        modifiedContent = modifyTitle(modifiedContent);
        return modifiedContent;
    }

    private String modifyTitle(String content) {
        /*Element titleElement = element.getElementsByClass("vest_naslov leva2_3width").first();
        String oldTitleHref = titleElement.attr("href");*/
        return content.replace("class=\"vest_naslov leva2_3width\" href=\"#\"", String.format("class=\"vest_naslov leva2_3width\" href=\"%s\"", getCurrentNewsLink()));
    }

    private String scrapeAttachments(Element content) {
        String finalTag = content.html();
        for (Element e : content.getElementsByClass("fajlovi")) {
            String oldIconLink = e.child(0).attr("src");
            finalTag = finalTag.replace(String.format("src=\"%s\"", oldIconLink), String.format("src=\"%s\"", "http://www.pfvr.ni.ac.rs/"
                                                                                                                      .concat(oldIconLink)));

            String oldAttLink = e.child(1).attr("href");
            finalTag = finalTag.replace(String.format("href=\"%s\"", oldAttLink), String.format("href=\"%s\"", "http://www.pfvr.ni.ac.rs/"
                                                                                                                       .concat(oldAttLink)));
        }

        return finalTag;
    }

    public List<News> getNews() {
        String scrapingLog = "Scraping news form the main news page: %s.";
        log.debug(String.format(scrapingLog, "Started"));

        List<News> newsList = new ArrayList<>();
        Element column = scrape();

        int i = 1;
        for (Element anchorLink : column.getAllElements()) {
            if (i == 5)
                break;
            anchorLink = column.child(i++).child(0);

            News news = buildNewsFromElement(anchorLink);

            WebScraper ws = new WebScraper();
            ws.setUrl(news.getLink());
            ws.setClassName("wrap_content leva3_3width");
            news.setContent(ws.getNewsContent());

            newsList.add(news);
        }

        log.debug(String.format(scrapingLog, "Finished"));
        log.debug(String.format("News scraped: %s", newsList.size()));
        return newsList;
    }

}
