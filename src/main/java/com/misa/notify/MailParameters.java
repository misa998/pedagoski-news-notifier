package com.misa.notify;

import com.misa.util.GetProperties;

import java.util.Properties;

import lombok.Getter;

@Getter
public class MailParameters {

    public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    public static final String MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String MAIL_SMTP_SSL_ENABLE = "mail.smtp.ssl.enable";
    public static final String MAIL_SMTP_PORT = "mail.smtp.port";

    public Properties getMailProperties() {
        Properties props = new Properties();
        props.put(MAIL_SMTP_AUTH, GetProperties.get(MAIL_SMTP_AUTH));
        props.put(MAIL_SMTP_STARTTLS_ENABLE, GetProperties.get(MAIL_SMTP_STARTTLS_ENABLE));
        props.put(MAIL_SMTP_HOST, GetProperties.get(MAIL_SMTP_HOST));
        props.put(MAIL_SMTP_SSL_ENABLE, Boolean.valueOf(GetProperties.get(MAIL_SMTP_SSL_ENABLE)));
        props.put(MAIL_SMTP_PORT, GetProperties.get(MAIL_SMTP_PORT));
        return props;
    }
}