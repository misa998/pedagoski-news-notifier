package com.misa.dao.exceptions;

public class PropertyNotFoundByKeyException extends RuntimeException {

    public PropertyNotFoundByKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyNotFoundByKeyException(String message) {
        super(message);
    }


    public PropertyNotFoundByKeyException(Throwable e) {
        super(e);
    }
}
