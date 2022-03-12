package com.misa.exceptions;

import com.misa.dao.exceptions.FailedToSendEmailException;

import lombok.extern.log4j.Log4j;

@Log4j
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    public void uncaughtException(Thread t, Throwable e) {
        log.error(e);

        try {
            new ExceptionNotifier(e).handleNotification();
        } catch (FailedToSendEmailException failedToSendEmailException) {
            throw new FailedToSendEmailException("Failed to send email for previous exception on thread: "
                                                         + t.getName());
        }
    }
}