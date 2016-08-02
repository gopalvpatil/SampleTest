package com.westernalliancebancorp.positivepay.workflow;

/**
 * PreActionCallback is
 *
 * @author Giridhar Duggirala
 */

public interface PreActionCallback {
    boolean executePreActionCallback(CallbackContext callbackContext) throws CallbackException;
}
