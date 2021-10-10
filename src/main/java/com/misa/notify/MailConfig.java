package com.misa.notify;

import com.misa.util.GetProperties;
import lombok.Getter;

import java.util.Properties;

@Getter
public class MailConfig {
    public Properties getMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", GetProperties.get("mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", GetProperties.get("mail.smtp.starttls.enable"));
        props.put("mail.smtp.host", GetProperties.get("mail.smtp.host"));
        props.put("mail.smtp.ssl.enable", Boolean.valueOf(GetProperties.get("mail.smtp.ssl.enable")));
        props.put("mail.smtp.port", GetProperties.get("mail.smtp.port"));
        return props;
    }
}