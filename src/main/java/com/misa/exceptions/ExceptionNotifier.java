package com.misa.exceptions;

import com.misa.dao.exceptions.FailedToSendEmailException;
import com.misa.dao.exceptions.ParsingEmailAddressException;
import com.misa.notify.IMailNotifier;

import java.util.Arrays;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * <p>This class is used to notify the {@link #getRecipientAddresses()} about an exception
 * by sending the email with stacktrace content.</p>
 *
 * @since 2.1
 * @author milos.ilic
 * @see GlobalExceptionHandler
 */
@AllArgsConstructor
@Log4j
public class ExceptionNotifier implements IMailNotifier {

    Throwable throwable;

    public void handleNotification() throws FailedToSendEmailException {
        try {
            String emailLog = "Sending email from %s : %s";
            log.info(String.format(emailLog, getFromAddress(), "Started"));

            send();

            log.info(String.format(emailLog, getFromAddress(), "Finished"));
        } catch (MessagingException e) {
            log.error(e);
            throw new FailedToSendEmailException("Failed to notify %s about the previous occurred exception.", e);
        }
    }

    @Override
    public String getTitle() {
        return "Exception has occurred on web scraping";
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getMessage());
        sb.append("<br/><br/>");
        sb.append(throwable.getCause());
        sb.append("<br/><br/>");
        sb.append(Arrays.toString(throwable.getStackTrace()));
        return sb.toString();
    }

    @Override
    public Address[] getRecipientAddresses() {
        Address[] addresses = new Address[1];
        try {
            addresses[0] = InternetAddress.parse("<add subscriber here>")[0];
            return addresses;
        } catch (AddressException e) {
            throw new ParsingEmailAddressException(e);
        }
    }
}
