package com.misa.notify;

import com.misa.dao.RecipientDAOImpl;
import com.misa.entity.News;
import com.misa.entity.Recipient;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class MailNotifier {

    public static final String SENDER_ADDRESS = "info.pfvr@gmail.com";

    @Getter
    private final NotifierController notifierController;

    public MailNotifier() {
        this.notifierController = new NotifierController();
    }

    public void notify(List<News> recentNewsList) {
        if (recentNewsList == null || recentNewsList.isEmpty())
            return;

        for (News news : recentNewsList) {
            send(news);
        }
    }

    private void send(News news) {
        MailCredentials creds = MailCredentials.builder()
                                               .username("info.pfvr@gmail.com")
                                               .password("infopfvrvestioglasi")
                                               .build();

        Session session = getSession(creds, new MailParameters());
        try {
            String emailLog = "Sending email from %s : %s";
            log.info(String.format(emailLog, creds.getUsername(), "Started"));

            Transport.send(getMessageForNews(news, session));

            log.info(String.format(emailLog, creds.getUsername(), "Finished"));
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

        Multipart multipart = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(news.getContent(), "text/html; charset=utf-8");
        multipart.addBodyPart(bodyPart);
        message.setContent(multipart);

        return message;
    }

    private String getTitleSubstring(String title) {
        Objects.requireNonNull(title);
        if (title.length() < 35) {
            return title;
        } else {
            return title.substring(0, 30).concat("...");
        }
    }

    private String getFromAddress() {
        return SENDER_ADDRESS;
    }

    private Session getSession(MailCredentials creds, MailParameters config) {
        return Session.getInstance(config.getMailProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(creds.getUsername(), creds.getPassword());
                    }
                });
    }


    private Address[] getRecipientAddresses() {
        try {
            List<Recipient> recipients = fetchAllActiveRecipients();
            Address[] addresses = new Address[recipients.size()];
            for (int i = 0; i < recipients.size(); i++) {
                addresses[i] = InternetAddress.parse(recipients.get(i).getAddress())[0];
            }

            return addresses;
        } catch (AddressException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<Recipient> fetchAllActiveRecipients() {
        return new RecipientDAOImpl().getAll().stream()
                                     .filter(recipient -> recipient.getActive() == 1)
                                     .collect(Collectors.toList());
    }
}