package com.misa.scraper;

import com.misa.dao.exceptions.FailedToScraperTheUrlException;
import com.misa.entity.News;

import org.apache.commons.codec.binary.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@NoArgsConstructor
@AllArgsConstructor
@Getter(AccessLevel.PRIVATE)
@Setter(onParam_=@NonNull)
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
            throw new FailedToScraperTheUrlException(
                    String.format("Failed to read from url: %s | and class name: %s", url, className), e);
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
            news.setContent(ws.getNewsContent());

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

    private BigDecimal getIdFromLink(String link) {
        Matcher matcher = PATTERN.matcher(link);
        if (matcher.find())
            return new BigDecimal(matcher.group(0));
        else
            throw new IllegalArgumentException(String.format("Id not found in the link: %s .", link));

    }

    public String getNewsContent() {
        Element content = scrape();
        String modifiedContent = scrapeAttachments(content);
        modifiedContent = modifyTitle(modifiedContent);
        modifiedContent = appendFooter(modifiedContent);
        return modifiedContent;
    }

    private String appendFooter(String modifiedContent) {
        StringBuilder sb = new StringBuilder();
        sb.append(modifiedContent);
        sb.append("<br/><br/>");
        sb.append("This is automatically generated email with content scraped from the ");
        sb.append(getCurrentNewsLink());
        sb.append(" website. You are receiving this email because you are subscribed to the mailing list.");
        return sb.toString();
    }

    private String modifyTitle(String content) {
        return content.replaceFirst("class=\"vest_naslov leva2_3width\" href=\"#\"",
                String.format("href=\"%s\"", getCurrentNewsLink()));
    }

    private String scrapeAttachments(Element content) {
        String finalTag = content.html();
        for (Element e : content.getElementsByClass("fajlovi")) {
            String oldIconLink = e.child(0).attr("src");
            String srcToReplace = String.format("src=\"%s\"", oldIconLink);
            String srcToReplaceWith = String.format("src=\"%s\"", "http://www.pfvr.ni.ac.rs/"
                                                                          .concat(oldIconLink));
            finalTag = finalTag.replace(srcToReplace, srcToReplaceWith);

            String oldAttLink = e.child(1).attr("href");
            String hrefToReplace = String.format("href=\"%s\"", oldAttLink);
            String hrefToReplaceWith = String.format("href=\"%s\"", "http://www.pfvr.ni.ac.rs/"
                                                                            .concat(oldAttLink));
            finalTag = finalTag.replace(hrefToReplace, hrefToReplaceWith);
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
            ws.setCurrentNewsLink(currentNewsLink);
            news.setContent(ws.getNewsContent());

            newsList.add(news);
        }

        log.debug(String.format(scrapingLog, "Finished"));
        log.debug(String.format("News scraped: %s", newsList.size()));
        return newsList;
    }

}
