package com.misa.dao.exceptions;

public class ParsingEmailAddressException extends RuntimeException {

    public ParsingEmailAddressException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingEmailAddressException(String message) {
        super(message);
    }


    public ParsingEmailAddressException(Throwable e) {
        super(e);
    }
}