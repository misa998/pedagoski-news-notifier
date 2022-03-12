package com.misa.notify;

import com.misa.util.GetApplicationProperties;

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
        props.put(MAIL_SMTP_AUTH, GetApplicationProperties.get(MAIL_SMTP_AUTH));
        props.put(MAIL_SMTP_STARTTLS_ENABLE, GetApplicationProperties.get(MAIL_SMTP_STARTTLS_ENABLE));
        props.put(MAIL_SMTP_HOST, GetApplicationProperties.get(MAIL_SMTP_HOST));
        props.put(MAIL_SMTP_SSL_ENABLE, Boolean.valueOf(GetApplicationProperties.get(MAIL_SMTP_SSL_ENABLE)));
        props.put(MAIL_SMTP_PORT, GetApplicationProperties.get(MAIL_SMTP_PORT));
        return props;
    }
}