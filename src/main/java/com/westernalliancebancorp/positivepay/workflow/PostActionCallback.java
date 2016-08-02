package com.westernalliancebancorp.positivepay.workflow;

/**
 * PostActionCallback is
 *
 * @author Giridhar Duggirala
 */

public interface PostActionCallback {
    boolean executePostActionCallback(CallbackContext callbackContext) throws CallbackException;
}
