package com.misa;

import com.misa.dao.KeywordsToAvoidDAOImpl;
import com.misa.entity.News;
import com.misa.notify.MailNotifier;
import com.misa.scraper.WebScraper;

import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class Main {

    private static final String BULLETIN_BOARD_URL = "http://www.pfvr.ni.ac.rs/oglasna.php";
    private static final String NEWS_URL = "http://www.pfvr.ni.ac.rs/vesti.php?oblast=opste";

    public static void main(String[] args) {
        String initLog = "Scraper has: %s.";
        log.info(String.format(initLog, "Started"));

        String scrapeLog = "Scraping data on %s : %s";
        log.info(String.format(scrapeLog, BULLETIN_BOARD_URL, "Started"));
        scrapeBulletinBoard();
        log.info(String.format(scrapeLog, BULLETIN_BOARD_URL, "Finished"));

        log.info(String.format(scrapeLog, NEWS_URL, "Started"));
        scrapeNews();
        log.info(String.format(scrapeLog, NEWS_URL, "Finished"));

        log.info(String.format(initLog, "Finished"));
    }

    private static void scrapeNews() {
        WebScraper webScraper2 = new WebScraper();
        webScraper2.setUrl(NEWS_URL);
        webScraper2.setClassName("big_providno");
        MailNotifier mailNotifier2 = new MailNotifier();
        List<News> recentNewsList = mailNotifier2.getNotifierController()
                                                 .getNewsOfInterest(webScraper2.getNews());
        mailNotifier2.notify(recentNewsList);
    }

    private static void scrapeBulletinBoard() {
        WebScraper webScraper = new WebScraper();
        webScraper.setUrl(BULLETIN_BOARD_URL);
        webScraper.setClassName("leva1 kvadratic l2top");
        MailNotifier mailNotifier = new MailNotifier();
        List<News> recentNewsList = mailNotifier.getNotifierController()
                                                .getNewsOfInterest(webScraper
                                                                           .getFromBulletinBoard());
        mailNotifier.notify(recentNewsList);
    }
}