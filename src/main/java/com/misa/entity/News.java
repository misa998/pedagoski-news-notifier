package com.misa.entity;

import org.apache.commons.codec.binary.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class News {

    private BigDecimal id;
    private BigDecimal newsId;
    private byte[] title;
    private String link;
    private String content;
    private Timestamp dateTime;

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
