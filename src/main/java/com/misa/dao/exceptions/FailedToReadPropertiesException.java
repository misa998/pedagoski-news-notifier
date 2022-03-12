package com.misa.dao.exceptions;

public class FailedToReadPropertiesException extends RuntimeException {

    public FailedToReadPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }


    public FailedToReadPropertiesException(Throwable e) {
        super(e);
    }
}
