package com.misa.dao.exceptions;

import lombok.extern.log4j.Log4j;

@Log4j
public class FailedToSendEmailException extends RuntimeException {

    public FailedToSendEmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToSendEmailException(String message) {
        super(message);
    }


    public FailedToSendEmailException(Throwable e) {
        super(e);
    }

}
