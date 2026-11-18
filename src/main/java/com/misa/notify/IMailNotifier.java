package com.misa.notify;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public interface IMailNotifier {

    String SENDER_ADDRESS = "";

    default void send() throws MessagingException {
        MailCredentials creds = MailCredentials.builder()
                                               .username(SENDER_ADDRESS)
                                               .password("")
                                               .build();

        Session session = getSession(creds, new MailParameters());

        Message message = getMessage(session);
        Transport.send(message);
    }

    String getTitle();

    String getContent();

    default Message getMessage(Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(getFromAddress()));
        message.addRecipients(Message.RecipientType.BCC, getRecipientAddresses());
        message.setSubject(getTitle(), "UTF-8");

        Multipart multipart = new MimeMultipart();
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(getContent(), "text/html; charset=utf-8");
        multipart.addBodyPart(bodyPart);
        message.setContent(multipart);

        return message;
    }

    default String getFromAddress() {
        return SENDER_ADDRESS;
    }

    default Session getSession(MailCredentials creds, MailParameters config) {
        return Session.getInstance(config.getMailProperties(),
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(creds.getUsername(), creds.getPassword());
                    }
                });
    }


    Address[] getRecipientAddresses();

}
