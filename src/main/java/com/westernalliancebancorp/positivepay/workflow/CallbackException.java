package com.westernalliancebancorp.positivepay.workflow;

/**
 * Created with IntelliJ IDEA.
 * User: gduggirala
 * Date: 2/4/14
 * Time: 7:35 AM
 */
public class CallbackException extends Exception {

    public CallbackException(String message) {
        super(message);
    }

    public CallbackException(String message, Throwable cause) {
        super(message, cause);
    }

    public CallbackException(Throwable cause) {
        super(cause);
    }

    public CallbackException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        //super(message, cause, enableSuppression, writableStackTrace);
    }
}
