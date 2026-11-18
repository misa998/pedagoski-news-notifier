package com.misa.dao.exceptions;

public class UrlDoesntMatchFormatException extends RuntimeException {

    public UrlDoesntMatchFormatException(String message, Throwable cause) {
        super(message, cause);
    }


    public UrlDoesntMatchFormatException(Throwable e) {
        super(e);
    }

    public UrlDoesntMatchFormatException(String message) {
        super(message);
    }
}
