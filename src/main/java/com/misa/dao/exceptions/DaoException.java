package com.misa.dao.exceptions;

public class DaoException extends RuntimeException {

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }


    public DaoException(Throwable e) {
        super(e);
    }
}
