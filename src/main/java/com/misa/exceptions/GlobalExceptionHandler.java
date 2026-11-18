package com.misa.exceptions;

import com.misa.dao.exceptions.FailedToSendEmailException;

import lombok.extern.log4j.Log4j;

/**
 * <p>This class is used to catch any uncaught exceptions, so that information can be
 * sent to an email address.</p>
 *
 * @see com.misa.Main
 * @author milos.ilic
 * @since 2.1
 */
@Log4j
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {

    /**
     * <p>This method logs the uncaught exception and sends an email notification
     * with the stacktrace content.</p>
     *
     * @param thread - Thread on which the exception occurred.
     * @param throwable - uncaught exception.
     * @throws FailedToSendEmailException - if {@link ExceptionNotifier} fails to send an email.
     */
    public void uncaughtException(Thread thread, Throwable throwable) {
        log.error(throwable);

        try {
            new ExceptionNotifier(throwable).handleNotification();
        } catch (FailedToSendEmailException failedToSendEmailException) {
            throw new FailedToSendEmailException("Failed to send email for previous exception on thread: "
                                                         + thread.getName());
        }
    }
}