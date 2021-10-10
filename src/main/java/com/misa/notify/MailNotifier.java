package com.misa.notify;

import com.misa.dao.RecipientDAOImpl;
import com.misa.entity.News;
import com.misa.entity.Recipient;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.stream.Collectors;

@Log4j
public class MailNotifier {

    @Getter
    private final NotifierController notifierController;

    public MailNotifier() {
        this.notifierController = new NotifierController();
    }

    public void notify(List<News> recentNewsList) {
        if (recentNewsList == null || recentNewsList.isEmpty())
            return;

        log.info("Notifying by group mail.");
        for (News news : recentNewsList) {
            send(news);
        }
    }

    private void send(News news) {
        log.info("Sending mail for news_id " + news.getNewsId());

        MailCredentials creds = MailCredentials.builder()
                .username("info.pfvr@gmail.com").password("infopfvrvestioglasi")
                .build();

        Session session = getSession(creds, new MailConfig());
        try {
            log.debug("Sending mail from " + creds.getUsername());
            Transport.send(getMessageForNews(news, session));
            log.info("Mail sent.");
        } catch (MessagingException e) {
            log.error("Sending mail failed. Check antivirus, firewall or email settings.", e);
            throw new RuntimeException(e);
        }
    }

    private Message getMessageForNews(News news, Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(getFromAddress()));
        message.addRecipients(Message.RecipientType.BCC, getRecipientAddresses());
        message.setSubject(getTitleSubstring(news.getTitleString()), "UTF-8");
        message.setText(news.getTitleString() + "\n" + news.getLink());

        return message;
    }

    private String getTitleSubstring(String title) {
        try {
            return title.substring(0, 35);
        } catch (IndexOutOfBoundsException e){
            log.error(e);
            return title;
        }
    }

    private String getFromAddress() {
        return "info.pfvr@gmail.com";
    }

    private Session getSession(MailCredentials creds, MailConfig config) {
        return Session.getInstance(config.getMailProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(creds.getUsername(), creds.getPassword());
                    }
                });
    }


    private Address[] getRecipientAddresses() {
        try {
            List<Recipient> recipients = getAllActiveRecipients();
            Address[] addresses = new Address[recipients.size()];
            for(int i=0; i<recipients.size(); i++){
                addresses[i] = InternetAddress.parse(recipients.get(i).getAddress())[0];
            }

            return addresses;
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<Recipient> getAllActiveRecipients() {
        return new RecipientDAOImpl().getAll().stream()
                .filter(recipient -> recipient.getActive() == 1)
                .collect(Collectors.toList());
    }
}