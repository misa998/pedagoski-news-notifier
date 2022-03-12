package com.misa.notify;

import com.misa.dao.RecipientDAOImpl;
import com.misa.dao.exceptions.FailedToSendEmailException;
import com.misa.entity.News;
import com.misa.entity.Recipient;

import java.util.List;
import java.util.stream.Collectors;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.NonNull;
import lombok.extern.log4j.Log4j;

@Log4j
public class MailNotifier implements IMailNotifier {

    private News curNews;

    public void notifyRecipients(@NonNull List<News> recentNewsList) {
        for (News news : recentNewsList) {
            try {
                String emailLog = "Sending email from %s : %s";
                log.info(String.format(emailLog, getFromAddress(), "Started"));

                this.curNews = news;

                send();
                log.info(String.format(emailLog, getFromAddress(), "Finished"));
            } catch (MessagingException e) {
                throw new FailedToSendEmailException(
                        String.format("Failed to send the email from: %s for news: %s",
                                getFromAddress(), news.getNewsId()), e);
            }
        }
    }

    @Override
    public String getTitle() {
        return getTitleSubstring(curNews.getTitleString());
    }

    @Override
    public String getContent() {
        return curNews.getContent();
    }

    private String getTitleSubstring(@NonNull String title) {
        if (title.length() < 35) {
            return title;
        } else {
            return title.substring(0, 30).concat("...");
        }
    }

    @Override
    public Address[] getRecipientAddresses() {
        try {
            List<Recipient> recipients = fetchAllActiveRecipients();
            Address[] addresses = new Address[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                addresses[i] = InternetAddress.parse(recipients.get(i).getAddress())[0];
            }

            return addresses;
        } catch (AddressException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Recipient> fetchAllActiveRecipients() {
        return new RecipientDAOImpl().getAll().stream()
                                     .filter(recipient -> recipient.getActive() == 1)
                                     .collect(Collectors.toList());
    }
}