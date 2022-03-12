package com.misa;

import com.misa.entity.News;
import com.misa.notify.MailNotifier;
import com.misa.notify.NotifierController;
import com.misa.scraper.WebScraper;
import com.misa.util.GetApplicationProperties;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.log4j.Log4j;

@Log4j
public class ApplicationMain implements Runnable{
    public final List<News> recentNewsList = new ArrayList<>();
    private static final String BULLETIN_BOARD_URL = "http://www.pfvr.ni.ac.rs/oglasna.php";
    private static final String NEWS_URL = "http://www.pfvr.ni.ac.rs/vesti.php?oblast=opste";
    private final MailNotifier mailNotifier = new MailNotifier();
    private NotifierController notifierController;

    @Override
    public void run() {
        scrape();
        notifyRecipients();
    }

    private void scrape() {
        long startTime = System.currentTimeMillis();
        String initLog = "Scraper has: %s.";
        log.info(String.format(initLog, "Started"));

        String scrapeLog = "Scraping data on %s : %s";
        log.info(String.format(scrapeLog, BULLETIN_BOARD_URL, "Started"));
        scrapeBulletinBoard();
        log.info(String.format(scrapeLog, BULLETIN_BOARD_URL, "Finished"));

        log.info(String.format(scrapeLog, NEWS_URL, "Started"));
        scrapeNews();
        log.info(String.format(scrapeLog, NEWS_URL, "Finished"));

        long endTime = System.currentTimeMillis();
        log.info(String.format(initLog, "Finished"));
        log.debug(String.format("It took %s seconds to run the scraper.", (endTime - startTime) / 1000.0));
    }

    private void scrapeNews() {
        WebScraper webScraper2 = new WebScraper();
        webScraper2.setUrl(NEWS_URL);
        webScraper2.setClassName("big_providno");

        List<News> scrapedNews = webScraper2.getNews();
        notifierController = new NotifierController(scrapedNews);
        recentNewsList.addAll(notifierController.getNewsOfInterest());
    }

    private void scrapeBulletinBoard() {
        WebScraper webScraper = new WebScraper();
        webScraper.setUrl(BULLETIN_BOARD_URL);
        webScraper.setClassName("leva1 kvadratic l2top");

        List<News> scrapedNews = webScraper.getFromBulletinBoard();
        notifierController = new NotifierController(scrapedNews);
        recentNewsList.addAll(notifierController.getNewsOfInterest());
    }

    private void notifyRecipients() {
        mailNotifier.notifyRecipients(recentNewsList);
    }
}
