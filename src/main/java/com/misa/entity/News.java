package com.misa.entity;

import lombok.*;
import org.apache.commons.codec.binary.StringUtils;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class News {

    private int id;
    private int newsId;
    private byte[] title;
    private String link;
    private LocalDateTime dateTime;

    public News() {
        dateTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "News{" +
                "id=" + id +
                ", newsId=" + newsId +
                ", title='" + getTitleString() + '\'' +
                ", link='" + link + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }

    public String getTitleString() {
        return StringUtils.newStringUtf8(title);
    }
}
