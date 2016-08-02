package com.westernalliancebancorp.positivepay.service;

/**
 * PositivePayDuplicateFileFoundException is
 *
 * @author Giridhar Duggirala
 */

public class PositivePayDuplicateFileFoundException extends Exception {
    public PositivePayDuplicateFileFoundException(String message) {
        super(message);
    }

    public PositivePayDuplicateFileFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PositivePayDuplicateFileFoundException(Throwable cause) {
        super(cause);
    }
}
