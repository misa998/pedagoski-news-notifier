package com.misa.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Recipient {
    private int id;
    private String address;
    private int active;

    public InternetAddress getInternetAddress() throws AddressException {
        return new InternetAddress(address);
    }
}
