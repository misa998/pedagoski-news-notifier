package com.misa.dao.exceptions;

public class FailedToScraperTheUrlException extends RuntimeException {

    public FailedToScraperTheUrlException(String message, Throwable cause) {
        super(message, cause);
    }


    public FailedToScraperTheUrlException(Throwable e) {
        super(e);
    }
}
