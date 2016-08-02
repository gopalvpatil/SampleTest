package com.westernalliancebancorp.positivepay.service;

/**
 * PositivePayFtpPollingServiceException is
 *
 * @author Giridhar Duggirala
 */

public class PositivePayFtpPollingServiceException extends Exception {
    public PositivePayFtpPollingServiceException(String message) {
        super(message);
    }

    public PositivePayFtpPollingServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PositivePayFtpPollingServiceException(Throwable cause) {
        super(cause);
    }
}
