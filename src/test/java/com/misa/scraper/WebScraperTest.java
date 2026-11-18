package com.misa.scraper;

import com.misa.dao.exceptions.PropertyNotFoundByKeyException;
import com.misa.dao.exceptions.UrlDoesntMatchFormatException;
import com.misa.entity.News;
import com.misa.util.GetApplicationProperties;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class WebScraperTest {

    private static final String BULLETIN_BOARD_URL = "http://www.pfvr.ni.ac.rs/oglasna.php";
    private static final String BULLETIN_BOARD_CLASS = "leva1 kvadratic l2top";
    private static final String NEWS_URL = "http://www.pfvr.ni.ac.rs/vesti.php?oblast=opste";
    private static final String NEWS_CLASS = "big_providno";

    @Test
    void scrape() {
    }

    @Test
    void getFromBulletinBoard() {
        WebScraper webScraper = new WebScraper();
        webScraper.setUrl(BULLETIN_BOARD_URL);
        webScraper.setClassName(BULLETIN_BOARD_CLASS);

        List<News> scrapedNewsList = webScraper.getFromBulletinBoard();
        assertNotNull(scrapedNewsList);
        assertEquals(4, scrapedNewsList.size());
        assertTrue(scrapedNewsList.stream().noneMatch(e -> e.getNewsId() == null));
        assertTrue(scrapedNewsList.stream().noneMatch(e -> e.getId() != null));
    }

    @Test
    void getNewsContent() {
    }

    @Test
    void getNews() {
        WebScraper webScraper = new WebScraper();
        webScraper.setUrl(NEWS_URL);
        webScraper.setClassName(NEWS_CLASS);

        List<News> scrapedNewsList = webScraper.getNews();
        assertNotNull(scrapedNewsList);
        assertEquals(4, scrapedNewsList.size());
        assertTrue(scrapedNewsList.stream().noneMatch(e -> e.getNewsId() == null));
        assertTrue(scrapedNewsList.stream().noneMatch(e -> e.getId() != null));
    }

    @Test
    void getIdFromLink(){
        WebScraper webScraper = new WebScraper();

        String link = "http://www.pfvr.ni.ac.rs/vest.php?id=4326&oblast=obavestenja";

        BigDecimal excepted = new BigDecimal("4326");
        BigDecimal actual = webScraper.getIdFromLink(link);

        assertEquals(excepted, actual);
    }

    @Test
    void getIdFromLink2(){
        WebScraper webScraper = new WebScraper();

        String link = "http://www.pfvr.ni.ac.rs/vest.php?id=998964531846&oblast=obavestenja";

        BigDecimal excepted = new BigDecimal("998964531846");
        BigDecimal actual = webScraper.getIdFromLink(link);

        assertEquals(excepted, actual);
    }

    @Test
    void getIdFromLinkUrlIsWrong(){
        String link = "http://www.pfvr.ni.ac.rs/vest.php?id=oblast=obavestenja";
        WebScraper webScraper = new WebScraper();
        Exception exception = assertThrows(UrlDoesntMatchFormatException.class, () -> {
            webScraper.getIdFromLink(link);
        });

        String expectedMsg = "URL doesn't match the excepted format";
        String actualMsg = exception.getMessage();
        assertTrue(actualMsg.contains(expectedMsg));
    }

    @Test
    void getIdFromLinkNoId(){
        String link = "http://www.pfvr.ni.ac.rs/vest.php?id=&oblast=obavestenja";
        WebScraper webScraper = new WebScraper();
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            webScraper.getIdFromLink(link);
        });

        String expectedMsg = "Id not found in the link";
        String actualMsg = exception.getMessage();
        assertTrue(actualMsg.contains(expectedMsg));
    }
}