package com.misa.entity;

import java.math.BigDecimal;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Recipient {
    private BigDecimal id;
    private String address;
    private int active;

    public InternetAddress getInternetAddress() throws AddressException {
        return new InternetAddress(address);
    }
}
